package frc.robot.test;

import org.frcteam2910.common.robot.input.Controller;
import org.frcteam2910.common.robot.input.DPadButton.Direction;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Intake;
import frc.robot.subsystems.SS_Intake.IntakePosition;

//this can be added to RobotContainer to run this intake test
//CommandScheduler.getInstance().setDefaultCommand(intake, new C_IntakeTest(intake, driveController));

public class C_IntakeTest extends CommandBase {

    //=====INSTANCE VARIABLES=====//
    private final SS_Intake ss_Intake;
    private final Timer timer = new Timer();
    private double currentTime;
    private Controller controller;

    //=====CONSTRUCTOR #1=====//
    public C_IntakeTest(SS_Intake subsystem, Controller controller) {
        ss_Intake = subsystem;
        this.controller = controller;
        addRequirements(subsystem);
    }

    //=====INITIALIZES THE COMMAND=====//
    @Override
    public void initialize() {
        timer.reset();
        timer.start();
    }

    //=====EXECUTES THE COMMAND=====//
    @Override
    public void execute() {
        currentTime = timer.get();
        SmartDashboard.putNumber("Time Between Transitions", currentTime);
        //SmartDashboard.putNumber("Current Motor Speed", speed);

        if(controller.getDPadButton(Direction.UP).get()) {
            ss_Intake.setArmPosition(IntakePosition.POSITION_3);
            //ss_Intake.setPickupMotorSpeed(0);
        }
        
        if(controller.getDPadButton(Direction.DOWN).get()) {
            ss_Intake.setArmPosition(IntakePosition.POSITION_0);
            //ss_Intake.setPickupMotorSpeed(0);
        }

        if(controller.getDPadButton(Direction.LEFT).get()) {
            ss_Intake.setArmPosition(IntakePosition.POSITION_1);
            //ss_Intake.setPickupMotorSpeed(0);
        }

        if(controller.getDPadButton(Direction.RIGHT).get()) {
            ss_Intake.setArmPosition(IntakePosition.POSITION_2);
            //ss_Intake.setPickupMotorSpeed(0);
        }
        
        //ss_Intake.setPickupMotorSpeed((controller.getRightTriggerAxis().get() - controller.getLeftTriggerAxis().get())/2);
    }

    //=====FINISHES THE COMMAND=====//
    @Override
    public boolean isFinished() {
        return false;
    }

    //=====ENDS THE COMMAND/INTERRUPTS IT=====//
    @Override
    public void end(boolean interrupted) {
        //ss_Intake.setPickupMotorSpeed(0.0);
    }
}