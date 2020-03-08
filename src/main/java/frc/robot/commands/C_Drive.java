package frc.robot.commands;

import org.frcteam2910.common.math.Vector2;
import org.frcteam2910.common.robot.input.XboxController;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.SS_Drivebase;

public class C_Drive extends CommandBase {
  private SS_Drivebase drivebase = SS_Drivebase.getInstance();
  private XboxController driveController = (XboxController) RobotContainer.getDriveController();
  private XboxController operatorController = (XboxController) RobotContainer.getOperatorController();
  private final double DEFAULT_DEADBAND = .001;

  private boolean inSlowMode = false;

  public C_Drive() {    
    addRequirements(drivebase);
  }

  @Override
  public void execute() {
    double forward = driveController.getLeftYAxis().get(true);
    double strafe = driveController.getLeftXAxis().get(true);
    double rotation = -driveController.getRightXAxis().get(true) * .3;
    if(operatorController.getBackButton().get()) {
      inSlowMode = true;
    }
    if(inSlowMode) {
      drivebase.drive(new Vector2(forward * .3,strafe * .3), deadband(rotation, DEFAULT_DEADBAND) * .3,  true);
    } else {
      drivebase.drive(new Vector2(forward, strafe), deadband(rotation, DEFAULT_DEADBAND),  true);
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