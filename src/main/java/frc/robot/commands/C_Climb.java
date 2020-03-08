
package frc.robot.commands;

import org.frcteam2910.common.robot.input.Controller;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Climber;
import frc.robot.subsystems.SS_Intake;
import frc.robot.subsystems.SS_Intake.IntakePosition;
import frc.robot.utils.TriggerButton;
import frc.robot.RobotContainer;
import frc.robot.commands.C_LetsGetReadyToRUMBLE;

public class C_Climb extends CommandBase {

  private Controller driveController = RobotContainer.getDriveController();
  private Controller operatorController = RobotContainer.getOperatorController();
  private Joystick operatorRumbleJoystick = RobotContainer.getOperatorRumbleJoystick();
  private TriggerButton operatorLeftTrigger = RobotContainer.getOperatorLeftTriggerButton();
  private SS_Climber climber = SS_Climber.getInstance();
  private SS_Intake intake = SS_Intake.getInstance();

  private final double MAX_HEIGHT = 120;
  private double winchStickY;
  private double climberStickY;
  private boolean manualOveride = false;
  private boolean hasRumbled = false;
  
  public C_Climb() {
    addRequirements(climber, intake);
  }

  @Override
  public void initialize() {
    intake.setPosition(IntakePosition.POSITION_1);
    climber.resetHookEncoder();
  }

  @Override
  public void execute() {
    winchStickY = -operatorController.getLeftYAxis().get();
    climberStickY = operatorController.getRightYAxis().get();

    if(!manualOveride){
      if ((climber.getHookPosition() <= 0 && climberStickY <= 0) && climber.getHookPosition() > -MAX_HEIGHT) {
        climber.setHookMotorSpeed(Math.pow(climberStickY, 2) * Math.signum(climberStickY));
      }else{
        climber.setHookMotorSpeed(0);
      }
      // if(climberStickY > 0){
      //   climber.setHookPosition(0);
      // }
      if(climber.getHookPosition() < -MAX_HEIGHT && !hasRumbled){
        new C_LetsGetReadyToRUMBLE(operatorRumbleJoystick, 1.5, 1).schedule();
        hasRumbled = true;
      }
    }else if(manualOveride){
      if(operatorLeftTrigger.get()) {
        climber.setHookMotorSpeed(Math.pow(climberStickY, 2) * Math.signum(climberStickY) / 4);
      } else {
        climber.setHookMotorSpeed(Math.pow(climberStickY, 2) * Math.signum(climberStickY));
      }
    }

    if (operatorController.getYButton().get()) {
      climber.resetHookEncoder();
    }
    if(operatorController.getRightTriggerAxis().get() > 0.5){
      manualOveride = true;
    }else{
      manualOveride = false;
    }

    double retractSpeed = .5;
    if(operatorLeftTrigger.get()) {
      retractSpeed /= 4;
    }
    
    if(operatorController.getBButton().get()){
      climber.setRetractMotorSpeed(retractSpeed);
    }else if (operatorController.getAButton().get()) {
      climber.setRetractMotorSpeed(-retractSpeed);
    } else {
      climber.setRetractMotorSpeed(0);
    }

    climber.setWinchMotorSpeed(Math.pow(winchStickY, 2) * Math.signum(winchStickY));
  }

  @Override
  public void end(boolean interrupted) {
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
