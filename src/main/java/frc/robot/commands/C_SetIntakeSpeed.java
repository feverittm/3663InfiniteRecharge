package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Intake;
import frc.robot.utils.IntakePosition;

public class C_SetIntakeSpeed extends CommandBase {

    //=====INSTANCE VARIABLES=====//
    private final SS_Intake m_intakeSubsystem;
    private double intakeSpeed;
    private double speed;

    //=====CONSTRUCTOR=====//
    public C_SetIntakeSpeed(SS_Intake subsystem, double intakeSpeed) {
        m_intakeSubsystem = subsystem;
        this.intakeSpeed = intakeSpeed;
        addRequirements(m_intakeSubsystem);
    }

    //=====EXECUTES THE COMMAND=====//
    @Override
    public void execute() {

        //Intake motor speed is initialized as zero.
        speed = 0.0;
        
        /*
        If the intake arm position is position 3 (fully extended), the motor speed is set to
        the intakeSpeed variable established in the robot container.
        */
        if(m_intakeSubsystem.getIntakePosition() == IntakePosition.FULLY_EXTENDED) {
            speed = intakeSpeed;
        }
        SmartDashboard.putNumber("Current Motor Speed", speed);

        //Sets the speed of the pick up motor.
        m_intakeSubsystem.setPickupMotorSpeed(speed);
    }
    
    //=====FINISHES THE COMMAND=====//
    @Override
    public boolean isFinished() {
        return false;
    }

    //=====ENDS THE COMMAND/INTERRUPTS IT=====//
    @Override
    public void end(boolean interrupted) {
        m_intakeSubsystem.setPickupMotorSpeed(0.0);
    }
}