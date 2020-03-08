package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class SS_Climber extends SubsystemBase {

    private static SS_Climber instance;

    public static SS_Climber getInstance() {
        if(instance == null) {
            instance = new SS_Climber();
        }
        return instance;
    }
    private CANSparkMax gondolaMotor = new CANSparkMax(Constants.CLIMBER_GONDOLA_MOTOR_CANID, MotorType.kBrushless);
    private CANSparkMax winchMotor = new CANSparkMax(Constants.CLIMBER_WINCH_MOTOR_CANID, MotorType.kBrushless);
    private CANSparkMax extendMotor = new CANSparkMax(Constants.CLIMBER_EXTEND_MOTOR_CANID, MotorType.kBrushless);
    private TalonSRX retractMotor = new TalonSRX(Constants.CLIMBER_RETRACT_MOTOR_CANID);

    private CANPIDController extendPID;
    private final double P = 0.01;
    private final double I = 0;
    private final double D = 0;

    private NetworkTableEntry hookPos;

    public SS_Climber() {
        extendPID = extendMotor.getPIDController();
        extendPID.setP(P);
        extendPID.setI(I);
        extendPID.setD(D);

        extendMotor.setIdleMode(IdleMode.kBrake);

        retractMotor.setNeutralMode(NeutralMode.Brake);
        retractMotor.setInverted(true);

        winchMotor.setIdleMode(IdleMode.kBrake);

        initTelemetry();
    }

    private void initTelemetry() {
        ShuffleboardTab climberTab = Shuffleboard.getTab("Shooter");
        hookPos = climberTab.add("Hook Position", 0).withPosition(3, 3).withSize(1, 1).getEntry();
    }

    @Override
    public void periodic() {
        hookPos.setNumber(getHookPosition());
    }
    public void setRetractMotorSpeed(double speed){
        retractMotor.set(ControlMode.PercentOutput, speed);
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

    public void setGondolaMotorSpeed(double speed) {
        gondolaMotor.set(speed);
    }

    public void setWinchMotorSpeed(double speed) {
        winchMotor.set(speed);
    }

    public void setHookMotorSpeed(double speed) {
        extendMotor.set(speed);
    }

}