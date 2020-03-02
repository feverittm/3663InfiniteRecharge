package frc.robot.drivers;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSink;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

//Doc from WPI: http://docs.wpilib.org/en/latest/docs/software/vision-processing/introduction/using-multiple-cameras.html

public class DriverCameras {

    private UsbCamera feederCamera;
    private UsbCamera shooterCamera;
    private VideoSink server;

    private ShuffleboardTab cameraTab;

    public DriverCameras(int feederCameraPort, int intakeCameraPort) {
        //initialize cameras
        feederCamera = CameraServer.getInstance().startAutomaticCapture("Feeder camera", feederCameraPort);
        shooterCamera = CameraServer.getInstance().startAutomaticCapture("Shooter camera", intakeCameraPort);
        server = CameraServer.getInstance().getServer();
        server.setSource(feederCamera); //init  to the front camera by default;

        //add the video feed widget
        cameraTab = Shuffleboard.getTab("Camera");
        cameraTab.add("Camera Feed", server.getSource())
            .withWidget(BuiltInWidgets.kCameraStream)
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
                break;
            case SHOOTER:
                server.setSource(shooterCamera);
                break;
        }
    }
}
