package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.frcteam2910.common.math.Rotation2;
import org.frcteam2910.common.math.Vector2;
import org.frcteam2910.common.robot.UpdateManager;
import org.frcteam2910.common.robot.input.Controller;
import org.frcteam2910.common.robot.input.XboxController;
import org.frcteam2910.common.robot.input.DPadButton.Direction;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.StartEndCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.test.*;
import frc.robot.commandgroups.CG_LobShot;
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
import frc.robot.commands.C_PrepFeedIntake;
import frc.robot.commands.C_PrepFeedToShoot;
import frc.robot.commands.C_Shoot;
import frc.robot.commands.C_ShootAll;
import frc.robot.utils.TriggerButton;

public class RobotContainer {    
    private final Controller driveController = new XboxController(Constants.DRIVE_CONTROLLER_ID);
    private final Controller operatorController = new XboxController(Constants.OPERATOR_CONTROLLER_ID);
    private final Controller testcontroller = new XboxController(Constants.TEST_CONTROLLER_ID);
    private final Joystick rumbleJoystick = new Joystick(Constants.DRIVE_CONTROLLER_ID);
    private final Joystick operatorRumbleJoystick = new Joystick(Constants.OPERATOR_CONTROLLER_ID);
    private final TriggerButton rightTriggerButton = new TriggerButton(driveController.getRightTriggerAxis());
    // Driver Declarations
    Vision vision = new Vision();
    DriverCameras cameras = new DriverCameras(Constants.FEEDER_CAMERA_PORT, Constants.INTAKE_CAMERA_PORT);

    
    // Subsystem Declarations
    protected SS_Feeder feeder;
    protected SS_Shooter shooter;
    protected SS_Intake intake;
    private final SS_Drivebase drivebase = new SS_Drivebase();
    protected SS_Climber climber;

    // Command declarations 

    //All updatable subsystems should be passed as parameters into the UpdateManager constructor
    private final UpdateManager updateManager = new UpdateManager(drivebase);

    
public RobotContainer() {

        initDrivers();
        initSubsystems();
        initCommands();
        driveController.getRightXAxis().setScale(.3);
        driveController.getRightXAxis().setInverted(true);
        operatorController.getRightYAxis().setScale(.5);

        CommandScheduler.getInstance().setDefaultCommand(drivebase, new C_Drive(drivebase, 
                    () -> driveController.getLeftYAxis().get(true), 
                    () -> driveController.getLeftXAxis().get(true), 
                    () -> driveController.getRightXAxis().get(true))
        );
        CommandScheduler.getInstance().setDefaultCommand(feeder, new C_FeederDefault(feeder, rumbleJoystick));
        updateManager.startLoop(5.0e-3);

        configureButtonBindings();
    }

    private void initCommands() {
    }
    private void initDrivers() {
    }

    private void initSubsystems() {
        // Feeder subsystem
        CANSparkMax beltMotor = new CANSparkMax(Constants.FEED_MOTOR_CANID, MotorType.kBrushless);
        DigitalInput entrySensor = new DigitalInput(Constants.ENTRY_SENSOR_DIO_ID);
        DigitalInput exitSensor = new DigitalInput(Constants.EXIT_SENSOR_DIO_ID);
        this.feeder = new SS_Feeder(beltMotor, entrySensor, exitSensor);

        // Intake subsystem
        DoubleSolenoid shortSolenoid = new DoubleSolenoid(Constants.INTAKE_SHORT_SOLENOID_FORWARD_ID, Constants.INTAKE_SHORT_SOLENOID_REVERSE_ID);
        DoubleSolenoid longSolenoid = new DoubleSolenoid(Constants.INTAKE_LONG_SOLENOID_FORWARD_ID, Constants.INTAKE_LONG_SOLENOID_REVERSE_ID);
        CANSparkMax pickupMotor = new CANSparkMax(Constants.INTAKE_MOTOR_CANID, MotorType.kBrushless);
        intake = new SS_Intake(shortSolenoid, longSolenoid, pickupMotor);

       // Shooter subsystem
        shooter = new SS_Shooter(vision, Constants.SHOOTER_MOTOR_CANID, Constants.HOOD_SOLENOID_FORWARD_ID, Constants.HOOD_SOLENOID_REVERSE_ID);

        // Climber subsystem
        CANSparkMax gondolaMotor = new CANSparkMax(Constants.CLIMBER_DRIVE_MOTOR_CANID, MotorType.kBrushless);
        CANSparkMax winchMotor = new CANSparkMax(Constants.CLIMBER_WINCH_MOTOR_CANID, MotorType.kBrushless);
        CANSparkMax hookMotor = new CANSparkMax(Constants.CLIMBER_EXTEND_MOTOR_CANID, MotorType.kBrushless);
        climber = new SS_Climber(gondolaMotor, winchMotor, hookMotor);
    }
    
