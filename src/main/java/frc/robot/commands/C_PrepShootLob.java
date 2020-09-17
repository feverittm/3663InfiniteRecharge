package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Shooter;

public class C_PrepShootLob extends CommandBase {

  private SS_Shooter shooter = SS_Shooter.getInstance();

  public C_PrepShootLob() {
    addRequirements(shooter);
  }

  @Override
  public void initialize() {
    shooter.extendHood(false).setLobSpeed().setSpinning(true);
  }

  @Override
  public boolean isFinished() {
    return shooter.atCorrectRPM();
  }
}
