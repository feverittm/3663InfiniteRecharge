package frc.robot.test;

import org.frcteam2910.common.robot.input.Controller;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Intake;
import frc.robot.utils.IntakePosition;

public class C_IntakeTest extends CommandBase {

    //=====INSTANCE VARIABLES=====//
    private IntakePosition position;
    private final SS_Intake ss_Intake;
    private final Timer timer = new Timer();
    private final double DURATION = 10.0;
    private double currentTime;
    private double defaultSpeed = 0.5;
    private double speed;
    private Controller testController;

    //=====CONSTRUCTOR #1=====//
    public C_IntakeTest(SS_Intake subsystem, IntakePosition position, double speed, Controller testController) {
        this.speed = speed;
        ss_Intake = subsystem;
        this.position = position;
        this.testController = testController;
        addRequirements(subsystem);
    }

    //=====CONSTRUCTOR #2 (FOR CHOOSING THE POSITION OF THE INTAKE ARM)=====//
    public C_IntakeTest(SS_Intake subsystem, IntakePosition position, Controller testController) {
        ss_Intake = subsystem;
        this.position = position;
        this.testController = testController;
        addRequirements(subsystem);
    }

    //=====CONSTRUCTOR #3 (FOR SETTING THE SPEED OF THE INTAKE ARM MOTOR)=====//
    public C_IntakeTest(SS_Intake subsystem, double speed, Controller testController) {
        ss_Intake = subsystem;
        this.speed = speed;
        this.testController = testController;
        addRequirements(subsystem);
    }

    //=====INITIALIZES THE COMMAND=====//
    @Override
    public void initialize() {
        timer.reset();
        timer.start();

        if(testController.getRightTriggerAxis().get() > 0.5) {
            ss_Intake.setArmPosition(IntakePosition.FULLY_EXTENDED);
            ss_Intake.setPickupMotorSpeed(defaultSpeed);
        }
        else {
            ss_Intake.setArmPosition(IntakePosition.FULLY_RETRACTED);
            ss_Intake.setPickupMotorSpeed(0);
        }
        ss_Intake.setArmPosition(position);
        ss_Intake.setPickupMotorSpeed(speed);
    }

    //=====EXECUTES THE COMMAND=====//
    @Override
    public void execute() {
        currentTime = timer.get();
        SmartDashboard.putNumber("Time Between Transitions", currentTime);
        SmartDashboard.putNumber("Current Motor Speed", speed);
    }

    //=====FINISHES THE COMMAND=====//
    @Override
    public boolean isFinished() {
        if(currentTime > DURATION && speed == 0) {
            return true;
        }
        return false;
    }

    //=====ENDS THE COMMAND/INTERRUPTS IT=====//
    @Override
    public void end(boolean interrupted) {
        ss_Intake.setPickupMotorSpeed(0.0);
    }
}