package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Feeder;
import frc.robot.subsystems.SS_Feeder.FeedRate;

public class C_PrepFeederToShoot extends CommandBase {

  private SS_Feeder feeder = SS_Feeder.getInstance();

  public C_PrepFeederToShoot() {
    addRequirements(feeder);
  }

  @Override
  public void initialize() {
    feeder.resetEncoder();
  }

  @Override
  public void execute() {
    if(!feeder.ballInExit()){
      feeder.setRPM(FeedRate.SHOOT_PREP);
    }
  }

  @Override
  public void end(boolean interrupted) {
    feeder.setRPM(FeedRate.STOPPED);
  }

  @Override
  public boolean isFinished() {
    return feeder.ballInExit() || Math.abs(feeder.getPosition()) >= SS_Feeder.REV_PER_FULL_FEED;
  }
}
