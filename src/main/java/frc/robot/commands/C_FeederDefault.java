package frc.robot.commands;


import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Feeder;
import frc.robot.subsystems.SS_Feeder.FeedMode;

public class C_FeederDefault extends CommandBase {

  private SS_Feeder feeder;
  private Joystick rumbleJoystick;
  private boolean hasRumbled = false;

  private final double secondsToRumble = 1.5;
  public C_FeederDefault(SS_Feeder feeder, Joystick rumbleJoystick) {
    this.feeder = feeder;
    this.rumbleJoystick = rumbleJoystick;
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
    if(feeder.ballInExit() && !hasRumbled){
      new C_LetsGetReadyToRUMBLE(rumbleJoystick, secondsToRumble, 0.5).schedule();
      hasRumbled = true;
    }
    if(!feeder.ballInExit()){
      hasRumbled = false;
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    feeder.setFeedMode(FeedMode.STOPPED);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
