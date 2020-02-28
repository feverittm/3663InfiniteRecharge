package frc.robot.commands;

import org.frcteam2910.common.robot.input.Controller;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Intake;
import frc.robot.subsystems.SS_Intake.IntakePosition;

public class C_Intake extends CommandBase {
  private SS_Intake intake;
  private Controller controller;
  private int MAX_INTAKE_RPM = 3000;
  private int MAX_OUT_TAKE_RPM = 2000;


  public C_Intake(SS_Intake intake, Controller controller) {
    this.intake = intake;
    this.controller = controller;
    addRequirements(intake);
  }

  @Override
  public void initialize() {
    intake.setArmPosition(IntakePosition.FULLY_EXTENDED);
  }

  @Override
  public void execute() {
    
    if(controller.getXButton().get()){
      intake.setPickupMotorSpeed(-MAX_OUT_TAKE_RPM);
    }else{
      intake.setPickupMotorSpeed(controller.getRightTriggerAxis().get() * MAX_INTAKE_RPM);
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