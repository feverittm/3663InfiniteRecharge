package frc.robot.drivers;

import java.util.Map;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSink;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ComplexWidget;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

//Doc from WPI: http://docs.wpilib.org/en/latest/docs/software/vision-processing/introduction/using-multiple-cameras.html

public class DriverCameras {

    private UsbCamera feederCamera;
    private UsbCamera shooterCamera;
    private VideoSink server;
    private ComplexWidget widget;

    private ShuffleboardTab cameraTab;

    public DriverCameras(int feederCameraPort, int intakeCameraPort) {
        //initialize cameras
        feederCamera = CameraServer.getInstance().startAutomaticCapture("Feeder camera", feederCameraPort);
        shooterCamera = CameraServer.getInstance().startAutomaticCapture("Shooter camera", intakeCameraPort);
        server = CameraServer.getInstance().getServer();
        server.setSource(feederCamera); //init  to the front camera by default;

        //add the video feed widget
        cameraTab = Shuffleboard.getTab("Camera");
        widget = cameraTab.add("Camera Feed", server.getSource())
            .withWidget(BuiltInWidgets.kCameraStream)
            .withProperties(Map.of("Rotation", "NONE"))
            .withPosition(3, 0)
            .withSize(5, 5);
    }

    public enum CameraFeed {
        FEEDER,
        SHOOTER
    }

    /**
     * Switches the video shown on the camera feed widget
     * @param targetCamera the camera to switch to
     */
    public void switchCameraFeed(CameraFeed targetCamera) {
        switch(targetCamera) {
            case FEEDER:
                server.setSource(feederCamera);
                widget.withProperties(Map.of("Rotation", "NONE"));
                break;
            case SHOOTER:
                server.setSource(shooterCamera);
                widget.withProperties(Map.of("Rotation", "HALF"));
                break;
        }
    }
}
