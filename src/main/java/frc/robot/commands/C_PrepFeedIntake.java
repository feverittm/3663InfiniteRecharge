package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Feeder;
import frc.robot.subsystems.SS_Feeder.FeedRate;

public class C_PrepFeedIntake extends CommandBase {

  private SS_Feeder feeder;
  public C_PrepFeedIntake(SS_Feeder feeder) {
    this.feeder = feeder;
  }

  @Override
  public void initialize() {
  }

  @Override
  public void execute() {
    if(!feeder.ballInEntry()){
      feeder.setRPM(FeedRate.INTAKE_PREP);
    }
  }

  @Override
  public void end(boolean interrupted) {
    feeder.setRPM(FeedRate.STOPPED);
  }

  @Override
  public boolean isFinished() {
    return feeder.ballInEntry();
  }
}
