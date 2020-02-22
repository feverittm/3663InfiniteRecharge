package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import frc.robot.Constants;
import frc.robot.utils.IntakePosition;

public class SS_Intake extends SubsystemBase {

    //=====INSTANCE VARIABLES=====//
    private DoubleSolenoid shortSolenoid = new DoubleSolenoid(Constants.INTAKE_SHORT_SOLENOID_FORWARD_ID,
            Constants.INTAKE_SHORT_SOLENOID_REVERSE_ID);
    private DoubleSolenoid longSolenoid = new DoubleSolenoid(Constants.INTAKE_LONG_SOLENOID_FORWARD_ID,
            Constants.INTAKE_LONG_SOLENOID_REVERSE_ID);
    private CANSparkMax pickupMotor = new CANSparkMax(Constants.INTAKE_MOTOR_CANID, MotorType.kBrushless);

    private IntakePosition currentPosition;
    
    //=====CONSTRUCTOR=====//
    public SS_Intake() { 
        pickupMotor.setIdleMode(IdleMode.kBrake);
    }

    //=====SETS THE INTAKE ARM POSITION=====//
    public void setArmPosition(IntakePosition position){
        //Depending on the button pressed, this command sets the intake arm position.
        currentPosition = position;
        switch(position){
            case FULLY_RETRACTED:
                shortSolenoid.set(DoubleSolenoid.Value.kReverse);
                longSolenoid.set(DoubleSolenoid.Value.kReverse);
                break;

            case SHORT_RETRACT:
                shortSolenoid.set(DoubleSolenoid.Value.kReverse);
                longSolenoid.set(DoubleSolenoid.Value.kForward);
                break;
            
            case LONG_RETRACT:
                shortSolenoid.set(DoubleSolenoid.Value.kForward);
                longSolenoid.set(DoubleSolenoid.Value.kReverse);
                break;

            case FULLY_EXTENDED:
                shortSolenoid.set(DoubleSolenoid.Value.kForward);
                longSolenoid.set(DoubleSolenoid.Value.kForward);   
                break;
        }
    }

    //=====RETURNS THE POSITION OF THE INTAKE ARM=====//
    public IntakePosition getIntakePosition() {
        return currentPosition;
    }

    //=====SETS THE SPEED OF THE PICKUP MOTOR=====//
    public void setPickupMotorSpeed(double pickUpSpeed){
    	pickupMotor.set(pickUpSpeed);
    }
}