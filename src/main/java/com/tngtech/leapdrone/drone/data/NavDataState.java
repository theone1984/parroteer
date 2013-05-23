package com.tngtech.leapdrone.drone.data;

import com.tngtech.leapdrone.drone.data.enums.ControlAlgorithm;

public class NavDataState
{
  private boolean flying;

  private boolean videoEnabled;

  private boolean visionEnabled;

  private ControlAlgorithm controlAlgorithm;

  private boolean altitudeControlActive;

  private boolean userFeedbackOn;

  private boolean controlReceived;

  private boolean trimReceived;

  private boolean trimRunning;

  private boolean trimSucceeded;

  private boolean navDataDemoOnly;

  private boolean navDataBootstrap;

  private boolean motorsDown;

  private boolean gyrometersDown;

  private boolean batteryTooLow;

  private boolean batteryTooHigh;

  private boolean timerElapsed;

  private boolean notEnoughPower;

  private boolean anglesOutOufRange;

  private boolean tooMuchWind;

  private boolean ultrasonicSensorDeaf;

  private boolean cutoutSystemDetected;

  private boolean picVersionNumberOK;

  private boolean atCodedThreadOn;

  private boolean navDataThreadOn;

  private boolean videoThreadOn;

  private boolean acquisitionThreadOn;

  private boolean controlWatchdogDelayed;

  private boolean adcWatchdogDelayed;

  private boolean communicationProblemOccurred;

  private boolean emergency;

  public void setFlying(boolean flying)
  {
    this.flying = flying;
  }

  public boolean isFlying()
  {
    return flying;
  }

  public void setVideoEnabled(boolean videoEnabled)
  {
    this.videoEnabled = videoEnabled;
  }

  public boolean isVideoEnabled()
  {
    return videoEnabled;
  }

  public void setVisionEnabled(boolean visionEnabled)
  {
    this.visionEnabled = visionEnabled;
  }

  public boolean isVisionEnabled()
  {
    return visionEnabled;
  }

  public void setControlAlgorithm(ControlAlgorithm controlAlgorithm)
  {
    this.controlAlgorithm = controlAlgorithm;
  }

  public ControlAlgorithm getControlAlgorithm()
  {
    return controlAlgorithm;
  }

  public void setAltitudeControlActive(boolean altitudeControlActive)
  {
    this.altitudeControlActive = altitudeControlActive;
  }

  public boolean isAltitudeControlActive()
  {
    return altitudeControlActive;
  }

  public void setUserFeedbackOn(boolean userFeedbackOn)
  {
    this.userFeedbackOn = userFeedbackOn;
  }

  public boolean isUserFeedbackOn()
  {
    return userFeedbackOn;
  }

  public void setControlReceived(boolean controlReceived)
  {
    this.controlReceived = controlReceived;
  }

  public boolean isControlReceived()
  {
    return controlReceived;
  }

  public void setTrimReceived(boolean trimReceived)
  {
    this.trimReceived = trimReceived;
  }

  public boolean isTrimReceived()
  {
    return trimReceived;
  }

  public void setTrimRunning(boolean trimRunning)
  {
    this.trimRunning = trimRunning;
  }

  public boolean isTrimRunning()
  {
    return trimRunning;
  }

  public void setTrimSucceeded(boolean trimSucceeded)
  {
    this.trimSucceeded = trimSucceeded;
  }

  public boolean isTrimSucceeded()
  {
    return trimSucceeded;
  }

  public void setNavDataDemoOnly(boolean navDataDemoOnly)
  {
    this.navDataDemoOnly = navDataDemoOnly;
  }

  public boolean isNavDataDemoOnly()
  {
    return navDataDemoOnly;
  }

  public void setNavDataBootstrap(boolean navDataBootstrap)
  {
    this.navDataBootstrap = navDataBootstrap;
  }

  public boolean isNavDataBootstrap()
  {
    return navDataBootstrap;
  }

  public void setMotorsDown(boolean motorsDown)
  {
    this.motorsDown = motorsDown;
  }

  public boolean isMotorsDown()
  {
    return motorsDown;
  }

