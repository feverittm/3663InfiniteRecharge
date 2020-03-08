package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Feeder;
import frc.robot.subsystems.SS_Feeder.FeedRate;

public class C_ShootAll extends CommandBase {
  private SS_Feeder feeder = SS_Feeder.getInstance();
  public C_ShootAll() {
    addRequirements(feeder);
  }

  @Override
  public void initialize() {
    feeder.resetEncoder();
  }

  @Override
  public void execute() {
    feeder.setRPM(FeedRate.SHOOT_ALL);
  }

  @Override
  public void end(boolean interrupted) {
    feeder.setRPM(FeedRate.STOPPED);
  }

  @Override
  public boolean isFinished() {
    return feeder.getPosition() >= SS_Feeder.REV_PER_FULL_FEED;
  }
}
