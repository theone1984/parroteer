package com.tngtech.leapdrone.ui;

import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.DroneController;
import com.tngtech.leapdrone.drone.commands.SwitchCameraCommand;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingWindow implements ActionListener
{
  private final DroneController droneController;

  @Inject
  public SwingWindow(DroneController droneController)
  {
    this.droneController = droneController;
  }

  public void createWindow()
  {
    JFrame frame = new JFrame("Drone control");

    createUIElements(frame);

    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

  private void createUIElements(JFrame frame)
  {
    final JButton takeOffButton = new JButton("Take off");
    takeOffButton.setActionCommand("takeOff");
    takeOffButton.addActionListener(this);

    final JButton landButton = new JButton("Land");
    landButton.setActionCommand("land");
    landButton.addActionListener(this);

    final JButton flatTrimButton = new JButton("Flat trim");
    flatTrimButton.setActionCommand("flattrim");
    flatTrimButton.addActionListener(this);

    final JButton emergencyButton = new JButton("Emergency");
    flatTrimButton.setActionCommand("emergency");
    flatTrimButton.addActionListener(this);

    final JButton switchCameraButton = new JButton("Switch camera");
    switchCameraButton.setActionCommand("switchCamera");
    switchCameraButton.addActionListener(this);

    final VideoPanel videoPanel = new VideoPanel();
    droneController.addVideoDataListener(videoPanel);
    droneController.addNavDataListener(videoPanel);

    final JPanel buttonPanel = new JPanel();
    GridLayout horizontalLayout = new GridLayout(3, 2);
    buttonPanel.setLayout(horizontalLayout);

    final JPanel panel = new JPanel();
    GridLayout verticalLayout = new GridLayout(2, 0);
    panel.setLayout(verticalLayout);

    buttonPanel.add(takeOffButton);
    buttonPanel.add(landButton);
    buttonPanel.add(flatTrimButton);
    buttonPanel.add(emergencyButton);
    buttonPanel.add(switchCameraButton);

    panel.add(buttonPanel);
    panel.add(videoPanel);

    frame.getContentPane().add(panel);
  }

  public void actionPerformed(ActionEvent e)
  {
    switch (e.getActionCommand())
    {
      case "takeOff":
        droneController.takeOff();
        break;
      case "land":
        droneController.land();
        break;
      case "flattrim":
        droneController.flatTrim();
        break;
      case "emergency":
        droneController.emergency();
        break;
      case "switchCamera":
        droneController.switchCamera(SwitchCameraCommand.Camera.BACK);
        break;
      default:
        System.out.println(String.format("Don't know what to do with command '%s'", e.getActionCommand()));
    }
  }
}