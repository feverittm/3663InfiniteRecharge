package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Intake;
import frc.robot.subsystems.SS_Intake.IntakePosition;

public class C_SetArmPosition extends CommandBase {

    //=====INSTANCE VARIABLES=====//
    private final SS_Intake ss_Intake;
    private final Timer timer = new Timer();
    private final double DURATION = 10.0;
    private double currentTime;

    //=====CONSTRUCTOR=====//
    public C_SetArmPosition(SS_Intake subsystem) {
        ss_Intake = subsystem;
        addRequirements(ss_Intake);
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
        ss_Intake.setArmPosition(IntakePosition.POSITION_0);
    }

    //=====FINISHES THE COMMAND=====//
    @Override
    public boolean isFinished() {

        /*
        If the intake arm hits the limit switch, or the timer is past the duration,
        the command ends.
        */
        if(currentTime > DURATION) {
            return true;
        }
        return false;
    }
}