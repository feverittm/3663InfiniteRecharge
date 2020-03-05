package frc.robot.commandgroups;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.C_LetsGetReadyToRUMBLE;
import frc.robot.commands.C_LobPrep;
import frc.robot.commands.C_PrepFeedToShoot;
import frc.robot.subsystems.SS_Feeder;
import frc.robot.subsystems.SS_Shooter;

public class CG_LobShot extends SequentialCommandGroup {
  public CG_LobShot(Joystick rumbleJoystick, SS_Shooter shooter, SS_Feeder feeder) {
    super(
      new ParallelCommandGroup(new C_LobPrep(shooter), new C_PrepFeedToShoot(feeder)),
      new C_LetsGetReadyToRUMBLE(rumbleJoystick, 0.01, 0.7)
    );
  }
}
