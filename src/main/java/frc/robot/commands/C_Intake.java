package frc.robot.commands;

import org.frcteam2910.common.robot.input.Controller;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Intake;
import frc.robot.subsystems.SS_Intake.IntakeDirection;
import frc.robot.subsystems.SS_Intake.IntakePosition;
import frc.robot.utils.TriggerButton;

public class C_Intake extends CommandBase {
  private SS_Intake intake;
  private TriggerButton leftTriggerButton;

  public C_Intake(SS_Intake intake, Controller controller) {
    this.intake = intake;
    leftTriggerButton = new TriggerButton(controller.getLeftTriggerAxis());
    addRequirements(intake);
  }

  @Override
  public void initialize() {
    intake.setArmPosition(IntakePosition.POSITION_3);
  }

  @Override
  public void execute() {
    if(leftTriggerButton.get()){
      intake.startPickUpMotor(IntakeDirection.OUT);
    }
    else {
      intake.startPickUpMotor(IntakeDirection.IN);
    }
  }

  @Override
  public void end(boolean interrupted) {
    intake.retractIntake();
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}