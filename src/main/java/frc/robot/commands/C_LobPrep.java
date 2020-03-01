package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Feeder;
import frc.robot.subsystems.SS_Shooter;
import frc.robot.subsystems.SS_Feeder.FeedMode;

public class C_LobPrep extends CommandBase {

  SS_Shooter shooter;
  SS_Feeder feeder;

  public C_LobPrep(SS_Shooter shooter, SS_Feeder feeder) {
    addRequirements(shooter, feeder);
    this.shooter = shooter;
    this.feeder = feeder;
  }

  @Override
  public void initialize() {
    feeder.setFeedMode(FeedMode.PRESHOOT);
    shooter.setHoodFar(false).setLobSpeed().setSpinning(true);
  }

  @Override
  public boolean isFinished() {
    return feeder.isIdle();
  }

  @Override
  public void end(boolean interrupted) {
    feeder.setFeedMode(FeedMode.STOPPED);
  }
}
