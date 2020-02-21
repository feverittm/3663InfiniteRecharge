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

//import org.frcteam2910.common.robot.UpdateManager;
import org.frcteam2910.common.robot.input.Controller;
import org.frcteam2910.common.robot.input.XboxController;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
//import edu.wpi.first.wpilibj2.command.CommandScheduler;
//import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Constants;
<<<<<<< HEAD
import frc.robot.subsystems.FeederSys;
import frc.robot.subsystems.SS_Intake;
=======
import frc.robot.subsystems.SS_Feeder;
>>>>>>> 92cc563287dfa6a622822c5503c052a4eb842f4f


public class RobotContainer {
    
    private final Controller driveController = new XboxController(Constants.DRIVE_CONTROLLER_ID);

    // Driver Declarations
 
    
    // Subsystem Declarations
<<<<<<< HEAD
    private FeederSys feederSys;
    private SS_Intake ss_Intake;
=======
    protected SS_Feeder feederSys;
>>>>>>> 92cc563287dfa6a622822c5503c052a4eb842f4f

    // Command declarations


    // All updatable subsystems should be passed as parameters into the UpdateManager constructor
//    private final UpdateManager updateManager = new UpdateManager(drivebase);

    
public RobotContainer() {

        initSubsystems();
        
        driveController.getRightXAxis().setScale(.3);
        
 //       updateManager.startLoop(5.0e-3);

    CommandScheduler.getInstance().setDefaultCommand(ss_Intake, CG_Rooma());
    //driveController.getRightTriggerAxis()
    //    .whenHeld(new InstantCommand(() -> new CG_Roomba(true, ss_Intake)));


    }

    private void initSubsystems() {

        // Set the feeder subsystem
        CANSparkMax beltMotor = new CANSparkMax(Constants.FEED_MOTOR_CANID, MotorType.kBrushless);
        TimeOfFlight entrySensor = new TimeOfFlight( Constants.ENTRY_SENSOR_CANID);
        TimeOfFlight exitSensor = new TimeOfFlight( Constants.EXIT_SENSOR_CANID);

<<<<<<< HEAD
        feederSys = new FeederSys(beltMotor, entrySensor, exitSensor);

        ss_Intake = new SS_Intake();
=======
        this.feederSys = new SS_Feeder(beltMotor, entrySensor, exitSensor);
>>>>>>> 92cc563287dfa6a622822c5503c052a4eb842f4f
    }
    
    private void configureButtonBindings() {
    }

    public Command getAutonomousCommand() {
        return null;
    }

    public Command getTeleopCommand() {
        return null;
    }
}
