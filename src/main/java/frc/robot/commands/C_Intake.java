package frc.robot.commands;

import org.frcteam2910.common.robot.input.Controller;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Intake;
import frc.robot.subsystems.SS_Intake.IntakeDirection;
import frc.robot.subsystems.SS_Intake.IntakePosition;
import frc.robot.utils.TriggerButton;

public class C_Intake extends CommandBase {
  private SS_Intake intake;
  private TriggerButton leftTriggerButton;
  private boolean isRetracting = false;
  private Timer timer;
  private Controller controller;

  public C_Intake(SS_Intake intake, Controller controller) {
    this.intake = intake;
    this.controller = controller;
    leftTriggerButton = new TriggerButton(controller.getLeftTriggerAxis());
    timer = new Timer();
    addRequirements(intake);
  }

  @Override
  public void initialize() {
    intake.setArmPosition(IntakePosition.POSITION_2);
  }

  @Override
  public void execute() {
    if(leftTriggerButton.get()){
      intake.startPickUpMotor(IntakeDirection.OUT);
    }
    else {
      intake.startPickUpMotor(IntakeDirection.IN);
    }

    if(controller.getXButton().get() && !isRetracting/*!intakeSensor.get() && !isRetracting*/) {
      timer.reset();
      timer.start();
      isRetracting = true;
    }
    if(isRetracting){
      intake.setArmPosition(IntakePosition.POSITION_0);
      intake.startPickUpMotor(IntakeDirection.STOP);
      if(timer.get() > 0.4) {
        intake.setArmPosition(IntakePosition.POSITION_2);
        if(timer.get() > 0.8){
          timer.stop();
          isRetracting = false;
        }
      }
    }
  }

  @Override
  public void end(boolean interrupted) {
    intake.retractIntake();
    intake.startPickUpMotor(IntakeDirection.STOP);
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
