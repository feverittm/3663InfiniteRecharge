package frc.robot.commands;

import org.frcteam2910.common.math.Vector2;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.drivers.Vision;
import frc.robot.subsystems.SS_Drivebase;



public class C_Track extends CommandBase {

  private Vision vision;

  private final double kP = 0.0006;
  private final double kI = 0.0;
  private final double kD = 0.0;
  

  private DoubleSupplier forward;
  private DoubleSupplier strafe;
  
  private SS_Drivebase drivebase;
  private PIDController rotationPID;


  public C_Track(Vision vision, SS_Drivebase drivebase, DoubleSupplier forward, DoubleSupplier strafe) {    
    this.forward = forward;
    this.strafe = strafe;
    this.drivebase = drivebase;
    this.vision = vision;

    rotationPID = new PIDController(kP, kI, kD);

    rotationPID.setSetpoint(0);
    rotationPID.setTolerance(1);
    addRequirements(drivebase);
  }

  @Override
  public void initialize() {
    vision.setLEDMode(Vision.LED_ON);
  }

  @Override
  public void execute() {
        drivebase.drive(new Vector2(forward.getAsDouble(), strafe.getAsDouble()), rotationPID.calculate(vision.getXOffset()), true);
  }

  @Override
  public void end(boolean interrupted) {
    vision.setLEDMode(Vision.LED_OFF);
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}