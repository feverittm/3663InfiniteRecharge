package frc.robot.commandgroups;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.RobotContainer;
import frc.robot.commands.C_LetsGetReadyToRUMBLE;
import frc.robot.commands.C_PrepShootLob;
import frc.robot.commands.C_PrepFeederToShoot;

public class CG_PrepLobShot extends SequentialCommandGroup {
  public CG_PrepLobShot() {
    super(
      new ParallelCommandGroup(
        new C_PrepShootLob(), 
        new C_PrepFeederToShoot()
      ),
      new C_LetsGetReadyToRUMBLE(RobotContainer.getRumbleJoystick(), 0.01, 0.7)
    );
  }
}
