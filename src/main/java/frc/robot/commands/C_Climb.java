
package frc.robot.commands;

import org.frcteam2910.common.robot.input.Controller;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Climber;

public class C_Climb extends CommandBase {

  private Controller operatorController;
  private SS_Climber climber;

  private final double MAX_HEIGHT = 125;
  private double winchStickY;
  private double climberStickY;
  private boolean manualOveride = false;
  public C_Climb(SS_Climber climber, Controller operatorController) {
    this.operatorController = operatorController;
    this.climber = climber;
    addRequirements(climber);
  }

  @Override
  public void initialize() {
  }

  @Override
  public void execute() {
    winchStickY = operatorController.getLeftYAxis().get();
    climberStickY = operatorController.getRightYAxis().get();

    if(!manualOveride){
      if ((climber.getHookPosition() <= 0 && climberStickY <= 0) && climber.getHookPosition() > -MAX_HEIGHT) {
        climber.setHook(Math.pow(climberStickY, 2) * Math.signum(climberStickY));
      }else{
        climber.setHook(0);
      }
      if(climberStickY > 0){
        climber.setHookPosition(0);
      }
    }else if(manualOveride){
      climber.setHook(Math.pow(climberStickY, 2) * Math.signum(climberStickY));
    }

    if (operatorController.getYButton().get()) {
      climber.resetHookEncoder();
    }
    if(operatorController.getStartButton().get()){
      manualOveride = true;
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
