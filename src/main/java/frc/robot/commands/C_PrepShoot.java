
package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Shooter;

public class C_PrepShoot extends CommandBase {

  private SS_Shooter shooter;

  public C_PrepShoot(SS_Shooter shooter) {
    this.shooter = shooter;
    addRequirements(shooter);
  }

  @Override
  public void initialize() {
    shooter.updateFromVision(true).setSpinning(true).extendHood(true);
    // shooter.setSpinning(true).setHoodFar(false).testSetTargetRPM(1000);
  }

  @Override
  public void execute() {
  }

  @Override
  public void end(boolean interrupted) {
  }

  @Override
  public boolean isFinished() {
    return shooter.atCorrectRPM();
  }
}
