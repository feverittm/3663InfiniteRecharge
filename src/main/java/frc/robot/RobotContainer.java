package frc.robot;

//import com.playingwithfusion.TimeOfFlight;
//import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.frcteam2910.common.math.Rotation2;
import org.frcteam2910.common.robot.UpdateManager;
import org.frcteam2910.common.robot.input.Controller;
import org.frcteam2910.common.robot.input.XboxController;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
//import edu.wpi.first.wpilibj2.command.CommandScheduler;
//import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Constants;
import frc.robot.commands.C_Drive;
import frc.robot.commands.C_SetArmPosition;
import frc.robot.subsystems.SS_Drivebase;
import frc.robot.subsystems.SS_Feeder;
//import frc.robot.subsystems.SS_Feeder;
import frc.robot.subsystems.SS_Intake;
import frc.robot.utils.IntakePosition;
import frc.robot.commands.C_Track;

import frc.robot.drivers.Vision;
import frc.robot.subsystems.SS_Drivebase;
import frc.robot.subsystems.SS_Feeder;
import frc.robot.subsystems.SS_Shooter;


public class RobotContainer {    
    private final Controller driveController = new XboxController(Constants.DRIVE_CONTROLLER_ID);

    // Driver Declarations
    Vision vision = new Vision();

    
    // Subsystem Declarations

    protected SS_Feeder feeder;
    protected SS_Shooter shooter;
    private final SS_Drivebase drivebase = new SS_Drivebase();
    //private SS_Feeder feederSys;
    private SS_Intake ss_Intake;

    // Command declarations


    //All updatable subsystems should be passed as parameters into the UpdateManager constructor
    private final UpdateManager updateManager = new UpdateManager(drivebase);

    
public RobotContainer() {

        initDrivers();
        initSubsystems();
        
        driveController.getRightXAxis().setScale(.3);
        driveController.getRightXAxis().setInverted(true);

        
        CommandScheduler.getInstance().setDefaultCommand(drivebase, new C_Drive(drivebase, 
                    () -> driveController.getLeftYAxis().get(true), 
                    () -> driveController.getLeftXAxis().get(true), 
                    () -> driveController.getRightXAxis().get(true))
        );
        
        updateManager.startLoop(5.0e-3);

        CommandScheduler.getInstance().setDefaultCommand(ss_Intake, new C_SetArmPosition(ss_Intake, IntakePosition.FULLY_RETRACTED));
    //driveController.getRightTriggerAxis()
    //    .whenHeld(new InstantCommand(() -> new CG_Roomba(true, ss_Intake)));

        configureButtonBindings();
    }

    private void initDrivers() {
        vision = new Vision();
    }

    private void initSubsystems() {
        // Set the feeder subsystem
        /*CANSparkMax beltMotor = new CANSparkMax(Constants.FEED_MOTOR_CANID, MotorType.kBrushless);
        TimeOfFlight entrySensor = new TimeOfFlight( Constants.ENTRY_SENSOR_CANID);
        TimeOfFlight exitSensor = new TimeOfFlight( Constants.EXIT_SENSOR_CANID);*/

        //feederSys = new SS_Feeder(beltMotor, entrySensor, exitSensor);
        
        ss_Intake = new SS_Intake();
    }
    
    /*private void configureButtonBindings() {
    }*/

    public Command getAutonomousCommand() {
        return null;
    }

    public Command getTeleopCommand() {
        return null;
        this.feeder = new SS_Feeder(beltMotor, entrySensor, exitSensor);

        shooter = new SS_Shooter(vision, Constants.SHOOTER_MOTOR_CANID, Constants.HOOD_SOLENOID_FORWARD_ID, 
            Constants.HOOD_SOLENOID_REVERSE_ID);
    }
    
    private void configureButtonBindings() {
        driveController.getBackButton().whenPressed(new InstantCommand(() -> drivebase.resetGyroAngle(Rotation2.ZERO), drivebase));
        driveController.getLeftBumperButton().whenHeld(new C_Track(vision, drivebase,
            () -> driveController.getLeftYAxis().get(true),
            () -> driveController.getLeftXAxis().get(true)), true);
    }
}
