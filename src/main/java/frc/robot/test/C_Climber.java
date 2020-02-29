

package frc.robot.test;

import org.frcteam2910.common.robot.input.Controller;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Climber;

public class C_Climber extends CommandBase {

  
  private Controller operaterController;
  private SS_Climber climber;

  private double winchStickY;
  private double climberStickY;

  public C_Climber(SS_Climber climber, Controller operaterController) {
    this.operaterController = operaterController;
    this.climber = climber;
  }

  @Override
  public void initialize() {
  }

  @Override
  public void execute() {
    winchStickY = operaterController.getLeftYAxis().get();
    climberStickY = operaterController.getRightYAxis().get();


    climber.setWinch(Math.pow(winchStickY,2) * Math.signum(winchStickY));
    climber.setHook(Math.pow(climberStickY, 2) * Math.signum(climberStickY));
  }

  @Override
  public void end(boolean interrupted) {
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
