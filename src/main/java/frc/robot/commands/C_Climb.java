
package frc.robot.commands;

import org.frcteam2910.common.robot.input.Controller;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Climber;
import frc.robot.subsystems.SS_Intake;
import frc.robot.subsystems.SS_Intake.IntakePosition;
import frc.robot.commands.C_LetsGetReadyToRUMBLE;

public class C_Climb extends CommandBase {

  private Controller operatorController;
  private Controller driveController;
  private Joystick operatorRumbleJoystick;
  private SS_Climber climber;
  private SS_Intake intake;

  private final double MAX_HEIGHT = 120;
  private double winchStickY;
  private double climberStickY;
  private boolean manualOveride = false;
  private boolean hasRumbled = false;
  public C_Climb(SS_Climber climber, SS_Intake intake, Controller driveController, Controller operatorController, Joystick operatorRumbleJoystick) {
    this.operatorController = operatorController;
    this.driveController = driveController;
    this.operatorRumbleJoystick = operatorRumbleJoystick;
    this.climber = climber;
    this.intake = intake;
    addRequirements(climber, intake);
  }

  @Override
  public void initialize() {
    intake.setArmPosition(IntakePosition.POSITION_1);
    climber.resetHookEncoder();
  }

  @Override
  public void execute() {
    winchStickY = -operatorController.getLeftYAxis().get();
    climberStickY = operatorController.getRightYAxis().get();

    if(!manualOveride){
      if ((climber.getHookPosition() <= 0 && climberStickY <= 0) && climber.getHookPosition() > -MAX_HEIGHT) {
        climber.setHook(Math.pow(climberStickY, 2) * Math.signum(climberStickY));
      }else{
        climber.setHook(0);
      }
      // if(climberStickY > 0){
      //   climber.setHookPosition(0);
      // }
      if(climber.getHookPosition() < -MAX_HEIGHT && !hasRumbled){
        new C_LetsGetReadyToRUMBLE(operatorRumbleJoystick, 1.5, 1).schedule();
        hasRumbled = true;
      }
    }else if(manualOveride){
      climber.setHook(Math.pow(climberStickY, 2) * Math.signum(climberStickY));
    }

    if (operatorController.getYButton().get()) {
      climber.resetHookEncoder();
    }
    if(operatorController.getRightTriggerAxis().get() > 0.5){
      manualOveride = true;
    }else{
      manualOveride = false;
    }
    
    if(operatorController.getBButton().get()){
      climber.setRetractMotorSpeed(0.5);
    }else{
      climber.setRetractMotorSpeed(0);
    }

    climber.setWinch(Math.pow(winchStickY, 2) * Math.signum(winchStickY));
  }

  @Override
  public void end(boolean interrupted) {
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
