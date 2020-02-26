package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
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

    private CANPIDController pid;

    private IntakePosition currentPosition;

    private boolean isRetracting = false;
    private int targetRotations = 0;
    private final int INTAKE_ROTATIONS = 500;

    private final double KP = 0.0001;
    private final double KI = 0.000001;
    private final double KD = 0;

    private final int RETRACT_VELOCITY = 200;
    
    //=====CONSTRUCTOR=====//
    public SS_Intake(DoubleSolenoid shortSolenoid, DoubleSolenoid longSolenoid, CANSparkMax pickupMotor) { 
        this.shortSolenoid = shortSolenoid;
        this.longSolenoid = longSolenoid;
        this.pickupMotor = pickupMotor;

        setArmPosition(IntakePosition.FULLY_RETRACTED);
        pickupMotor.setIdleMode(IdleMode.kBrake);

        pid = pickupMotor.getPIDController();
        pid.setOutputRange(-1, 1);

        pid.setP(KP);
        pid.setI(KI);
        pid.setD(KD);
    }

    //===== CHECKS TO SEE IF THE INTAKE ARM IS RETRACTING AND IF THE INTAKE MOTOR HAS SPUN THE CORRECT AMOUNT OF TIMES =====//
    @Override
    public void periodic() {
        if(isRetracting && targetRotations >= pickupMotor.getEncoder().getPosition()) {
            isRetracting = false;
            setPickupMotorSpeed(0);
        }
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

    //===== RECTACTS THE INTAKE ARM WHILE SPINNING THE INTAKE MOTOR FOR A BIT=====//
    public void setIntakeMode() {
        isRetracting = true;
        targetRotations = (int)pickupMotor.getEncoder().getPosition() + INTAKE_ROTATIONS;
        
        setPickupMotorSpeed(RETRACT_VELOCITY);
        setArmPosition(IntakePosition.LONG_RETRACT);
    }

    //=====RETURNS THE POSITION OF THE INTAKE ARM=====//
    public IntakePosition getIntakePosition() {
        return currentPosition;
    }

    //=====SETS THE SPEED OF THE PICKUP MOTOR IN RPMS=====//
    public void setPickupMotorSpeed(double pickUpSpeed){
        pid.setReference(pickUpSpeed, ControlType.kVelocity);
    }
}