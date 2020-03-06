package frc.robot.test;

import org.frcteam2910.common.robot.input.Controller;
import org.frcteam2910.common.robot.input.DPadButton.Direction;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Feeder;
import frc.robot.subsystems.SS_Intake;
import frc.robot.subsystems.SS_Shooter;
import frc.robot.subsystems.SS_Feeder.FeedRate;
import frc.robot.subsystems.SS_Intake.IntakePosition;

public class C_RPMTuneTest extends CommandBase {

  Controller controller;
  SS_Feeder feeder;
  SS_Shooter shooter;
  int currentRPM = 3500;
  SS_Intake intake;
  boolean pressed;

  public C_RPMTuneTest(Controller controller, SS_Shooter shooter, SS_Feeder feeder, SS_Intake intake) {
    this.controller = controller;
    this.feeder = feeder;
    this.intake = intake;
    this.shooter = shooter;
    addRequirements(shooter, feeder);
  }

  @Override
  public void initialize() {
    shooter.setSpinning(true).updateFromVision(false).setHoodFar(false);
    intake.setArmPosition(IntakePosition.POSITION_2);
  }

  @Override
  public void execute() {
    if(feeder.getRPM() == 0) {
      feeder.setRPM(FeedRate.SHOOT_ALL);
    }
    if(controller.getDPadButton(Direction.RIGHT).get()) {
      currentRPM += 10;
    } else if (controller.getDPadButton(Direction.LEFT).get()) {
      currentRPM -= 10;
    }
    if(controller.getDPadButton(Direction.UP).get() && !pressed) {
      currentRPM += 10;
      pressed = true;
    } else if(controller.getDPadButton(Direction.DOWN).get() && !pressed) {
      currentRPM -= 10;
      pressed = true;
    } else if(!controller.getDPadButton(Direction.UP).get() && !controller.getDPadButton(Direction.DOWN).get()){
      pressed = false;
    }
    shooter.testSetTargetRPM(currentRPM);
  }

  @Override
  public void end(boolean interrupted) {
  }

  @Override
  public boolean isFinished() {
    if(controller.getAButton().get()){
      return true;
    }
    return false;
  }
}
