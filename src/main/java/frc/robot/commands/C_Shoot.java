/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

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
  private boolean startedShot = false;
  public C_Shoot(SS_Feeder feeder, Controller controller) {
    this.feeder = feeder;
    this.controller = controller;
    addRequirements(feeder);
  }

  @Override
  public void initialize() {}

  @Override
  public void execute() { 
    if(controller.getAButton().get() && !startedShot) {
      feeder.setFeedMode(FeedMode.SHOOT);
      startedShot = true;
    }
  }

  @Override
  public void end(boolean interrupted) {
    feeder.setFeedMode(FeedMode.STOPPED);
  }

  @Override
  public boolean isFinished() {
    return feeder.isIdle() && startedShot;
  }
}
