package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.drivers.DriverCameras;
import frc.robot.drivers.DriverCameras.CameraFeed;

public class C_SwitchCamera extends CommandBase {

  private DriverCameras cameras = RobotContainer.getDriverCameras();
  private CameraFeed targetCamera;

  public C_SwitchCamera(CameraFeed targetCamera) {
    this.targetCamera = targetCamera;
  }

  @Override
  public void initialize() {
    cameras.switchCameraFeed(targetCamera);    
  }

  @Override
  public boolean isFinished() {
    return true;
  }
}
