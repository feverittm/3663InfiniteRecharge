package frc.robot.commands;

import java.util.function.BooleanSupplier;
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
  private BooleanSupplier slowMode;
  private boolean inSlowMode = false;

  public C_Drive(SS_Drivebase drivebase, DoubleSupplier forward, DoubleSupplier strafe, DoubleSupplier rotation, BooleanSupplier slowMode) {
    this.drivebase = drivebase;
    this.forward = forward;
    this.strafe = strafe;
    this.rotation = rotation;
    this.slowMode = slowMode;
    
    addRequirements(drivebase);
  }

  @Override
  public void execute() {
    if(slowMode.getAsBoolean()) {
      inSlowMode = true;
    }
    if(inSlowMode) {
      drivebase.drive(new Vector2(forward.getAsDouble() * .3,strafe.getAsDouble() * .3), deadband(rotation.getAsDouble(), DEFAULT_DEADBAND) * .3,  true);
    } else {
      drivebase.drive(new Vector2(forward.getAsDouble(), strafe.getAsDouble()), deadband(rotation.getAsDouble(), DEFAULT_DEADBAND),  true);
    }
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
}