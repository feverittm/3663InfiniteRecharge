package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import frc.robot.utils.IntakePosition;

public class SS_Intake extends SubsystemBase {

    //=====INSTANCE VARIABLES=====//
    private DoubleSolenoid shortSolenoid;
    private DoubleSolenoid longSolenoid;

    private CANSparkMax pickupMotor;

    private IntakePosition currentPosition;
    
    //=====CONSTRUCTOR=====//
    public SS_Intake(DoubleSolenoid shortSolenoid, DoubleSolenoid longSolenoid, CANSparkMax pickUpMotor) { 
        this.shortSolenoid = shortSolenoid;
        this.longSolenoid = longSolenoid;

        this.pickupMotor = pickUpMotor;

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

    //=====SETS THE BREAK MODE OF THE SPARKS=====//
    public void setBrakeMode() {
        pickupMotor.setIdleMode(IdleMode.kBrake);
    }
}