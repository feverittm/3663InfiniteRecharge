/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.playingwithfusion.TimeOfFlight;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.frcteam2910.common.math.Rotation2;
import org.frcteam2910.common.robot.UpdateManager;
import org.frcteam2910.common.robot.input.Controller;
import org.frcteam2910.common.robot.input.XboxController;

import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Constants;
import frc.robot.commands.C_Drive;
import frc.robot.test.C_FeederTest;
import frc.robot.drivers.TimeOfFlightSensor;
import frc.robot.subsystems.SS_Drivebase;
import frc.robot.subsystems.SS_Feeder;


public class RobotContainer {    
    private final Controller driveController = new XboxController(Constants.DRIVE_CONTROLLER_ID);

    // Driver Declarations
 
    
    // Subsystem Declarations
    protected SS_Feeder feeder;
    private final SS_Drivebase drivebase = new SS_Drivebase();

    // Command declarations


    //All updatable subsystems should be passed as parameters into the UpdateManager constructor
    private final UpdateManager updateManager = new UpdateManager(drivebase);

    
public RobotContainer() {

        initSubsystems();
        
        driveController.getRightXAxis().setScale(.3);
        driveController.getRightXAxis().setInverted(true);

        
        CommandScheduler.getInstance().setDefaultCommand(drivebase, new C_Drive(drivebase, 
                    () -> driveController.getLeftYAxis().get(true), 
                    () -> driveController.getLeftXAxis().get(true), 
                    () -> driveController.getRightXAxis().get(true))
        );
        
        updateManager.startLoop(5.0e-3);

        configureButtonBindings();
    }

    private void initSubsystems() {
        // Set the feeder subsystem
        CANSparkMax beltMotor = new CANSparkMax(Constants.FEED_MOTOR_CANID, MotorType.kBrushless);
        //drivers
        TimeOfFlightSensor entrySensor = new TimeOfFlightSensor( Constants.ENTRY_SENSOR_CANID);
        TimeOfFlightSensor exitSensor = new TimeOfFlightSensor( Constants.EXIT_SENSOR_CANID);

        //subsystems
        feeder = new SS_Feeder(beltMotor, entrySensor, exitSensor);
    }
    
    private void configureButtonBindings() {
        driveController.getBackButton().whenPressed(new InstantCommand(() -> drivebase.resetGyroAngle(Rotation2.ZERO), drivebase));
        driveController.getAButton().whenPressed(new C_FeederTest(feeder));
    }
}
