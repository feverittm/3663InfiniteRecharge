/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commandgroups;

import org.frcteam2910.common.robot.input.Controller;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.SS_Feeder;
import frc.robot.subsystems.SS_Shooter;
import frc.robot.test.C_PrepIntakeShoot;
import frc.robot.test.C_PrepShoot;
import frc.robot.test.C_RPMTuneTest;
import frc.robot.test.C_Shoot;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/latest/docs/software/commandbased/convenience-features.html
public class CG_ShootBalls extends SequentialCommandGroup {
  /**
   * Creates a new C_ShootBalls.
   */
  public CG_ShootBalls(SS_Feeder feeder, SS_Shooter shooter, Controller controller) {
    addCommands(
      new ParallelCommandGroup(new C_PrepShoot(shooter), new C_PrepIntakeShoot(feeder)),
      new C_Shoot(feeder, controller)
    );
  }
}
