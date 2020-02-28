package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.frcteam2910.common.math.Rotation2;
import org.frcteam2910.common.robot.UpdateManager;
import org.frcteam2910.common.robot.input.Controller;
import org.frcteam2910.common.robot.input.XboxController;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Constants;
import frc.robot.test.*;
import frc.robot.commandgroups.CG_LobShot;
import frc.robot.commandgroups.CG_ShootBalls;
import frc.robot.commands.C_Drive;
import frc.robot.commands.C_FeederDefault;
import frc.robot.commands.C_StopShooter;
import frc.robot.drivers.TimeOfFlightSensor;
import frc.robot.commands.C_Track;

import frc.robot.drivers.Vision;
import frc.robot.subsystems.SS_Drivebase;
import frc.robot.subsystems.SS_Feeder;
import frc.robot.subsystems.SS_Intake;
import frc.robot.subsystems.SS_Shooter;
import frc.robot.commands.C_Intake;
import frc.robot.utils.TriggerButton;

public class RobotContainer {    
    private final Controller driveController = new XboxController(Constants.DRIVE_CONTROLLER_ID);
    //private final Controller testcontroller = new XboxController(Constants.TEST_CONTROLLER_ID);
    private final Joystick rumbleJoystick = new Joystick(Constants.DRIVE_CONTROLLER_ID);
    private final TriggerButton rightTriggerButton = new TriggerButton(driveController.getRightTriggerAxis());
    // Driver Declarations
    Vision vision = new Vision();

    
    // Subsystem Declarations
    protected SS_Feeder feeder;
    protected SS_Shooter shooter;
    protected SS_Intake intake;
    private final SS_Drivebase drivebase = new SS_Drivebase();

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
        TimeOfFlightSensor entrySensor = new TimeOfFlightSensor(Constants.ENTRY_SENSOR_CANID);
        TimeOfFlightSensor exitSensor = new TimeOfFlightSensor(Constants.EXIT_SENSOR_CANID);
        this.feeder = new SS_Feeder(beltMotor, entrySensor, exitSensor);

        // Intake subsystem
        DoubleSolenoid shortSolenoid = new DoubleSolenoid(Constants.INTAKE_SHORT_SOLENOID_FORWARD_ID, Constants.INTAKE_SHORT_SOLENOID_REVERSE_ID);
        DoubleSolenoid longSolenoid = new DoubleSolenoid(Constants.INTAKE_LONG_SOLENOID_FORWARD_ID, Constants.INTAKE_LONG_SOLENOID_REVERSE_ID);
        CANSparkMax pickupMotor = new CANSparkMax(Constants.INTAKE_MOTOR_CANID, MotorType.kBrushless);
        intake = new SS_Intake(shortSolenoid, longSolenoid, pickupMotor);

       // Shooter subsystem
        shooter = new SS_Shooter(vision, Constants.SHOOTER_MOTOR_CANID, Constants.HOOD_SOLENOID_FORWARD_ID, Constants.HOOD_SOLENOID_REVERSE_ID);
    }
    
    private void configureButtonBindings() {
        driveController.getYButton().whenPressed(new C_RPMTuneTest(driveController, shooter));
        driveController.getBackButton().whenPressed(new InstantCommand(() -> drivebase.resetGyroAngle(Rotation2.ZERO), drivebase));
        driveController.getRightBumperButton().whenHeld(new C_Track(vision, drivebase,
            () -> driveController.getLeftYAxis().get(true),
            () -> driveController.getLeftXAxis().get(true)), true);

        rightTriggerButton.whileHeld(new C_Intake(intake, driveController));
        driveController.getXButton().whenHeld(new C_Intake(intake, driveController));
            
        
        driveController.getLeftBumperButton().whileHeld(new CG_ShootBalls(feeder, shooter, driveController, rumbleJoystick),false);
        driveController.getLeftBumperButton().whenReleased(new C_StopShooter(shooter));

        //lob shot command bindings
        driveController.getRightBumperButton().whileHeld(new CG_LobShot(driveController, rumbleJoystick, shooter, feeder));
        driveController.getRightBumperButton().whenReleased(new C_StopShooter(shooter));
    }
}
