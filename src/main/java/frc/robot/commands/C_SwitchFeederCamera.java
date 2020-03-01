package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.drivers.DriverCameras;
import frc.robot.drivers.DriverCameras.DriveCamera;

public class C_SwitchFeederCamera extends CommandBase {

  private DriverCameras cameras;
  public C_SwitchFeederCamera(DriverCameras cameras) {
    this.cameras = cameras;
  }

  @Override
  public boolean isFinished() {
    cameras.switchCameraFeed(DriveCamera.FEEDER);
    return true;
  }
}
