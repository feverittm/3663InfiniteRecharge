package frc.robot.commands;

import java.util.function.DoubleSupplier;

import org.frcteam2910.common.math.Vector2;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Drivebase;

public class C_Drive extends CommandBase {
  private SS_Drivebase drivebase;
  private final double DEFAULT_DEADBAND = .001;

  private DoubleSupplier forward;
  private DoubleSupplier strafe;
  private DoubleSupplier rotation;

  public C_Drive(SS_Drivebase drivebase, DoubleSupplier forward, DoubleSupplier strafe, DoubleSupplier rotation) {
    this.drivebase = drivebase;
    this.forward = forward;
    this.strafe = strafe;
    this.rotation = rotation;
    
    addRequirements(drivebase);
  }

  @Override
  public void execute() {
    drivebase.drive(new Vector2(deadband(forward.getAsDouble(), DEFAULT_DEADBAND, true), 
      deadband(strafe.getAsDouble(), DEFAULT_DEADBAND, true)), deadband(rotation.getAsDouble(), DEFAULT_DEADBAND, true),  true);
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