  public void setGyrometersDown(boolean gyrometersDown)
  {
    this.gyrometersDown = gyrometersDown;
  }

  public boolean isGyrometersDown()
  {
    return gyrometersDown;
  }

  public void setBatteryTooLow(boolean batteryTooLow)
  {
    this.batteryTooLow = batteryTooLow;
  }

  public boolean isBatteryTooLow()
  {
    return batteryTooLow;
  }

  public void setBatteryTooHigh(boolean batteryTooHigh)
  {
    this.batteryTooHigh = batteryTooHigh;
  }

  public boolean isBatteryTooHigh()
  {
    return batteryTooHigh;
  }

  public void setTimerElapsed(boolean timerElapsed)
  {
    this.timerElapsed = timerElapsed;
  }

  public boolean isTimerElapsed()
  {
    return timerElapsed;
  }

  public void setNotEnoughPower(boolean notEnoughPower)
  {
    this.notEnoughPower = notEnoughPower;
  }

  public boolean isNotEnoughPower()
  {
    return notEnoughPower;
  }

  public void setAnglesOutOufRange(boolean anglesOutOufRange)
  {
    this.anglesOutOufRange = anglesOutOufRange;
  }

  public boolean isAnglesOutOufRange()
  {
    return anglesOutOufRange;
  }

  public void setTooMuchWind(boolean tooMuchWind)
  {
    this.tooMuchWind = tooMuchWind;
  }

  public boolean isTooMuchWind()
  {
    return tooMuchWind;
  }

  public void setUltrasonicSensorDeaf(boolean ultrasonicSensorDeaf)
  {
    this.ultrasonicSensorDeaf = ultrasonicSensorDeaf;
  }

  public boolean isUltrasonicSensorDeaf()
  {
    return ultrasonicSensorDeaf;
  }

  public void setCutoutSystemDetected(boolean cutoutSystemDetected)
  {
    this.cutoutSystemDetected = cutoutSystemDetected;
  }

  public boolean isCutoutSystemDetected()
  {
    return cutoutSystemDetected;
  }

  public void setPicVersionNumberOK(boolean picVersionNumberOK)
  {
    this.picVersionNumberOK = picVersionNumberOK;
  }

  public boolean isPicVersionNumberOK()
  {
    return picVersionNumberOK;
  }

  public void setAtCodedThreadOn(boolean atCodedThreadOn)
  {
    this.atCodedThreadOn = atCodedThreadOn;
  }

  public boolean isAtCodedThreadOn()
  {
    return atCodedThreadOn;
  }

  public void setNavDataThreadOn(boolean navDataThreadOn)
  {
    this.navDataThreadOn = navDataThreadOn;
  }

  public boolean isNavDataThreadOn()
  {
    return navDataThreadOn;
  }

  public void setVideoThreadOn(boolean videoThreadOn)
  {
    this.videoThreadOn = videoThreadOn;
  }

  public boolean isVideoThreadOn()
  {
    return videoThreadOn;
  }

  public void setAcquisitionThreadOn(boolean acquisitionThreadOn)
  {
    this.acquisitionThreadOn = acquisitionThreadOn;
  }

  public boolean isAcquisitionThreadOn()
  {
    return acquisitionThreadOn;
  }

  public void setControlWatchdogDelayed(boolean controlWatchdogDelayed)
  {
    this.controlWatchdogDelayed = controlWatchdogDelayed;
  }

  public boolean isControlWatchdogDelayed()
  {
    return controlWatchdogDelayed;
  }

  public void setAdcWatchdogDelayed(boolean adcWatchdogDelayed)
  {
    this.adcWatchdogDelayed = adcWatchdogDelayed;
  }

  public boolean isAdcWatchdogDelayed()
  {
    return adcWatchdogDelayed;
  }

  public void setCommunicationProblemOccurred(boolean communicationProblemOccurred)
  {
    this.communicationProblemOccurred = communicationProblemOccurred;
  }

  public boolean isCommunicationProblemOccurred()
  {
    return communicationProblemOccurred;
  }

  public void setEmergency(boolean emergency)
  {
    this.emergency = emergency;
  }

  public boolean isEmergency()
  {
    return emergency;
  }
}