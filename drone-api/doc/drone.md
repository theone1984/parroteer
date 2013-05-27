# Communication with the client

When plugged in, the drone creates a WiFi hotspot that clients can connect to. Using factory defaults, the SSID of the network starts with 'ardrone_' for AR drone 1 and 'ardrone2_' for AR drone 2. When connecting, the drone assigns each client an IP address using its own DHCP server. Typically, the drone itself uses IP address '192.168.1.1' and the first client is assigned '192.168.1.2'.

When connected, the client can communicate with the drone using four different sockets:
* UDP 5556 is used for sending commands to the drone (the so called AT commands)
* TCP 5555 (AR drone 2) or UDP 5555 (AR drone 1) is used for transmitting video data to the client
* UDP 5554 sends navigation data
* TCP 5559 is used for retrieving the drone configuration

There is also an FTP server running on port 5551. You can use this server for retrieving information that you can't get using the sockets only (like finding out the version of the drone).

# Communication in detail

## Data formats

Most of the communication is done using strings. Within these strings, some special data formats are used. When you are sending data to the drone or receive data, be sure to remember the following characteristics.

### Integer values

Each integer value is represented as the sum of unsigned integer values from each byte. Converting a byte array to the desired integer value can thus be done the following way (using Java):

    public static int getIntValue(byte[] data, int offset, int length)
    {
      int tempValue;
      int integerValue = 0;

      for (int i = length - 1; i ]= 0; i--)
      {
        integerValue [[= 8;
        tempValue = data[offset + i] & 0xFF;
        integerValue |= tempValue;
      }
  
      return integerValue;
    }

### Floating point numbers

Floating point numbers are sent and retrieved using the IEEE-754 floating point single format bit layout. Java already comes with a standard implementation of this layout:

    Float.intBitsToFloat(byte[] bytes, int offset, int length);
    Float.floatToIntBits(float value);

### CRC-32 hex strings

Sometimes, you need to transmit 8-characters CRC-32 checksum values. These are hex numbers that MUST be exactly eight characters long. Java provides a way of creating a CRC-32 checksum number given a string value:

    byte bytes[] = value.getBytes();
    Checksum checksumCreator = new CRC32();
    checksumCreator.update(bytes, 0, bytes.length);
    long checkSumValue = checksumCreator.getValue();

You can then use this value to create a string containing the hex numbers:

     String hexString = Long.toHexString(checkSumValue);

But be careful here. The string you get will not necessarily be eight characters long. If it is not, you will  have to fill it using trailing 0 values yourself.

### Checksums

Checksums are calculated:
* Summing up all the byte values in the byte array
* Converting each byte to an unsigned integer

In Java, the sum of all received bytes can be calculated the following way:

    private int calculateCheckSumForReceivedBytes(int checkSumLength)
    {
      int checksum = 0;
      for (int index = 0; index [ bufferLength - checkSumLength; index++)
      {
        checksum += getUnsignedByteValue(buffer[index]);
      }
      return checksum;
    }

To get an unsigned integer value from a byte value, the following method can be used:

    public static int getUnsignedByteValue(byte by)
    {
      return (int) (by & 0xffL);
    }


## The command sender port

You can send simple command strings to the command sender port (UDP 5556).

### Command sequencing

The commands you send must contain a sequence number. The first command you send must use sequence number 1. Every command must increment the sequence number by exactly one, there must not be any gap between sequence numbers. Commands sent out of order (containing a sequence number not expected by the drone) will be ignored.

To stay active, you must send a command to the command port at least every two seconds. If there is nothing to send, you'll have to use a watchdog command (which is explained later) instead. If you don't send a command for this amount of time, you'll have to reset the sequence number and start over with number 1. Otherwise, your commands will be ignored.

### Command structure

Each command has to end with a carriage return character "\r". Newline characters are not supported. Every command starts with 'AT*', contains a unique name that is followed by an equals sign. After the sign, there are some parameters that are separated using commas (and no spaces).

### Commands

#### The watchdog command

This command is used to keep the connection alive. It is best to send a watchdog command every few hundred milliseconds so the drone remains active.

    AT*COMWDG=[SEQ]

Replace [SEQ] with the current sequence number.
Example:

    AT*COMWDG=52

If the last command issued had sequence number 51, then this command upholds the connection.

#### The flight mode command

The flight mode command can be used to start up and land the drone. It is also used for setting and clearing the emergency mode.

    AT*REF=[SEQ],[MODE]

Replace [SEQ] with the current sequence number and [MODE] with a 32-bit integer value representing the current state.

CAUTION: There are only a few supported states that will work. If you set the mode to other values, drone trim values may be changed.

Supported values:
* 290718208: Take off
* 290717696: Land
* 290717952: Set emergency

Example:

    AT*REF=53,290718208

This command tells the drone to take off.

#### The flat trim command

Whenever this command is sent, the drone will take its current gyroscope settings as "standing on even ground" and use these values when trying to hover.

    AT*FTRIM=[SEQ]

Replace [SEQ] with the current sequence number.

#### The flight move command

This command lets the drone move in several directions using four different float values (which must be IEEE-754 encoded).

    AT*PCMD=[SEQ],[ROLL],[PITCH],[GAZ],[YAW]

* Replace [SEQ] with the current sequence number.
* [ROLL] must contain the desired left-right angle ranging from -1.0 (maximum negative angle) to +1.0 (maximum positive angle).
* [PITCH] must contain the desired forward-backward angle (-1.0 to +1.0).
* [GAZ] represents the height increase/decrease velocity (-1.0 to +1.0).
* [YAW] must contain the desired horizontal drift angle (-1.0 to +1.0).

Example:

    AT*PCMD=54,0,-1085485875,0,0

This command contains the following values:
* [SEQ] = 54
* [ROLL] = 0
* [PITCH] = -0.8 (IEEE-754 encoded value)
* [GAZ] = 0
* [YAW] = 0

So the drone will move backwards using an angle of -0.8 times the maximum pitch angle.

#### The control mode command

This command is doing various things.

    AT*CTRL=[SEQ],[MODE],0

Replace [SEQ] with the current sequence number and [MODE] with the desired mode.

There are a few modes that we will need to set:
* Value 5 will reset the control received flag in the navigation data state (explained later)
* Value 4 will trigger the drone to resend the configuration (using the drone configuration port)

The actions that are triggered by this command will be explained in more detail later on.

Example:

    AT*CTRL=55,4,0

Using this command, the drone will resend the configuration to the configuration data port (since [MODE] = 4).

#### The set config value command

This command can be used to change drone configuration settings.

    AT*CONFIG=[SEQ],"[KEY]","[VALUE]"

Replace [SEQ] with the current sequence number, [KEY] with the configuration setting you want to change and [VALUE] with the configuration value you want to set.

Example:

     AT*CONFIG=56,"custom:sessionId","affeaffe"

This command sets the session ID config value to "affeaffe".

In multi configuration mode (explained later), before each set config command, the session ID, profile ID and application ID to use (explained later) must be sent:

    AT*CONFIG_IDS=[SEQ],"[SESSION_ID]","[PROFILE_ID]","[APPLICATION_ID]"

Replace [SEQ] with the current sequence number, [SESSION_ID] with the currently used session ID (which must be an 8-character hex string containing a CRC-32 checksum), [PROFILE_ID] / [APPLICATION_ID] with the currently used profile/application ID  (8-character CRC-32 hex strings).

Example:

    AT*CONFIG_IDS=57,"affeaffe","abcdef12","789abcde"

This command tells us that the next AT*CONFIG command will use session ID "affeaffe", profile ID "abcdef12" and application ID "789abcde".

## The navigation data port

The navigation data represents the current drone state. It can be determined by querying the navigation data port (UDP 5554).

### Keep-alive packets

To get navigation data from this port, you'll frequently have to send keep alive data packets to this port (a 4-byte packet representing integer value 1 will do). It is best to send this packet whenever:
* you got a nav data packet
* a read timeout occurred

### Navigation data structure

The incoming data is structured the following way:
* First, a header section arrives
* After the header section, various other section may be sent
* The last 8 bits contain a checksum value so you can check the integrity of the data sent

#### Navigation data header

Navigation data always starts with a navigation data header. The header is always 16 bytes long and contains the following values:
* an identifier (4 bytes)
* a state bit mask (4 bytes)
* a sequence number (4 bytes)
* an AR state bit mask (4 bytes)

The identifier must always equal value 0x55667788 (octal notation). If this is not the case, it is considered best practice to throw away the data.

The state mask contains 31 boolean values (bit 13 is not used):

* flying (bit 0)
* video enabled (bit 1)
* vision enabled (bit 2)
* control algorithm (bit 3): angular speed control if set, euler angles control otherwise
* altitude control active (bit 4)
* user feedback on (bit 5)
* control received (bit 6)
* trim received (bit 7)
* trim running (bit 8)
* trim succeeded (bit 9)
* nag data demo only (bit 10)
* nav data bootstrap (bit 11)
* motors down (bit 12)
* gyrometers down (bit 14)
* battery too low (bit 15)
* battery too high (bit 16)
* timer elapsed (bit 17)
* not enough power (bit 18)
* angles out of range (bit 19)
* too much wind (bit 20)
* ultrasonic sensor deaf (bit 21)
* cutout system detected (bit 22)
* pic version number OK (bit 23)
* AT coded thread on (bit 24)
* nav data thread on (bit 25)
* video thread on (bit 26)
* acquisition thread on (bit 27)
* control watchdog delayed (bit 28)
* Adc watchdog delayed (bit 29)
* communication problem occurred (bit 30)
* emergency (bit 31)

The sequence number has NO connection to the sequence number sent to the command sender port. It is just a running number.

The AR state bit mask is not described here.

### Navigation data tags

Following the header, the navigation data is split into sections identified by tags. Each section starts with a 4 byte header containing:
* a section tag identifier (2 bytes)
* the length of the section (2 bytes)

The length includes the section header.

#### Section navigation data

This section has identifier value 0x0000. It contains:

* Battery level (4 byte integer value, ranging from 0 to 100)
* Current pitch value (4 byte IEEE-754 encoded float value ranging from -1000.0 (maximum negative angle) to +1000.0)
* Current roll value (same characteristics as pitch)
* Current yaw value (same characteristics as pitch)
* Current altitude (4 byte IEEE-754 encoded float value representing the height in millimeters)
* x-axis speed (4 byte IEEE-754 encoded float value)
* y-axis speed (4 byte IEEE-754 encoded float value)
* z-axis speed (4 byte IEEE-754 encoded float value)

#### Section checksum

This section has identifier 0xffff. It contains a 4 byte checksum.

To check the integrity of the navigation data, simply calculate the sum of all received byte values (as unsigned integers) and compare this value with the checksum received. If they match, then the navigation data is fine.

## The configuration data port

The configuration data represents the current drone configuration. It can be determined by querying the configuration data port (UDP 5559).

When the user requests the drone configuration (see later), the current configuration can be determined using this port.

Drone configuration can be determined as a multiline string. Each line contains one configuration entry. Key and value are split by the equals sign. So the configuration syntax is:

    [KEY] = [VALUE]

The values read here can also be set using the set config value command.

Further information for each of the configuration values can be found in the official developer guide.

## The video data port

A video stream can be obtained using the video data port (UDP 5555 when using AR drone 1, TCP 5555 when using AR drone 2).

### Keep-alive packets

When using UDP, you'll frequently have to send keep alive data packets to this port (a 4-byte packet representing integer value 1 will do). It is best to send this packet whenever:
* you got a nav data packet
* a read timeout occurred

### Codecs

Different codecs can be used for transmitting the data:
* P264 (older codec, mainly used for AR drone 1)
* H264-alike (new codec, relatively standard)

For Java, there are standard implementations decoding each of the codecs:
* For P264, have a look at the [JavaDrone project](https://code.google.com/p/javadrone/source/browse/src/com/codeminders/ardrone/video/BufferedVideoImage.java?r=d54d80caf17b33ee18f75031e63669bb5758eb96)
* For H264, have a lookt at the [ARDroneForP5 project](https://github.com/shigeodayo/ARDroneForP5/blob/master/src/com/shigeodayo/ardrone/video/VideoManager2.java)

The details of each codec are too complex to be explained in a few sentences. Further information can be found in the official developer guide.

## The FTP server

The drone uses an FTP server at port 5551. You can connect to this server anonymously and get file contents. The file index is deactivated.

# Putting it all together

Now that we know about the details concerning each of the ports, we'll have to put all these things together to make them work.

## Setting configuration values

To set a configuration value, it is not sufficient to just send the AT*CONFIG command. First, you'll have to reset the navdata control received flag (using the AT*CTRL command with mode 5, see above). Then, you'll have to wait for the nav data control received flag (bit number 6 of the navigation data state, see above) to become false. When this is done you can then submit the AT*CONFIG command itself. This command will set the control received flag again. So finally, you'll have to wait for the nav data flag to become true again.

To set the value of "video:video_channel" to "4", you'll have to do the following (assuming the last sequence number sent was 50):

    AT*CTRL=51,5,0
    // Wait for nav data control received flag to become false
    AT*CONFIG=52,"video:video_channel","4"
    // Wait for nav data control received flag to become true 

## Multiconfiguration

Starting with firmware version 1.6.4, some of the configuration settings cannot be set by sending the AT*CONFIG command anymore. You'll have to log in using three different CRC-32 checksum hex strings:
* A session ID determining the current session
* A profile ID determining the current user
* An application ID determining the application used

While profile and application ID are relatively static, the session ID should be a pseudo-random value calculated each time the application restarts.

Each config command must also be prefixed with the AT*CONFIG_IDS command using the same session, profile and application ID. 

To login, you'll have to do the following:
* Set session ID to the desired session ID using a set config command (including the AT*CONFIG_IDS command)
* Set profile ID to the desired profile ID using a set config command (including the AT*CONFIG_IDS command)
* Set application ID to the desired application ID using a set config command (including the AT*CONFIG_IDS command)

Assuming session ID shall be "affeaffe", profile ID shall be "12345678" and application ID shall be "affe1234", the complete login sequence is (last sequence number sent was 50):

    AT*CTRL=51,5,0
    // Wait for nav data control received flag to become false
    AT*CONFIG_IDS=52,"affeaffe","12345678","affe1234"
    AT*CONFIG=53,"custom:session_id","affeaffe"
    // Wait for nav data control received flag to become true

    AT*CTRL=54,5,0
    // Wait for nav data control received flag to become false
    AT*CONFIG_IDS=55,"affeaffe","12345678","affe1234"
    AT*CONFIG=56,"custom:profile_id","12345678"
    // Wait for nav data control received flag to become true 

    AT*CTRL=57,5,0
    // Wait for nav data control received flag to become false
    AT*CONFIG_IDS=58,"affeaffe","12345678","affe1234"
    AT*CONFIG=59,"custom:application_id","affe1234"
    // Wait for nav data control received flag to become true

Now, when setting another configuration option, you'll have to do the following:

    AT*CTRL=124,5,0
    // Wait for nav data control received flag to become false
    AT*CONFIG_IDS=125,"affeaffe","12345678","affe1234"
    AT*CONFIG=126,"[KEY]","[VALUE]"
    // Wait for nav data control received flag to become true

## Enabling navigation data

When starting up, the drone is in a state where only the navigation data header and the checksum is sent. To enable navigation data sending, you'll have to set the option value "general:navdata_demo" to "TRUE".

Assuming that the last sequence number was 123, this can be done the following way (like setting any other configuration value):

    AT*CTRL=124,5,0
    // Wait for nav data control received flag to become false
    AT*CONFIG_IDS=125,"affeaffe","12345678","affe1234"
    AT*CONFIG=126,"general:navdata_demo","TRUE"
    // Wait for nav data control received flag to become true

## Getting drone configuration data

To get drone data from the configuration data port, you'll have to do the following things:
* Reset the control received nav data flag using the AT*CTRL command with mode 5 (see above)
* Wait for the control nav data flag to become false
* Send a get command asking for the drone configuration using the AT*CTRL command with mode 4 (see above)
* Read the configuration from the drone configuration port (the socket has to be connected before sending the commands)

Example:

    AT*CTRL=124,5,0 
    // Wait for nav data control received flag to become false
    AT*CTRL=125,4,0
    // Read configuration from drone configuration port

## Getting the drone version

To determine whether the drone is an ARDrone 1 or an ARDrone 2, just connect to the FTP server (port 5551) and read the file "version.txt". The major version ("2" for version "2.1.3") corresponds to the drone number.

Example:
ftp://192.168.1.1:5551/version.txt

## Video codecs and video channels

To set the video codec, you'll have to set the drone configuration setting "video:video_codec".
The following values are typically used:
* 64: P264 codec (320 x 240)
* 129: H264 codec with resolution 360P (640 x 360)
* 131: H264 codec with resolution 720P (1280 x 720)

To switch cameras, you'll have to set the drone configuration setting "video:video_channel":
The following values can be used:
* 0: Front facing camera
* 1: Bottom facing camera
* 2: Picture in picture with front facing camera being the larger picture (only available for AR drone 1)
* 3: Picture in picture with the bottom facing camera being the larger picture (only available for AR drone 1)
* 4: Switch to the next available camera mode

## LED and flight animations

Playing LED/flight animations is done by setting config values as well.

### LED animation

You'll have to set the configuration value "control:leds_anim". The value must contain three values:
* The animation number (can be an integer between 0 and 20)
* The frequency in Hz (an IEEE-754 floating point number)
* The duration in seconds (an integer value)

LED animation numbers:

* 0: BLINK_GREEN_RED
* 1: BLING_GREEN
* 2: BLINK_RED
* 3: BLINK_ORANGE
* 4: SNAKE_GREEN_RED
* 5: FIRE
* 6: STANDARD
* 7: RED
* 8: GREEN
* 9: RED_SNAKE
* 10: BLANK
* 11: RIGHT_MISSILE
* 12: LEFT_MISSILE
* 13: DOUBLE_MISSILE
* 14: FRONT_LEFT_GREEN_OTHERS_RED
* 15: FRONT_RIGHT_GREEN_OTHERS_RED
* 16: REAR_LEFT_GREEN_OTHERS_RED
* 17: REAR_RIGHT_GREEN_OTHERS_RED
* 18: LEFT_GREEN_RIGHT_RED
* 19: LEFT_RED_RIGHT_GREEN
* 20:  BLINK_STANDARDs

Example:

    AT*CTRL=124,5,0
    // Wait for nav data control received flag to become false  
    AT*CONFIG_IDS=125,"affeaffe","12345678","affe1234"
    AT*CONFIG=126,"control:leds_anim","2,1085485875,3"

This config command plays:
* LED animation number 2
* at a frequency of 0.8 Hz (floating point value corresponding to value 1085485875)
* for three seconds

### FLIGHT animation

You'll have to set the configuration value "control:flight_anim". The value must contain two values:
* The animation number (can be an integer between 0 and 19)
* A timeout value for normal flight mode commands in milliseconds (an integer value)

Flight animation numbers:
* 0: PHI_M30_DEG
* 1: PHI_30_DEG
* 2: THETA_M30_DEG
* 3: THETA_30_DEG
* 4: THETA_20_DEG_YAW_200_DEG
* 5: THETA_20_DEG_YAW_M200_DEG
* 6: TURNAROUND
* 7: TURNAROUND_GODOWN
* 8: YAW_SHAKE
* 9: YAW_DANCE
* 10: PHI_DANCE
* 11: THETA_DANCE
* 12: VZ_DANCE
* 13: WAVE
* 14: PHI_THETA_MIXED
* 15: DOUBLE_PHI_THETA_MIXED
* 16: FLIP_AHEAD
* 17: FLIP_BEHIND
* 18: FLIP_LEFT
* 19: FLIP_RIGHT

Example

    AT*CTRL=124,5,0
    // Wait for nav data control received flag to become false  
    AT*CONFIG_IDS=125,"affeaffe","12345678","affe1234"
    AT*CONFIG=126,"control:flight_anim","2,1000"

This config command plays:
* flight animation number 2
* with a timeout of 1000 ms