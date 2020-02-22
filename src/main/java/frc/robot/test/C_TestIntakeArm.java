package frc.robot.test;

import org.frcteam2910.common.robot.input.Controller;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Intake;
import frc.robot.utils.IntakePosition;

public class C_TestIntakeArm extends CommandBase {

    //=====INSTANCE VARIABLES=====//
    private IntakePosition position;
    private final SS_Intake ss_Intake;
    private final Timer timer = new Timer();
    private final double DURATION = 10.0;
    private double currentTime;
    private double defaultSpeed;
    private double speed;
    private Controller testController;

    //=====CONSTRUCTOR #1=====//
    public C_TestIntakeArm(SS_Intake subsystem, IntakePosition position, double defaultSpeed, Controller testController) {
        this.defaultSpeed = defaultSpeed;
        ss_Intake = subsystem;
        this.position = position;
        this.testController = testController;
        addRequirements(subsystem);
    }

    //=====CONSTRUCTOR #2=====//
    public C_TestIntakeArm(SS_Intake subsystem, IntakePosition position, Controller testController) {
        ss_Intake = subsystem;
        this.position = position;
        this.testController = testController;
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

        if(testController.getRightTriggerAxis().get() > 0.5) {
            speed = defaultSpeed;
            ss_Intake.setArmPosition(IntakePosition.FULLY_EXTENDED);
            ss_Intake.setPickupMotorSpeed(0.5);
        }
        SmartDashboard.putNumber("Current Motor Speed", speed);

        ss_Intake.setArmPosition(position);
    }

    //=====FINISHES THE COMMAND=====//
    @Override
    public boolean isFinished() {
        /*if(ss_Intake.getReachedLimit() || currentTime > DURATION) {
            return true;
        }
        return false;*/

        if(currentTime > DURATION) {
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