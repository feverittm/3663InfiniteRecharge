package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.RobotContainer;
import frc.robot.drivers.DriverCameras;

public class Robot extends TimedRobot {
  //private final RobotContainer container = new RobotContainer();
  @Override
  public void robotInit() {
    new DriverCameras();
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
  }
}
