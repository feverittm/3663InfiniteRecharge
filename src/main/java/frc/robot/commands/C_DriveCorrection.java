package frc.robot.commands;

import java.util.function.DoubleSupplier;

import org.frcteam2910.common.math.Vector2;
import org.frcteam2910.common.robot.input.XboxController;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj.controller.PIDController;
import frc.robot.RobotContainer;
import frc.robot.subsystems.SS_Drivebase;

public class C_DriveCorrection extends CommandBase {  
  private SS_Drivebase drivebase = SS_Drivebase.getInstance();
  private XboxController driveController = (XboxController) RobotContainer.getDriveController();
  private final double DEFAULT_DEADBAND = 0.007;

  private final double KP = 0.0293; //0.0006; 
  private final double KI = 0.0;
  private final double KD = 0.0;
  private PIDController rotationPID = new PIDController(KP, KI, KD);

  public C_DriveCorrection() {
    addRequirements(drivebase);
    rotationPID.setTolerance(0.001);
  }

  @Override
  public void initialize() {
    rotationPID.setSetpoint(drivebase.getPose().rotation.toRadians());
  }

  @Override
  public void execute() {
    double forward = driveController.getLeftYAxis().get(true);
    double strafe = driveController.getLeftXAxis().get(true);
    double rotation = deadband(-driveController.getRightYAxis().get(true) * .3, DEFAULT_DEADBAND);
    if(rotation == 0) {
      rotation = rotationPID.calculate(drivebase.getPose().rotation.toRadians());
    } else {
      rotationPID.setSetpoint(drivebase.getPose().rotation.toRadians());
    }

    drivebase.drive(new Vector2(forward ,strafe), rotation,  true);
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