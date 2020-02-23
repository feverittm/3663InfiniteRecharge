/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.test;

import org.frcteam2910.common.robot.input.Controller;
import org.frcteam2910.common.robot.input.DPadButton.Direction;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Shooter;

public class C_RPMTuneTest extends CommandBase {

  Controller controller;
  SS_Shooter shooter;
  int currentRPM = 1000;
  boolean pressed;

  public C_RPMTuneTest(Controller controller, SS_Shooter shooter) {
    addRequirements(shooter);
    this.controller = controller;
    this.shooter = shooter;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    shooter.setSpinning(true).updateFromVision(false);
    //shooter.setHoodFar(true);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if(controller.getDPadButton(Direction.UP).get() && !pressed) {
      currentRPM += 50;
      pressed = true;
    } else if(controller.getDPadButton(Direction.DOWN).get() && !pressed) {
      currentRPM -= 50;
      pressed = true;
    } else if(!controller.getDPadButton(Direction.UP).get() && !controller.getDPadButton(Direction.DOWN).get()){
      pressed = false;
    }
    shooter.testSetTargetRPM(currentRPM);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    //shooter.setSpinning(false);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if(controller.getAButton().get()){
      return true;
    }
    return false;
  }
}
