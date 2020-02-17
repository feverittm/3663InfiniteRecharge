/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.RobotContainer;

public class Robot extends TimedRobot {
  private final RobotContainer container = new RobotContainer();
  
  private Command autonomousCommand;
  private Command teleopCommand;

  @Override
  public void robotInit() {
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
  }

  @Override
  public void autonomousInit() {
    autonomousCommand = container.getAutonomousCommand();
    // schedule the autonomous command
    if (autonomousCommand != null) {
      autonomousCommand.schedule();
    }
  }

  @Override
  public void teleopInit() {
    //ensure that the autonomus command is stopped for teleop
    if (autonomousCommand != null) {
      autonomousCommand.cancel();
    }

    //schedule the teleop command
    teleopCommand = container.getTeleopCommand();
    if(teleopCommand != null) {
      teleopCommand.schedule();
    }

    container.getTeleopCommand().schedule();
  }
}
