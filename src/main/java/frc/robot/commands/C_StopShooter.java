package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Shooter;

public class C_StopShooter extends CommandBase {
  private SS_Shooter shooter;
  public C_StopShooter(SS_Shooter shooter) {
    this.shooter = shooter;
    addRequirements(shooter);
  }

  @Override
  public void initialize() {
    shooter.setSpinning(false).extendHood(false);
  }
  
  @Override
  public boolean isFinished() {
    return true;
  }
}
