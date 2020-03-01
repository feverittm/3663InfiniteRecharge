package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.frcteam2910.common.math.Rotation2;
import org.frcteam2910.common.robot.UpdateManager;
import org.frcteam2910.common.robot.input.Controller;
import org.frcteam2910.common.robot.input.XboxController;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Constants;
import frc.robot.test.*;
import frc.robot.commandgroups.CG_LobShot;
import frc.robot.commandgroups.CG_ShootBalls;
import frc.robot.commands.C_Climb;
import frc.robot.commands.C_Drive;
import frc.robot.commands.C_FeederDefault;
import frc.robot.commands.C_StopShooter;
import frc.robot.drivers.DriverCameras;
import frc.robot.commands.C_Track;

import frc.robot.drivers.Vision;
import frc.robot.subsystems.SS_Climber;
import frc.robot.subsystems.SS_Drivebase;
import frc.robot.subsystems.SS_Feeder;
import frc.robot.subsystems.SS_Intake;
import frc.robot.subsystems.SS_Shooter;
import frc.robot.commands.C_Intake;
import frc.robot.utils.TriggerButton;

public class RobotContainer {    
    private final Controller driveController = new XboxController(Constants.DRIVE_CONTROLLER_ID);
    private final Controller operatorController = new XboxController(Constants.OPERATOR_CONTROLLER_ID);
    private final Controller testcontroller = new XboxController(Constants.TEST_CONTROLLER_ID);
    private final Joystick rumbleJoystick = new Joystick(Constants.DRIVE_CONTROLLER_ID);
    private final TriggerButton rightTriggerButton = new TriggerButton(driveController.getRightTriggerAxis());
    // Driver Declarations
    Vision vision = new Vision();
    DriverCameras cameras = new DriverCameras();

    
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

        CommandScheduler.getInstance().setDefaultCommand(drivebase, new C_Drive(drivebase, 
                    () -> driveController.getLeftYAxis().get(true), 
                    () -> driveController.getLeftXAxis().get(true), 
                    () -> driveController.getRightXAxis().get(true))
        );
        CommandScheduler.getInstance().setDefaultCommand(feeder, new C_FeederDefault(feeder, rumbleJoystick));
        CommandScheduler.getInstance().setDefaultCommand(climber, new C_Climb(climber, operatorController));
        updateManager.startLoop(5.0e-3);

        configureButtonBindings();
    }

    private void initCommands(){
    }
    private void initDrivers() {
        vision = new Vision();
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
        CANSparkMax gondolaMotor = new CANSparkMax(Constants.CLIMBER_EXTEND_MOTOR_CANID, MotorType.kBrushless);
        CANSparkMax winchMotor = new CANSparkMax(Constants.CLIMBER_WINCH_MOTOR_CANID, MotorType.kBrushless);
        CANSparkMax hookMotor = new CANSparkMax(Constants.CLIMBER_DRIVE_MOTOR_CANID, MotorType.kBrushless);
        climber = new SS_Climber(gondolaMotor, winchMotor, hookMotor);
    }
    
    private void configureButtonBindings() {
        //testcontroller.getYButton().whenPressed(new C_RPMTuneTest(testcontroller, shooter, feeder, intake));

        driveController.getBackButton().whenPressed(new InstantCommand(() -> drivebase.resetGyroAngle(Rotation2.ZERO), drivebase));
        driveController.getLeftBumperButton().whenHeld(new C_Track(vision, drivebase,
            () -> driveController.getLeftYAxis().get(true),
            () -> driveController.getLeftXAxis().get(true)), true);

        DigitalInput intakeSensor = new DigitalInput(Constants.INTAKE_SENSOR);
        rightTriggerButton.whileHeld(new C_Intake(intake, driveController, intakeSensor));
        //driveController.getXButton().whenHeld(new C_Intake(intake, driveController));
            
        
        driveController.getLeftBumperButton().whileHeld(new CG_ShootBalls(feeder, shooter, driveController, rumbleJoystick),false);
        driveController.getLeftBumperButton().whenReleased(new C_StopShooter(shooter));

        //lob shot command bindings
        driveController.getRightBumperButton().whileHeld(new CG_LobShot(driveController, rumbleJoystick, shooter, feeder), false);
        driveController.getRightBumperButton().whenReleased(new C_StopShooter(shooter));
    }
}
