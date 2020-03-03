package frc.robot.test;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Feeder;
import frc.robot.subsystems.SS_Feeder.FeedRate;

public class C_FeederTest extends CommandBase {
  private SS_Feeder feeder;
  private double targetRPM;

  public C_FeederTest(SS_Feeder feeder, double targetRPM) {
    this.feeder = feeder;
    this.targetRPM = targetRPM;
    addRequirements(feeder);
  }

  @Override
  public void initialize() {
  }

  @Override
  public void execute() {
    feeder.setRPM(targetRPM);
  }

  @Override
  public void end(boolean interrupted) {
    feeder.setRPM(FeedRate.STOPPED);
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
