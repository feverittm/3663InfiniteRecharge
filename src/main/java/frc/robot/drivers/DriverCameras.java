package frc.robot.drivers;

import java.util.function.Consumer;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSink;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.EntryNotification;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableValue;
import edu.wpi.first.networktables.TableEntryListener;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.SendableCameraWrapper;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.WidgetType;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

//Doc from WPI: http://docs.wpilib.org/en/latest/docs/software/vision-processing/introduction/using-multiple-cameras.html

public class DriverCameras {

    private final boolean DEFAULT_MANUAL = false; //whether to put the cameras into manual switch mode initially

    private UsbCamera feederCamera;
    private UsbCamera shooterCamera;

    private VideoSink server;

    private SendableChooser cameraOptions;
    private NetworkTableEntry manualModeEntry;
    private ShuffleboardTab cameraTab;

    public DriverCameras() {
        //initialize cameras
        feederCamera = CameraServer.getInstance().startAutomaticCapture("Feeder camera", 0);
        shooterCamera = CameraServer.getInstance().startAutomaticCapture("Shooter camera", 1);
        server = CameraServer.getInstance().getServer();
        server.setSource(feederCamera); //init  to the front camera by default;

        //add the video feed widget
        cameraTab = Shuffleboard.getTab("Camera");
        cameraTab.add("Camera Feed", server.getSource())
            .withWidget(BuiltInWidgets.kCameraStream)
            .withPosition(3, 0)
            .withSize(5, 5);
    }

    public enum DriveCamera {
        FEEDER,
        SHOOTER
    }

    /**
     * Switches the video shown on the camera feed widget
     * @param targetCamera the camera to switch to
     */
    public void switchCameraFeed(DriveCamera targetCamera) {
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
