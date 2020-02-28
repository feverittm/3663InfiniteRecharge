/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class C_LetsGetReadyToRUMBLE extends CommandBase {
  /**
   * Creates a new C_Rumble.
   */
  private Joystick joystick;
  private Timer timer;
  private double seconds;
  private double rumbleIntensity;
  public C_LetsGetReadyToRUMBLE(Joystick joystick, double seconds, double rumbleIntensity){
    this.joystick = joystick;
    this.seconds = seconds;
    this.rumbleIntensity = rumbleIntensity;
    timer = new Timer();
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    timer.reset();
    timer.start();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    joystick.setRumble(RumbleType.kLeftRumble, rumbleIntensity);
    joystick.setRumble(RumbleType.kRightRumble, rumbleIntensity);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    joystick.setRumble(RumbleType.kLeftRumble, 0);
    joystick.setRumble(RumbleType.kRightRumble, 0);
    timer.stop();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return timer.get() >= seconds;
  }
}
