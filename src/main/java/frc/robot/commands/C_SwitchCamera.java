package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.drivers.DriverCameras;
import frc.robot.drivers.DriverCameras.CameraFeed;

public class C_SwitchCamera extends CommandBase {

  private DriverCameras cameras;
  private CameraFeed targetCamera;

  public C_SwitchCamera(DriverCameras cameras, CameraFeed targetCamera) {
    this.cameras = cameras;
    this.targetCamera = targetCamera;
  }

  @Override
  public boolean isFinished() {
    cameras.switchCameraFeed(targetCamera);
    return true;
  }
}
