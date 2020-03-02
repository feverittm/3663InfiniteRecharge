package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.RobotContainer;

public class Robot extends TimedRobot {
  private final RobotContainer container = new RobotContainer();
  private CommandBase autonomousCommand;
  @Override
  public void robotInit() {
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
  }

  @Override
  public void autonomousInit() {
    if(autonomousCommand != null) {
      autonomousCommand.cancel();
    }
    autonomousCommand = container.getAutonomousCommand();
    CommandScheduler.getInstance().schedule(false, autonomousCommand);
  }
}
