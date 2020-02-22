package frc.robot.commands;

import org.frcteam2910.common.robot.input.Controller;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Feeder;
import frc.robot.subsystems.SS_Feeder.FeedMode;

public class C_FeederDefault extends CommandBase {

  private SS_Feeder feeder;
  //private Controller controller;
  public C_FeederDefault(SS_Feeder feeder) {
    //this.controller = controller;
    this.feeder = feeder;
    addRequirements(feeder);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    feeder.setFeedMode(FeedMode.INTAKE);    
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // if(contoller.getAButton().get()){
    //   feeder.setMotorSpeed(0.3);
    // }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    feeder.setFeedMode(FeedMode.STOPPED);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return feeder.isIdle();
  }
}
