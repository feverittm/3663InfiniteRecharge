package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.drivers.DriverCameras;
import frc.robot.drivers.DriverCameras.DriveCamera;

public class C_SwitchShooterCamera extends CommandBase {

  private DriverCameras cameras;

  public C_SwitchShooterCamera(DriverCameras cameras) {
    this.cameras = cameras;
  }

  @Override
  public boolean isFinished() {
    cameras.switchCameraFeed(DriveCamera.SHOOTER);
    return true;
  }
}
