package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Feeder;
import frc.robot.subsystems.SS_Intake;
import frc.robot.subsystems.SS_Shooter;
import frc.robot.subsystems.SS_Intake.IntakeDirection;
import frc.robot.subsystems.SS_Intake.IntakePosition;

public class C_Unjam extends CommandBase {
  private SS_Shooter shooter;
  private SS_Feeder feeder;
  private SS_Intake intake;
  public C_Unjam(SS_Shooter shooter, SS_Feeder feeder, SS_Intake intake) {
    this.shooter = shooter;
    this.feeder = feeder;
    this.intake = intake;
    addRequirements(shooter, feeder, intake);
  }

  @Override
  public void execute() {
    shooter.setSpinning(true).updateFromVision(false).testSetTargetRPM(-1000);
    feeder.setSpeed(-.4);
    intake.setMotor(IntakeDirection.OUT);
    intake.setPosition(IntakePosition.POSITION_3);
  }
  @Override
  public void end(boolean interrupted) {
    shooter.setSpinning(false);
    feeder.setSpeed(0.0);
    intake.setMotor(IntakeDirection.STOP);
    intake.setPosition(IntakePosition.POSITION_1);
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
