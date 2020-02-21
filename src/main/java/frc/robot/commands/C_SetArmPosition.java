package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Intake;
import frc.robot.utils.IntakePosition;

public class C_SetArmPosition extends CommandBase {

    //=====INSTANCE VARIABLES=====//
    private IntakePosition position;
    private final SS_Intake m_intakeSubsystem;
    private final Timer timer = new Timer();
    private final double DURATION = 10.0;
    private double currentTime;

    //=====CONSTRUCTOR=====//
    public C_SetArmPosition(SS_Intake subsystem, IntakePosition position) {
        m_intakeSubsystem = subsystem;
        this.position = position;
        addRequirements(m_intakeSubsystem);
    }

    //=====INITIALIZES THE COMMAND=====//
    @Override
    public void initialize() {

        //Resets and starts the timer the moment the command is initialized.
        timer.reset();
        timer.start();

        //Sets the position of the intake arm.
        currentTime = timer.get();
        SmartDashboard.putNumber("Time Between Transitions", currentTime);
        m_intakeSubsystem.setArmPosition(position);
    }

    //=====FINISHES THE COMMAND=====//
    @Override
    public boolean isFinished() {

        /*
        If the intake arm hits the limit switch, or the timer is past the duration,
        the command ends.
        */
        if(m_intakeSubsystem.getReachedLimit() || currentTime > DURATION) {
            return true;
        }
        return false;
    }
}