package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.wpilibj.DoubleSolenoid;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SS_Intake extends SubsystemBase {
    /**
     * Intake arm extend positions
     * 
     * @param POSITION_0 checks to see if both the long and short solenoids are
     *                   retracted
     * @param POSITION_1 checks to see if ONLY the long solenoid is retracted
     * @param POSITION_2 checks to see if ONLY the short solenoid is retracted
     * @param POSITION_3 checks to see if both the long and short solenoids are
     *                   extended
     */
    public enum IntakePosition {
        POSITION_0, // This position retracts both the long and short solenoids for putting the
                    // intake arm within the frame perimeter.
        POSITION_1, // This position ONLY retracts the long solenoid for inserting power cells into
                    // the magazine.
        POSITION_2, // This position ONLY retracts the short solenoid for holding power cells in the
                    // intake arm.
        POSITION_3 // This position extends both the long and short solenoids for intaking power
                   // cells.
    }

    /**
     * Intake motor directions
     * 
     * @param STOP turns off the intake motor
     * @param IN   changes the direction of the intake motor to intake power cells
     * @param OUT  changes the direction of the intake motor to spit out power cells
     */
    public enum IntakeDirection {
        STOP, // This turns off the intake motor, stopping it from spinning
        IN, // This changes the direction of the motor to intake power cells
        OUT // This changes the direction of the motor to spit out power cells
    }

    // =====INSTANCE VARIABLES=====//
    private DoubleSolenoid shortSolenoid;
    private DoubleSolenoid longSolenoid;
    private CANSparkMax pickupMotor;

    private CANPIDController pid;

    private IntakePosition currentPosition;

    private boolean isRetracting = false;
    private int targetRotations = 0;
    private final int INTAKE_ROTATIONS = 10;

    private final double KP = 0.0001;
    private final double KI = 0.000001;
    private final double KD = 0;

    private final int RETRACT_VELOCITY = 2000;

    // private int lastVelocity = 0;
    private final int INTAKE_VELOCITY = 3000;
    private final int OUTTAKE_VELOCITY = -3000;

    // =====CONSTRUCTOR=====//
    public SS_Intake(DoubleSolenoid shortSolenoid, DoubleSolenoid longSolenoid, CANSparkMax pickupMotor) {
        this.shortSolenoid = shortSolenoid;
        this.longSolenoid = longSolenoid;
        this.pickupMotor = pickupMotor;

        setArmPosition(IntakePosition.POSITION_0);
        pickupMotor.setIdleMode(IdleMode.kCoast);
        pickupMotor.setSmartCurrentLimit(23);

        pid = pickupMotor.getPIDController();
        pid.setOutputRange(-1, 1);

        pid.setP(KP);
        pid.setI(KI);
        pid.setD(KD);
    }

    // =====CHECKS TO SEE IF THE INTAKE ARM IS RETRACTING AND IF THE INTAKE MOTOR
    // HAS SPUN THE CORRECT AMOUNT OF TIMES=====//
    @Override
    public void periodic() {
        if (isRetracting && pickupMotor.getEncoder().getPosition() >= targetRotations) {
            isRetracting = false;
            setPickUpMotorSpeed(0);
        }
    }

    // =====SETS THE INTAKE ARM POSITION=====//
    public void setArmPosition(IntakePosition position) {
        // Depending on the button pressed, this command sets the intake arm position.
        currentPosition = position;
        switch (position) {
        case POSITION_0:
            shortSolenoid.set(DoubleSolenoid.Value.kReverse);
            longSolenoid.set(DoubleSolenoid.Value.kReverse);
            break;

        case POSITION_1:
            shortSolenoid.set(DoubleSolenoid.Value.kForward);
            longSolenoid.set(DoubleSolenoid.Value.kReverse);
            break;

        case POSITION_2:
            shortSolenoid.set(DoubleSolenoid.Value.kReverse);
            longSolenoid.set(DoubleSolenoid.Value.kForward);
            break;

        case POSITION_3:
            shortSolenoid.set(DoubleSolenoid.Value.kForward);
            longSolenoid.set(DoubleSolenoid.Value.kForward);
            break;
        }
    }

    // =====RETRACTS THE INTAKE ARM WHILE SPINNING THE INTAKE MOTOR FOR A BIT=====//
    public void retractIntake() {
        isRetracting = true;
        targetRotations = (int) pickupMotor.getEncoder().getPosition() + INTAKE_ROTATIONS;

        setPickUpMotorSpeed(0);
        setArmPosition(IntakePosition.POSITION_1);
    }

    // =====RETURNS THE POSITION OF THE INTAKE ARM=====//
    public IntakePosition getIntakePosition() {
        return currentPosition;
    }

    // =====SETS THE SPEED OF THE PICKUP MOTOR IN RPMS=====//
    private void setPickUpMotorSpeed(int pickUpSpeed) {
        pid.setReference(pickUpSpeed, ControlType.kVelocity);
    }

    // =====STARTS THE INTAKE MOTOR BY SETTTING THE DIRECTION OF THE INTAKE MOTOR
    // USING setPickUpMotorSpeed()=====//
    public void startPickUpMotor(IntakeDirection direction) {
        switch (direction) {
        case STOP:
            setPickUpMotorSpeed(0);
            break;

        case IN:
            setPickUpMotorSpeed(INTAKE_VELOCITY);
            break;

        case OUT:
            setPickUpMotorSpeed(OUTTAKE_VELOCITY);
            break;
        }
    }
}