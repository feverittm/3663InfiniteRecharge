package frc.robot.commands;

import org.frcteam2910.common.math.Vector2;
import org.frcteam2910.common.robot.input.Controller;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.drivers.Vision;
import frc.robot.subsystems.SS_Drivebase;



public class C_Track extends CommandBase {

  private Vision vision = RobotContainer.getVision();
  private SS_Drivebase drivebase = SS_Drivebase.getInstance();
  private Controller driveController = RobotContainer.getDriveController();

  private final double kP = 0.0006;
  private final double kI = 0.0;
  private final double kD = 0.0;
  
  private PIDController rotationPID = new PIDController(kP, kI, kD);

  public C_Track() {
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
    double forward = driveController.getLeftYAxis().get(true);
    double strafe = driveController.getLeftXAxis().get(true);
    drivebase.drive(new Vector2(forward, strafe), rotationPID.calculate(vision.getXOffset()), true);
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