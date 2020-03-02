package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Feeder;
import frc.robot.subsystems.SS_Feeder.FeedRate;

public class C_ShootAll extends CommandBase {
  private SS_Feeder feeder;
  public C_ShootAll(SS_Feeder feeder) {
    this.feeder = feeder;
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
    return feeder.getPosition() >= feeder.getRevPerFullFeed();
  }
}
