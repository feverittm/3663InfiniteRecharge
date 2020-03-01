package frc.robot.commands;

import java.util.function.DoubleSupplier;

import org.frcteam2910.common.math.Vector2;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj.controller.PIDController;
import frc.robot.drivers.NavX;
import frc.robot.drivers.NavX.Axis;
import frc.robot.subsystems.SS_Drivebase;

public class C_DriveCorrection extends CommandBase {
  
  private SS_Drivebase drivebase;
  private NavX navx;
  private final double DEFAULT_DEADBAND = .001;

  private double lastRotation = 0;

  private final double KP = 0.0006;
  private final double KI = 0.0;
  
  private PIDController rotationPID;
  

  private DoubleSupplier forward;
  private DoubleSupplier strafe;
  private DoubleSupplier rotation;

  public C_DriveCorrection(SS_Drivebase drivebase, DoubleSupplier forward, DoubleSupplier strafe, DoubleSupplier rotation, NavX navx) {
    this.drivebase = drivebase;
    this.forward = forward;
    this.strafe = strafe;
    this.rotation = rotation;
    this.navx = navx;
    addRequirements(drivebase);

    rotationPID = new PIDController(KP, KI, 0);
    rotationPID.setSetpoint(0);
    rotationPID.setTolerance(1);
  }

  @Override
  public void initialize() {
    lastRotation = navx.getAxis(Axis.YAW);
  }

  @Override
  public void execute() {
    double rotate = deadband(rotation.getAsDouble(), DEFAULT_DEADBAND, true);

    if(rotate == 0) {
      rotate = rotationPID.calculate(lastRotation - navx.getAxis(Axis.YAW));
    } else {
      lastRotation = navx.getAxis(Axis.YAW);
    }

    drivebase.drive(new Vector2(deadband(forward.getAsDouble(), DEFAULT_DEADBAND, true), 
      deadband(strafe.getAsDouble(), DEFAULT_DEADBAND, true)), rotate,  true);
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