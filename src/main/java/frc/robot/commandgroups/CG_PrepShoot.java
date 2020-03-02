package frc.robot.commandgroups;


import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.C_LetsGetReadyToRUMBLE;
import frc.robot.commands.C_PrepFeedToShoot;
import frc.robot.commands.C_PrepShoot;
import frc.robot.subsystems.SS_Feeder;
import frc.robot.subsystems.SS_Shooter;

public class CG_PrepShoot extends SequentialCommandGroup {
  public CG_PrepShoot(SS_Feeder feeder, SS_Shooter shooter, Joystick rumbleJoystick) {
    addCommands(
      new ParallelCommandGroup(new C_PrepShoot(shooter), new C_PrepFeedToShoot(feeder))
      ,new C_LetsGetReadyToRUMBLE(rumbleJoystick, 0.05, 0.7)
    );
  }
}
