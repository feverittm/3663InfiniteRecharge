package frc.robot.drivers;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.SendableCameraWrapper;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

//Doc from WPI: http://docs.wpilib.org/en/latest/docs/software/vision-processing/introduction/using-multiple-cameras.html

public class DriverCameras {

    private UsbCamera frontCamera;
    private UsbCamera backCamera;
    private SendableChooser cameraOptions;
    private ShuffleboardTab cameraTab;

    public DriverCameras() {
        //initialize cameras
        frontCamera = CameraServer.getInstance().startAutomaticCapture("front camera", 0);
        backCamera = CameraServer.getInstance().startAutomaticCapture("back camera", 1);

        //create a sendable chooser with the cameras
        cameraOptions = new SendableChooser<SendableCameraWrapper>();
        cameraOptions.setDefaultOption("front camera", SendableCameraWrapper.wrap(frontCamera));
        cameraOptions.addOption("back camera", SendableCameraWrapper.wrap(backCamera));

        //add the video feed widget
        cameraTab = Shuffleboard.getTab("Camera");
        cameraTab.add("Camera Feed", cameraOptions)
            .withWidget(BuiltInWidgets.kCameraStream)
            .withPosition(3, 0)
            .withSize(5, 5);

        //add the camera selector widget
        cameraTab.add("Camera Selector", cameraOptions)
            .withWidget(BuiltInWidgets.kComboBoxChooser)
            .withPosition(0, 8)
            .withSize(2, 1);
    }
}
