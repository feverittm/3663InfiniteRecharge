package frc.robot.drivers;

import java.util.HashMap;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableValue;
import edu.wpi.first.networktables.TableEntryListener;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ComplexWidget;
import edu.wpi.first.wpilibj.shuffleboard.SendableCameraWrapper;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

//Doc from WPI: http://docs.wpilib.org/en/latest/docs/software/vision-processing/introduction/using-multiple-cameras.html

public class OldDriverCameras {

    private UsbCamera frontCamera;
    private UsbCamera backCamera;
    private HashMap<CameraPosition, UsbCamera> cameras;
    private NetworkTableEntry selectedCameraEntry;

    private SelectorListener selectorListener; 
    private SendableChooser cameraSelector;

    private SendableChooser selectedCamera;

    private ShuffleboardTab cameraTab;

    public OldDriverCameras() {
        initCameras();
        initCameraSelector();
    }

    private void initCameras() {
        frontCamera = CameraServer.getInstance().startAutomaticCapture("front camera", 0);
        backCamera = CameraServer.getInstance().startAutomaticCapture("back camera", 1);

        cameras = new HashMap<CameraPosition, UsbCamera>() {};
        cameras.put(CameraPosition.FRONT, frontCamera);
        cameras.put(CameraPosition.BACK, backCamera);

        cameraTab = Shuffleboard.getTab("Camera");

        selectedCameraEntry.setValue(SendableCameraWrapper.wrap(frontCamera));

        cameraTab.add("Video Stream", selectedCameraEntry)
            .withWidget(BuiltInWidgets.kCameraStream)
            .withPosition(3, 0)
            .withSize(5, 5);

        cameraTab.add("Video Stream", frontCamera);
        ComplexWidget widget;
        // cameraWidget = cameraTab.add("Front Camera", frontCamera)
        //     .withWidget(BuiltInWidgets.kCameraStream)
        //     .withPosition(0, 0)
        //     .withSize(5, 5);
        // cameraWidget = cameraTab.add("Back Camera", backCamera)
        //     .withWidget(BuiltInWidgets.kCameraStream)
        //     .withPosition(5, 0)
        //     .withSize(5, 5);

    }

    private void initCameraSelector() {
        //init camera selector and add options to it
        cameraSelector = new SendableChooser<CameraPosition>();
        cameraSelector.setDefaultOption("Front", CameraPosition.FRONT);
        cameraSelector.addOption("Back", CameraPosition.BACK);

        //add the camera selector to the shuffleboard
        String selectorName = "SelectedCamera";
        cameraTab.add(selectorName, cameraSelector)
            .withWidget(BuiltInWidgets.kComboBoxChooser)
            .withPosition(8, 0)
            .withSize(2, 1);

        selectorListener = new SelectorListener(this);
        //Add the entry listener to the. Code from: https://github.com/wpilibsuite/allwpilib/issues/843
        NetworkTableInstance.getDefault().getTable("Shuffleboard").getSubTable("Camera").getSubTable(selectorName)
            .addEntryListener("selected", selectorListener, EntryListenerFlags.kUpdate);
    }

    public enum CameraPosition {
        FRONT,
        BACK
    }

    public void switchCameraFeed() {
        switchCameraFeed((CameraPosition)cameraSelector.getSelected());
    }

    public void switchCameraFeed(CameraPosition camera) {
        selectedCameraEntry.setString(cameras.get(camera).getPath());
    }

    private class SelectorListener implements TableEntryListener {
        OldDriverCameras cameras;

        private SelectorListener(OldDriverCameras cameras) {
            this.cameras = cameras;
        }

        @Override
        public void valueChanged(NetworkTable table, String key, NetworkTableEntry entry, NetworkTableValue value, int flags) {
            cameras.switchCameraFeed();
        }
    }
}
