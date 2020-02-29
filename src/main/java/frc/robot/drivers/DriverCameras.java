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

    private UsbCamera frontCamera;
    private UsbCamera backCamera;

    private VideoSink server;

    private SendableChooser cameraOptions;
    private NetworkTableEntry manualModeEntry;
    private ShuffleboardTab cameraTab;

    public DriverCameras() {
        //initialize cameras
        frontCamera = CameraServer.getInstance().startAutomaticCapture("front camera", 0);
        backCamera = CameraServer.getInstance().startAutomaticCapture("back camera", 1);
        server = CameraServer.getInstance().getServer();
        server.setSource(frontCamera); //init  to the front camera by default;

        //create a sendable chooser with the cameras
        cameraOptions = new SendableChooser<SendableCameraWrapper>();
        cameraOptions.setDefaultOption("front camera", frontCamera);
        cameraOptions.addOption("back camera", backCamera);

        //add the video feed widget
        cameraTab = Shuffleboard.getTab("Camera");
        cameraTab.add("Camera Feed", server.getSource())
            .withWidget(BuiltInWidgets.kCameraStream)
            .withPosition(3, 0)
            .withSize(5, 5);

        //add the camera selector widget
        String selectorName = "SelectedCamera";
        cameraTab.add(selectorName, cameraOptions)
            .withWidget(BuiltInWidgets.kComboBoxChooser)
            .withPosition(8, 0)
            .withSize(2, 1);

        //Add the entry listener to the camera selector. Code from: https://github.com/wpilibsuite/allwpilib/issues/843
        SelectorListener selectorListener = new SelectorListener(this);
        NetworkTableInstance.getDefault().getTable("Shuffleboard").getSubTable("Camera").getSubTable(selectorName)
            .addEntryListener("selected", selectorListener, EntryListenerFlags.kUpdate);

        //add a button for manual or automatic selection
        manualModeEntry = cameraTab.add("Manual Mode", DEFAULT_MANUAL)
            .withWidget(BuiltInWidgets.kToggleButton)
            .withPosition(8, 1)
            .withSize(2, 1)
            .getEntry();

        //add listener to manual mode toggle
        manualModeEntry.addListener(new Consumer<EntryNotification>() {
            @Override
            public void accept(EntryNotification manualMode) {
                manualSwitchCameraFeed();
            }
        }, EntryListenerFlags.kUpdate);
    }

    public enum DriveCamera {
        FRONT,
        BACK
    }

    /**
     * Switches the video shown on the camera feed widget
     * @param targetCamera the camera to switch to
     */
    public void switchCameraFeed(DriveCamera targetCamera) {
        if(manualModeEntry.getBoolean(DEFAULT_MANUAL)) {
            return;
        }

        switch(targetCamera) {
            case FRONT:
                server.setSource(frontCamera);
                break;
            case BACK:
                server.setSource(backCamera);
                break;
        }
    }

    /**
     * switch the camera mode to what is selected on the camera selector widget
     */
    private void manualSwitchCameraFeed() {
        if(!manualModeEntry.getBoolean(DEFAULT_MANUAL)) {
            return;
        } 
        System.out.println(cameraOptions.getSelected().toString());
        server.setSource((UsbCamera)cameraOptions.getSelected());
    }

    /**
     * a listener for the camera selector to update the camera feed
     */
    private class SelectorListener implements TableEntryListener {
        DriverCameras cameras;

        private SelectorListener(DriverCameras cameras) {
            this.cameras = cameras;
        }

        @Override
        public void valueChanged(NetworkTable table, String key, NetworkTableEntry entry, NetworkTableValue value, int flags) {
            cameras.manualSwitchCameraFeed();
        }
    }
}
