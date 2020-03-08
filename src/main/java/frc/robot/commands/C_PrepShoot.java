package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Shooter;

public class C_PrepShoot extends CommandBase {

  private SS_Shooter shooter = SS_Shooter.getInstance();

  public C_PrepShoot() {
    addRequirements(shooter);
  }

  @Override
  public void initialize() {
    shooter.updateFromVision(true).setSpinning(true).extendHood(true);
  }

  @Override
  public boolean isFinished() {
    return shooter.atCorrectRPM();
  }
}
