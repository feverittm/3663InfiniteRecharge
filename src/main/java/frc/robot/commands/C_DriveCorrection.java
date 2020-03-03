package frc.robot.commands;

import java.util.function.DoubleSupplier;

import org.frcteam2910.common.math.Vector2;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj.controller.PIDController;
import frc.robot.subsystems.SS_Drivebase;

public class C_DriveCorrection extends CommandBase {
  
  private SS_Drivebase drivebase;
  private final double DEFAULT_DEADBAND = 0.007;

  private final double KP = 0.0293; //0.0006; 
  private final double KI = 0.0;
  private final double KD = 0.0;
  
  private PIDController rotationPID;
  

  private DoubleSupplier forward;
  private DoubleSupplier strafe;
  private DoubleSupplier rotation;

  public C_DriveCorrection(SS_Drivebase drivebase, DoubleSupplier forward, DoubleSupplier strafe, DoubleSupplier rotation) {
    this.drivebase = drivebase;
    this.forward = forward;
    this.strafe = strafe;
    this.rotation = rotation;
    addRequirements(drivebase);

    rotationPID = new PIDController(KP, KI, KD);
    rotationPID.setTolerance(0.001);
  }

  @Override
  public void initialize() {
    rotationPID.setSetpoint(drivebase.getPose().rotation.toRadians());
  }

  @Override
  public void execute() {
    double rotate = deadband(rotation.getAsDouble(), DEFAULT_DEADBAND, true);

    if(rotate == 0) {
      rotate = rotationPID.calculate(drivebase.getPose().rotation.toRadians());
    } else {
      rotationPID.setSetpoint(drivebase.getPose().rotation.toRadians());
    }

    drivebase.drive(new Vector2(forward.getAsDouble(),strafe.getAsDouble()), rotate,  true);
  }

  @Override
  public boolean isFinished() {
    return false;
  }

  @Override
  public void end(boolean interrupted) {
    drivebase.drive(Vector2.ZERO, 0.0, false);
  }

  /**
   * @param input controller axis input, from -1 to 1
   * @param range absolute range to deadband, usually around .05 to .20
   * @return deadbanded input
   */
  private double deadband(double input, double range) {
    if(Math.abs(input) < Math.abs(range)) {
      return 0;
    } 
    return input;
  }

  private double deadband(double input, double range, boolean squared) {
    if(Math.abs(input) < Math.abs(range)) {
      return 0;
    }
    return Math.pow(input, 2) * Math.signum(input);
  }
}