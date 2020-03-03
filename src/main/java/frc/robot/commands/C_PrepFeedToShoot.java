package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Feeder;
import frc.robot.subsystems.SS_Feeder.FeedRate;

public class C_PrepFeedToShoot extends CommandBase {
  private SS_Feeder feeder;

  public C_PrepFeedToShoot(SS_Feeder feeder) {
    this.feeder = feeder;
    addRequirements(feeder);
  }

  @Override
  public void initialize() {
    if (feeder.ballInExit()) {
      end(false);
    }
  }

  @Override
  public void execute() {
    feeder.setRPM(FeedRate.SHOOT_PREP);
  }

  @Override
  public void end(boolean interrupted) {
    feeder.setRPM(FeedRate.STOPPED);
  }

  @Override
  public boolean isFinished() {
    return feeder.ballInExit();
  }
}
