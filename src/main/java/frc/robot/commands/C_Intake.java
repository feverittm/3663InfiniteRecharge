package frc.robot.commands;

import org.frcteam2910.common.robot.input.Controller;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.SS_Intake;
import frc.robot.subsystems.SS_Intake.IntakeDirection;
import frc.robot.subsystems.SS_Intake.IntakePosition;
import frc.robot.utils.TriggerButton;

public class C_Intake extends CommandBase {
  private SS_Intake intake = SS_Intake.getInstance();
  private Controller driveController = RobotContainer.getDriveController();
  private TriggerButton driveLeftTriggerButton = RobotContainer.getDriveLeftTriggerButton();
  private boolean isRetracting = false;
  private Timer timer = new Timer();

  public C_Intake() {
    addRequirements(intake);
  }

  @Override
  public void initialize() {
    intake.setPosition(IntakePosition.POSITION_2);
  }

  @Override
  public void execute() {
    if(driveLeftTriggerButton.get()){
      intake.setMotor(IntakeDirection.OUT);
    }
    else {
      intake.setMotor(IntakeDirection.IN);
    }

    if(driveController.getXButton().get() && !isRetracting/*!intakeSensor.get() && !isRetracting*/) {
      timer.reset();
      timer.start();
      isRetracting = true;
    }
    if(isRetracting){
      intake.setPosition(IntakePosition.POSITION_0);
      intake.setMotor(IntakeDirection.STOP);
      if(timer.get() > 0.4) {
        intake.setPosition(IntakePosition.POSITION_2);
        if(timer.get() > 0.8){
          timer.stop();
          isRetracting = false;
        }
      }
    }
  }

  @Override
  public void end(boolean interrupted) {
    intake.retract();
    intake.setMotor(IntakeDirection.STOP);
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
