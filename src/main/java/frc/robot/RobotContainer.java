/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.sql.Time;

import com.playingwithfusion.TimeOfFlight;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

//import org.frcteam2910.common.robot.UpdateManager;
import org.frcteam2910.common.robot.input.Controller;
import org.frcteam2910.common.robot.input.XboxController;

import edu.wpi.first.wpilibj2.command.Command;
//import edu.wpi.first.wpilibj2.command.CommandScheduler;
//import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Constants;
import frc.robot.subsystems.FeederSys;


public class RobotContainer {
    
    private final Controller driveController = new XboxController(Constants.DRIVE_CONTROLLER_ID);

    // Driver Declarations
 
    
    // Subsystem Declarations
    private FeederSys feederSys;

    // Command declarations


    // All updatable subsystems should be passed as parameters into the UpdateManager constructor
//    private final UpdateManager updateManager = new UpdateManager(drivebase);

    
public RobotContainer() {

        initSubsystems();
        
        driveController.getRightXAxis().setScale(.3);
        
 //       updateManager.startLoop(5.0e-3);

        configureButtonBindings();
    }

    private void initSubsystems() {

        // Set the feeder subsystem
        CANSparkMax beltMotor = new CANSparkMax(Constants.FEED_MOTOR_CANID, MotorType.kBrushless);
        TimeOfFlight entrySensor = new TimeOfFlight( Constants.ENTRY_SENSOR_CANID);
        TimeOfFlight exitSensor = new TimeOfFlight( Constants.EXIT_SENSOR_CANID);

        feederSys = new FeederSys(beltMotor, entrySensor, exitSensor);
    }
    
    private void configureButtonBindings() {
 //       driveController.getBackButton().whenPressed(new InstantCommand(() -> drivebase.resetGyroAngle(Rotation2.ZERO), drivebase));
    }

    public Command getAutonomousCommand() {
        return null;
    }

    public Command getTeleopCommand() {
        return null;
    }
}
