package frc.robot;

import org.frcteam2910.common.math.Rotation2;
import org.frcteam2910.common.math.Vector2;
import org.frcteam2910.common.robot.UpdateManager;
import org.frcteam2910.common.robot.input.Controller;
import org.frcteam2910.common.robot.input.XboxController;
import org.frcteam2910.common.robot.input.DPadButton.Direction;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.commandgroups.CG_PrepLobShot;
import frc.robot.commandgroups.CG_PrepShoot;
import frc.robot.commands.C_AutoAim;
import frc.robot.commands.C_AutoDrive;
import frc.robot.commands.C_Climb;
import frc.robot.commands.C_Drive;
import frc.robot.commands.C_FeederDefault;
import frc.robot.commands.C_StopShooter;
import frc.robot.commands.C_SwitchCamera;
import frc.robot.drivers.DriverCameras;
import frc.robot.commands.C_Track;

import frc.robot.drivers.Vision;
import frc.robot.drivers.DriverCameras.CameraFeed;
import frc.robot.subsystems.SS_Climber;
import frc.robot.subsystems.SS_Drivebase;
import frc.robot.subsystems.SS_Feeder;
import frc.robot.subsystems.SS_Intake;
import frc.robot.subsystems.SS_Shooter;
import frc.robot.commands.C_Intake;
import frc.robot.commands.C_PrepFeederToIntake;
import frc.robot.commands.C_Shoot;
import frc.robot.commands.C_ShootAll;
import frc.robot.utils.OrButton;
import frc.robot.utils.TriggerButton;

public class RobotContainer {    
    private static final Controller driveController = new XboxController(Constants.DRIVE_CONTROLLER_ID);
    private static final Controller operatorController = new XboxController(Constants.OPERATOR_CONTROLLER_ID);
    private static final Controller testcontroller = new XboxController(Constants.TEST_CONTROLLER_ID);
    private static final Joystick rumbleJoystick = new Joystick(Constants.DRIVE_CONTROLLER_ID);
    private static final Joystick operatorRumbleJoystick = new Joystick(Constants.OPERATOR_CONTROLLER_ID);
    private static final TriggerButton driveLeftTriggerButton = new TriggerButton(driveController.getLeftTriggerAxis());
    private static final TriggerButton driveRightTriggerButton = new TriggerButton(driveController.getRightTriggerAxis());
    private static final TriggerButton operatorLeftTriggerButton = new TriggerButton(operatorController.getLeftTriggerAxis());
    private static final OrButton orLeftBumperButton = new OrButton(driveController.getLeftBumperButton(), operatorController.getLeftBumperButton());
    private static final OrButton orRightBumperButton = new OrButton(driveController.getRightBumperButton(), operatorController.getRightBumperButton());

    private static final Vision vision = new Vision();
    private static final DriverCameras cameras = new DriverCameras(Constants.FEEDER_CAMERA_PORT, Constants.INTAKE_CAMERA_PORT);
    
    private SS_Feeder feeder = SS_Feeder.getInstance();
    private SS_Shooter shooter = SS_Shooter.getInstance();
    private SS_Intake intake = SS_Intake.getInstance();
    private SS_Drivebase drivebase = SS_Drivebase.getInstance();

    //All updatable subsystems should be passed as parameters into the UpdateManager constructor
    private final UpdateManager updateManager = new UpdateManager(drivebase);

    
    public RobotContainer() {
        operatorController.getRightYAxis().setScale(.5);

        CommandScheduler.getInstance().setDefaultCommand(drivebase, new C_Drive());
        CommandScheduler.getInstance().setDefaultCommand(feeder, new C_FeederDefault());
        updateManager.startLoop(5.0e-3);

        configureButtonBindings();
    }
    
    private void configureButtonBindings() {
        driveController.getBackButton().whenPressed(new InstantCommand(() -> drivebase.resetGyroAngle(Rotation2.ZERO), drivebase));
        driveController.getLeftBumperButton().whenHeld(new C_Track(), true);

        // driveController.getYButton().whenPressed(new C_RPMTuneTest(driveController, shooter, feeder, intake));

        driveRightTriggerButton.whenPressed(new C_PrepFeederToIntake());
        driveRightTriggerButton.whileHeld(new C_Intake());
            
        orLeftBumperButton.whenHeld(new CG_PrepShoot());
        driveController.getLeftBumperButton().whenReleased(new C_StopShooter(), false);

        //lob shot command bindings
        orRightBumperButton.whenHeld(new CG_PrepLobShot());
        driveController.getRightBumperButton().whenReleased(new C_StopShooter(), false);

        //Shoot Command bindings
        driveController.getAButton().whileHeld(new C_Shoot(), false);
        driveController.getBButton().whenPressed(new C_ShootAll(), false);

        //camera switching
        driveController.getRightBumperButton().whenPressed(new C_SwitchCamera(CameraFeed.SHOOTER))
            .whenReleased(new C_SwitchCamera(CameraFeed.FEEDER));
        driveController.getLeftBumperButton().whenPressed(new C_SwitchCamera(CameraFeed.SHOOTER))
            .whenReleased(new C_SwitchCamera(CameraFeed.FEEDER));

        operatorController.getDPadButton(Direction.UP).whenPressed(new C_SwitchCamera(CameraFeed.SHOOTER));
        operatorController.getDPadButton(Direction.DOWN).whenPressed(new C_SwitchCamera(CameraFeed.SHOOTER));

        operatorController.getBackButton().whenPressed(new C_Climb());
    }

    /** 
     * @return a preset autonomous command
     * 
     * @see AutonomousBuilder.buildAutoRoutine() 
     */
    public SequentialCommandGroup getAutonomousCommand() {
        return new SequentialCommandGroup(
            new InstantCommand(() -> drivebase.resetGyroAngle(Rotation2.fromDegrees(180)), drivebase),
            new InstantCommand(() -> vision.setLEDMode(Vision.LED_ON)),
            new ParallelCommandGroup(
                new CG_PrepShoot(),
                new C_AutoAim()
            ),
            new WaitCommand(1.5),
            new C_ShootAll(),
            new InstantCommand(() -> vision.setLEDMode(Vision.LED_OFF)),
            new C_AutoDrive(new Vector2(-36.0, 0.0), 1.0, Math.toRadians(180), 1.0)
        );
    }

    public static Vision getVision() {
        return vision;
    }

    public static DriverCameras getDriverCameras() {
        return cameras;
    }

    public static Controller getDriveController() {
        return driveController;
    }

    public static Controller getOperatorController() {
        return operatorController;
    }

    public static Joystick getRumbleJoystick() {
        return rumbleJoystick;
    }

    public static Joystick getOperatorRumbleJoystick() {
        return operatorRumbleJoystick;
    }

    public static TriggerButton getDriveLeftTriggerButton() {
        return driveLeftTriggerButton;
    }

    public static TriggerButton getDriveRightTriggerButton() {
        return driveRightTriggerButton;
    }

    public static TriggerButton getOperatorLeftTriggerButton() {
        return operatorLeftTriggerButton;
    }

    public static OrButton getOrLeftBumperButton() {
        return orLeftBumperButton;
    }

    public static OrButton getOrRightBumperButton() {
        return orRightBumperButton;
    }
}
