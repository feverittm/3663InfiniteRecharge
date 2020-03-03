package frc.robot.commandgroups;

import org.frcteam2910.common.robot.input.Controller;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.C_LetsGetReadyToRUMBLE;
import frc.robot.commands.C_LobPrep;
import frc.robot.commands.C_Shoot;
import frc.robot.subsystems.SS_Feeder;
import frc.robot.subsystems.SS_Shooter;

public class CG_LobShot extends SequentialCommandGroup {
  
  boolean pressed = false;

  public CG_LobShot(Controller controller, Joystick rumbleJoystick, SS_Shooter shooter, SS_Feeder feeder) {
    super(
      new C_LobPrep(shooter, feeder),
      new ParallelCommandGroup(new C_Shoot(feeder, shooter), new C_LetsGetReadyToRUMBLE(rumbleJoystick, 0.0, 0.3))
    );
  }
}
