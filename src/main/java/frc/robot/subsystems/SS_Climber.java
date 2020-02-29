package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class SS_Climber extends SubsystemBase {
    // for SparkMax motor controllers
    CANSparkMax gondolaMotor;
    CANSparkMax winchMotor;
    CANSparkMax hookMotor;
    
    public SS_Climber(CANSparkMax gondolaMotor, CANSparkMax winchMotor, CANSparkMax hookMotor) {
      this.gondolaMotor = gondolaMotor;
      this.winchMotor = winchMotor;
      this.hookMotor = hookMotor;
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
        hookMotor.set(rollSpeed);
        SmartDashboard.putNumber("SS_Roller rollSpeed", rollSpeed);
    }
}