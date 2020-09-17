package frc.robot.commands;

import org.frcteam2910.common.control.PidConstants;
import org.frcteam2910.common.control.PidController;
import org.frcteam2910.common.math.Vector2;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.drivers.Vision;
import frc.robot.subsystems.SS_Drivebase;

public class C_AutoAim extends CommandBase {
  private SS_Drivebase drivebase = SS_Drivebase.getInstance();
  private Vision vision = RobotContainer.getVision();

  private double kP = .0293; //TODO tune this
  private double kI = 0;
  private double kD = 0;
  private PidConstants constants = new PidConstants(kP, kI, kD);
  private PidController controller = new PidController(constants);

  private double radianTolerance = Math.toRadians(.5);
  private double lastTimeStamp;

  public C_AutoAim() {
    addRequirements(drivebase);
    controller.setOutputRange(-1.0, 1.0);
    controller.setSetpoint(0.0);
  }

  @Override
  public void initialize() {
    vision.setLEDMode(Vision.LED_ON);
  }

  @Override
  public void execute() {
    double currentTime = Timer.getFPGATimestamp();
    double dt = currentTime - lastTimeStamp;
    lastTimeStamp = currentTime;

    double rotationSpeed = controller.calculate(vision.getXOffset(), dt);
    drivebase.drive(Vector2.ZERO, rotationSpeed, true);
  }

  @Override
  public void end(boolean interrupted) {
    if(!interrupted) {
      drivebase.drive(Vector2.ZERO, 0.0, false);
    }
    vision.setLEDMode(Vision.LED_OFF);
  }

  @Override
  public boolean isFinished() {
    return Math.toRadians(vision.getXOffset()) <= radianTolerance;
  }
}
