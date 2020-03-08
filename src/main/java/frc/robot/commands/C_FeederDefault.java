package frc.robot.commands;


import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.SS_Feeder;
import frc.robot.subsystems.SS_Feeder.FeedRate;

public class C_FeederDefault extends CommandBase {

  private SS_Feeder feeder = SS_Feeder.getInstance();
  private Joystick rumbleJoystick = RobotContainer.getRumbleJoystick();
  private boolean hasRumbled = false;

  private final double SECONDS_TO_RUMBLE = 1;
  public C_FeederDefault() {
    addRequirements(feeder);
  }
  
  @Override
  public void execute() {
    if(!feeder.ballInExit() && feeder.ballInEntry()){
      feeder.setRPM(FeedRate.INTAKE);
    }else{
      feeder.setRPM(FeedRate.STOPPED);
    }
    checkRumble();
  }

  public void checkRumble(){
    if(feeder.ballInExit() && !hasRumbled){
      new C_LetsGetReadyToRUMBLE(rumbleJoystick, SECONDS_TO_RUMBLE, 0.5).schedule();
      hasRumbled = true;
    }
    if(!feeder.ballInExit()){
      hasRumbled = false;
    }
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
