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

    public DriverCameras(int feederCameraPort, int shooterCameraPort) {
        //initialize cameras
        feederCamera = initCamera("Feeder Camera", feederCameraPort);
        shooterCamera = initCamera("Shooter Camera", shooterCameraPort);

        server = CameraServer.getInstance().getServer();
        server.setSource(feederCamera); //init to the feeder camera by default;

        //add the video feed widget
        cameraTab = Shuffleboard.getTab("Camera");
        cameraTab.add("Camera Feed", server.getSource())
            .withWidget(BuiltInWidgets.kCameraStream)
            .withPosition(3, 0)
            .withSize(5, 5);
    }

    private UsbCamera initCamera(String name, int port) {
        UsbCamera camera = CameraServer.getInstance().startAutomaticCapture(name, port);
        camera.setResolution(128, 96); // 96p: 128 X 96, 120p: 160 X 120, 240p: 320 X 240
        camera.setFPS(30);
        return camera;
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
