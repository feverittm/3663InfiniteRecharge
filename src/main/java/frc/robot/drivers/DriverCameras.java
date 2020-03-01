package frc.robot.drivers;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class DriverCameras {
    private UsbCamera frontCamera;
    private UsbCamera backCamera;
    private ShuffleboardTab cameraTab;

    public DriverCameras() {
        frontCamera = CameraServer.getInstance().startAutomaticCapture("front camera", 0);
        backCamera = CameraServer.getInstance().startAutomaticCapture("back camera", 1);
        
        cameraTab = Shuffleboard.getTab("Camera");

        cameraTab.add("Front Camera", frontCamera)
            .withWidget(BuiltInWidgets.kCameraStream)
            .withPosition(0, 0)
            .withSize(5, 5);
        cameraTab.add("Back Camera", backCamera)
            .withWidget(BuiltInWidgets.kCameraStream)
            .withPosition(5, 0)
            .withSize(5, 5);
    }
}
