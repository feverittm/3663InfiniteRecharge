package frc.robot.commands;

import org.frcteam2910.common.control.PidConstants;
import org.frcteam2910.common.control.PidController;
import org.frcteam2910.common.math.RigidTransform2;
import org.frcteam2910.common.math.Vector2;
import org.frcteam2910.common.util.HolonomicDriveSignal;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Drivebase;

public class C_AutoDrive extends CommandBase {

  private SS_Drivebase drivebase = SS_Drivebase.getInstance();
  private Vector2 targetTranslation;
  private double targetRotation;

  private double translationKp = .013;
  private double translationKi = 0;
  private double translationKd = 0;
  private PidConstants translationConstants = new PidConstants(translationKp, translationKi, translationKd);

  private double rotationKp = .0293;
  private double rotationKi = 0;
  private double rotationKd = 0;
  private PidConstants rotationConstants = new PidConstants(rotationKp, rotationKi, rotationKd);

  private PidController translationController = new PidController(translationConstants);
  private double translationPercentTolerance = .025;

  private PidController rotationController = new PidController(rotationConstants);
  private double rotationPercentTolerance = .01;

  private double lastTimeStamp;
  private double currentAngle;
  private RigidTransform2 currentPose;

  private double translationSpeed;
  private double rotationSpeed;

  /**
   * @param drivebase the drivebase subsystem
   * @param targetTranslation inches for the robot to travel, negative inches are backwards
   * @param translationPercentOutput the maximum percent output for translation
   * @param rotation angle to turn to in radians
   * @param rotationPercentOutput the maximum percent output for rotation
   */
  public C_AutoDrive(Vector2 targetTranslation, double translationPercentOutput, double targetRotation, double rotationPercentOutput) {
    this.targetTranslation = targetTranslation;
    this.targetRotation = targetRotation;
    addRequirements(drivebase);

    translationController.setSetpoint(targetTranslation.length);
    translationController.setOutputRange(-translationPercentOutput, translationPercentOutput);
    
    rotationController.setSetpoint(targetRotation);
    rotationController.setInputRange(0, 2 * Math.PI);
    rotationController.setContinuous(true);
    rotationController.setOutputRange(-rotationPercentOutput, rotationPercentOutput);
  }

  @Override
  public void initialize(){
    drivebase.resetPoseTranslation();
  }

  @Override
  public void execute() {
    double currentTime = Timer.getFPGATimestamp();
    double dt = currentTime - lastTimeStamp;
    lastTimeStamp = currentTime;
    currentPose = drivebase.getPose();
    currentAngle = currentPose.rotation.toRadians();

    translationSpeed = translationController.calculate(Math.hypot(currentPose.translation.x, currentPose.translation.y), dt);
    Vector2 translationVector = Vector2.fromAngle(targetTranslation.getAngle()).normal().scale(translationSpeed);

    rotationSpeed = rotationController.calculate(currentAngle, dt);
    
    drivebase.drive(translationVector, rotationSpeed, true);
  }

  @Override
  public void end(boolean interrupted) {
    drivebase.drive(new HolonomicDriveSignal(new Vector2(0.0, 0.0), 0.0, false));
  }

  @Override
  public boolean isFinished() {
    return ((Math.abs(targetRotation - currentAngle) <= 2 * Math.PI * rotationPercentTolerance ||
        targetRotation == 0.0) &&
        (Math.abs(targetTranslation.y - currentPose.translation.y) <= targetTranslation.y * translationPercentTolerance ||
        targetTranslation.y == 0.0)&&
        (Math.abs(targetTranslation.x - currentPose.translation.x) <= targetTranslation.x * translationPercentTolerance ||
        targetTranslation.x == 0.0))
        ||(translationSpeed <= .001 && rotationSpeed <= .0005);
  }
}