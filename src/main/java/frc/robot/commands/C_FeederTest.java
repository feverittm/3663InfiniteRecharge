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

public class C_FeederTest extends CommandBase {
  private SS_Feeder feeder;
  private Controller controller;
  public C_FeederTest(SS_Feeder feeder, Controller controller) {
    this.feeder = feeder;
    this.controller = controller;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if(controller.getAButton().get()){
      feeder.setFeedMode(FeedMode.INTAKE);
    }else if(controller.getBButton().get()){
      feeder.setFeedMode(FeedMode.PRESHOOT);
    }else if(controller.getXButton().get()){
        
    }else if(controller.getYButton().get()){
      feeder.setFeedMode(FeedMode.SHOOT);
    }else{
      feeder.setFeedMode(FeedMode.STOPPED);
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return feeder.isIdle();
  }
}
