package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Shooter;

public class C_LobPrep extends CommandBase {

  SS_Shooter shooter;

  public C_LobPrep(SS_Shooter shooter) {
    this.shooter = shooter;
    addRequirements(shooter);
  }

  @Override
  public void initialize() {
    shooter.extendHood(false).setLobSpeed().setSpinning(true);
  }

  @Override
  public void execute() {
  }
  @Override
  public boolean isFinished() {
    return shooter.atCorrectRPM();
  }

  @Override
  public void end(boolean interrupted) {
  }
}
