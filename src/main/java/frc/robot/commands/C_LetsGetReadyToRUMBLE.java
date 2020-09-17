package frc.robot.commands;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class C_LetsGetReadyToRUMBLE extends CommandBase {
  private Joystick joystick;
  private Timer timer;
  private double seconds;
  private double rumbleIntensity;
  public C_LetsGetReadyToRUMBLE(Joystick joystick, double seconds, double rumbleIntensity){
    this.joystick = joystick;
    this.seconds = seconds;
    this.rumbleIntensity = rumbleIntensity;
    timer = new Timer();
  }

  @Override
  public void initialize() {
    timer.reset();
    timer.start();
  }

  @Override
  public void execute() {
    joystick.setRumble(RumbleType.kLeftRumble, rumbleIntensity);
    joystick.setRumble(RumbleType.kRightRumble, rumbleIntensity);
  }

  @Override
  public void end(boolean interrupted) {
    joystick.setRumble(RumbleType.kLeftRumble, 0);
    joystick.setRumble(RumbleType.kRightRumble, 0);
    timer.stop();
  }

  @Override
  public boolean isFinished() {
    return timer.get() >= seconds;
  }
}
