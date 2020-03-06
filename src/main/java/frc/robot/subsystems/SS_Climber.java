package frc.robot.subsystems;

import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SS_Climber extends SubsystemBase {
    // for SparkMax motor controllers
    CANSparkMax gondolaMotor;
    CANSparkMax winchMotor;
    CANSparkMax extendMotor;
    CANSparkMax retractMotor;

    CANPIDController extendPID;
    private final double P = 0.01;
    private final double I = 0;
    private final double D = 0;

    private NetworkTableEntry hookPos;

    public SS_Climber(CANSparkMax gondolaMotor, CANSparkMax winchMotor, CANSparkMax extendMotor, CANSparkMax retractMotor) {
        this.gondolaMotor = gondolaMotor;
        this.winchMotor = winchMotor;
        this.extendMotor = extendMotor;
        this.retractMotor = retractMotor;

        extendPID = extendMotor.getPIDController();
        extendPID.setP(P);
        extendPID.setI(I);
        extendPID.setD(D);
        extendMotor.setIdleMode(IdleMode.kBrake);
        retractMotor.setIdleMode(IdleMode.kBrake);
        winchMotor.setIdleMode(IdleMode.kBrake);
        intiTelemetry();
    }

    private void intiTelemetry() {
        ShuffleboardTab climberTab = Shuffleboard.getTab("Shooter");
        hookPos = climberTab.add("Hook Position", 0).withPosition(3, 3).withSize(1, 1).getEntry();
    }

    @Override
    public void periodic() {
        hookPos.setNumber(getHookPosition());
    }
    public void setRetractMotorSpeed(double speed){
        retractMotor.set(speed);
    }
    public void resetHookEncoder() {
        extendMotor.getEncoder().setPosition(0.0);
    }

    public double getHookPosition() {
        return extendMotor.getEncoder().getPosition();
    }

    public void setHookPosition(double position) {
        extendPID.setReference(position, ControlType.kPosition);
    }

    public void gondolaRoll(double rollSpeed) {
        // for SparkMax motor controllers
        gondolaMotor.set(rollSpeed);
        SmartDashboard.putNumber("SS_Roller rollSpeed", rollSpeed);
    }

    public void setWinch(double rollSpeed) {
        // for SparkMax motor controllers
        winchMotor.set(rollSpeed);
        SmartDashboard.putNumber("SS_Roller rollSpeed", rollSpeed);
    }

    public void setHook(double rollSpeed) {
        // for SparkMax motor controllers
        extendMotor.set(rollSpeed);
        SmartDashboard.putNumber("SS_Roller rollSpeed", rollSpeed);
    }

}