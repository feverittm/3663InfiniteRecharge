package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class SS_Intake extends SubsystemBase {
    /**
     * Intake arm extend positions
     * 
     * @param POSITION_0 both the long and short solenoids are retracted
     * @param POSITION_1 ONLY the long solenoid is retracted
     * @param POSITION_2 ONLY the short solenoid is retracted
     * @param POSITION_3 both the long and short solenoids are extended
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
        OUT, // This changes the direction of the motor to spit out power cells
        BUMP
    }

    private DoubleSolenoid shortSolenoid;
    private DoubleSolenoid longSolenoid;
    private CANSparkMax pickupMotor;
    private DigitalInput intakeSensor = new DigitalInput(Constants.INTAKE_SENSOR);

    private CANPIDController velocityController;

    private IntakePosition currentPosition;

    private boolean isRetracting = false;
    private int targetRotations = 0;
    private final int INTAKE_ROTATIONS = 10;

    private final double KP = 0.0001;
    private final double KI = 0.000001;
    private final double KD = 0;

    private final int RETRACT_VELOCITY = 2000;

    //private int lastVelocity = 0;
    private final int INTAKE_VELOCITY = 3500;
    private final int OUTTAKE_VELOCITY = -3000;
    private final int BUMP_VELOCITY = 1000;
    private final int MOTOR_CURRENT_LIMIT = 25;

    private NetworkTableEntry intakeEntry;
    
    public SS_Intake(DoubleSolenoid shortSolenoid, DoubleSolenoid longSolenoid, CANSparkMax pickupMotor) { 
        this.shortSolenoid = shortSolenoid;
        this.longSolenoid = longSolenoid;
        this.pickupMotor = pickupMotor;

        setPosition(IntakePosition.POSITION_0);
        pickupMotor.setIdleMode(IdleMode.kCoast);
        pickupMotor.setSmartCurrentLimit(MOTOR_CURRENT_LIMIT);

        velocityController = pickupMotor.getPIDController();
        velocityController.setOutputRange(-1, 1);
        velocityController.setIMaxAccum(0.8, 0);

        velocityController.setP(KP);
        velocityController.setI(KI);
        velocityController.setD(KD);
        ShuffleboardTab intakeTab = Shuffleboard.getTab("Camera");
        intakeEntry = intakeTab.add("IAccum", 0)
            .withPosition(6, 3)
            .withSize(1, 1)
            .getEntry();
    }

    public DigitalInput getIntakeSensor() {
        return intakeSensor;
    }

    //CHECKS TO SEE IF THE INTAKE ARM IS RETRACTING AND IF THE INTAKE MOTOR HAS SPUN THE CORRECT AMOUNT OF TIMES
    @Override
    public void periodic() {
        if (isRetracting && pickupMotor.getEncoder().getPosition() >= targetRotations) {
            isRetracting = false;
            setMotor(0);
        }
        intakeEntry.setNumber(velocityController.getIAccum());
    }

    public void setPosition(IntakePosition position) {
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

    public void retract() {
        setPosition(IntakePosition.POSITION_1);
    }

    public IntakePosition getPosition() {
        return currentPosition;
    }

    private void setMotor(double rpms) {
        velocityController.setReference(rpms, ControlType.kVelocity);
    }

    /**
     * @param direction the direction to spin the intake motor
     */
    public void setMotor(IntakeDirection direction) {
        switch(direction) {
            case STOP:
                pickupMotor.set(0);
                break;

            case IN:
                setMotor(INTAKE_VELOCITY);
                break;

            case OUT:
                setMotor(OUTTAKE_VELOCITY);
                break;
            case BUMP:
                setMotor(BUMP_VELOCITY);
                break;
        }
    }
}