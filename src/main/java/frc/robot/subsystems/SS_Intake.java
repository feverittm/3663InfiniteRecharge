package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class SS_Intake extends SubsystemBase {
    /**
     * Intake arm extend positions
     * @param FULLY_RETRACTED checks to see if both the long and short solenoids are retracted
     * @param LONG_RETRACT checks to see if ONLY the long solenoid is retracted
     * @param SHORT_RETRACT checks to see if ONLY the short solenoid is retracted
     * @param FULLY_EXTENDED checks to see if both the long and short solenoids are extended
     */
    public enum IntakePosition{
        FULLY_RETRACTED, //POSITION 0 (for putting the intake arm within the frame perimeter)
        LONG_RETRACT, //POSITION 1 (for inserting power cells into the magazine)
        SHORT_RETRACT, //POSITION 2 (for storing power cells in the intake arm)
        FULLY_EXTENDED //POSITION 3 (for intaking power cells)
    }

    //=====INSTANCE VARIABLES=====//
    private DoubleSolenoid shortSolenoid;
    private DoubleSolenoid longSolenoid;
    private CANSparkMax pickupMotor;

    private IntakePosition currentPosition;
    
    //=====CONSTRUCTOR=====//
    public SS_Intake(DoubleSolenoid shortSolenoid, DoubleSolenoid longSolenoid, CANSparkMax pickupMotor) { 
        this.shortSolenoid = shortSolenoid;
        this.longSolenoid = longSolenoid;
        this.pickupMotor = pickupMotor;
        
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