    private void configureButtonBindings() {
        driveController.getYButton().whenPressed(new C_RPMTuneTest(driveController, shooter));
        driveController.getBackButton().whenPressed(new InstantCommand(() -> drivebase.resetGyroAngle(Rotation2.ZERO), drivebase));
        driveController.getLeftBumperButton().whenHeld(new C_Track(vision, drivebase,
            () -> driveController.getLeftYAxis().get(true),
            () -> driveController.getLeftXAxis().get(true)), true);

        rightTriggerButton.whenPressed(new C_PrepFeedIntake(feeder));
        rightTriggerButton.whileHeld(new C_Intake(intake, driveController));
        //driveController.getXButton().whenHeld(new C_Intake(intake, driveController));
            
        
        driveController.getLeftBumperButton().whileHeld(new CG_PrepShoot(feeder, shooter, rumbleJoystick),false);
        driveController.getLeftBumperButton().whenReleased(new C_StopShooter(shooter));
        driveController.getAButton().whileHeld(new C_Shoot(feeder, shooter), false);
        driveController.getXButton().whenPressed(new C_ShootAll(feeder), false);

        //lob shot command bindings
        driveController.getRightBumperButton().whileHeld(new CG_LobShot(driveController, rumbleJoystick, shooter, feeder));
        driveController.getRightBumperButton().whenReleased(new C_StopShooter(shooter));

        //camera switching
        driveController.getRightBumperButton().whenPressed(new C_SwitchCamera(cameras, CameraFeed.SHOOTER))
            .whenReleased(new C_SwitchCamera(cameras, CameraFeed.FEEDER));
        driveController.getLeftBumperButton().whenPressed(new C_SwitchCamera(cameras, CameraFeed.SHOOTER))
            .whenReleased(new C_SwitchCamera(cameras, CameraFeed.FEEDER));

        operatorController.getDPadButton(Direction.UP).whenPressed(new C_SwitchCamera(cameras, CameraFeed.SHOOTER));
        operatorController.getDPadButton(Direction.DOWN).whenPressed(new C_SwitchCamera(cameras, CameraFeed.SHOOTER));

        operatorController.getBackButton().whenPressed(new C_Climb(climber, operatorController, operatorRumbleJoystick));
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
                new CG_PrepShoot(feeder, shooter, rumbleJoystick),
                new C_AutoAim(drivebase, vision)
            ),
            new WaitCommand(1.5),
            new C_ShootAll(feeder),
            new InstantCommand(() -> vision.setLEDMode(Vision.LED_OFF)),
            new C_AutoDrive(drivebase, new Vector2(-36.0, 0.0), 1.0, Math.toRadians(180), 1.0)
        );
    }

    public SS_Drivebase getDrivebase() {
        return drivebase;
    }

    public SS_Feeder getFeeder() {
        return feeder;
    }
    
    public SS_Climber getClimber() {
        return climber;
    }

    public SS_Intake getIntake() {
        return intake;
    }

    public SS_Shooter getShooter() {
        return shooter;
    }

    public Vision getVision() {
        return vision;
    }

    public Controller getDriveController() {
        return driveController;
    }
    public Joystick getRumbleJoystick() {
        return rumbleJoystick;
    }
}
