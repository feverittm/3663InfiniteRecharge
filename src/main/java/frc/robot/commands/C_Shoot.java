package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Feeder;
import frc.robot.subsystems.SS_Shooter;
import frc.robot.subsystems.SS_Feeder.FeedRate;

public class C_Shoot extends CommandBase {
  private SS_Feeder feeder = SS_Feeder.getInstance();
  private SS_Shooter shooter = SS_Shooter.getInstance();
  private boolean hasShot = false;

  public C_Shoot() {
    addRequirements(feeder);
  }

  @Override
  public void initialize() {
    feeder.resetEncoder();
    hasShot = false;
  }

  @Override
  public void execute() {
    if(shooter.atCorrectRPM()){

      if (!feeder.ballInExit() && !hasShot) {
        hasShot = true;
      }
      if (feeder.ballInExit() || hasShot) {
        feeder.setRPM(FeedRate.SHOOT_ONE);
      }
      
    }
  }

  @Override
  public void end(boolean interrupted) {
    feeder.setRPM(FeedRate.STOPPED);
  }

  @Override
  public boolean isFinished() {
    return (feeder.ballInExit() && hasShot) || Math.abs(feeder.getPosition()) >= SS_Feeder.REV_PER_FULL_FEED / 2;
  }
}
