/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.test;

import org.frcteam2910.common.robot.input.Controller;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Feeder;
import frc.robot.subsystems.SS_Feeder.FeedMode;

public class C_Shoot extends CommandBase {
  /**
   * Creates a new C_Shoot.
   */
  private SS_Feeder feeder;
  private Controller controller;
  private boolean pressed = false;
  public C_Shoot(SS_Feeder feeder, Controller controller) {
    this.feeder = feeder;
    this.controller = controller;
    addRequirements(feeder);
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {

  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
  if(controller.getAButton().get() && !pressed){
      feeder.setFeedMode(FeedMode.SHOOT);
      pressed = true;
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    feeder.setFeedMode(FeedMode.SHOOT);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return controller.getAButton().get();
  }
}
