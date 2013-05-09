package com.tngtech.leapdrone.ui;

import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.DroneController;

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

    final JPanel panel = new JPanel();
    GridLayout experimentLayout = new GridLayout(0, 2);
    panel.setLayout(experimentLayout);

    panel.add(takeOffButton);
    panel.add(landButton);

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
      default:
        System.out.println(String.format("Don't know what to do with command '%s'", e.getActionCommand()));
    }
  }
}