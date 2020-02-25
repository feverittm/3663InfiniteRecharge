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
import frc.robot.commands.C_PrepIntakeShoot;
import frc.robot.commands.C_PrepShoot;
import frc.robot.subsystems.SS_Feeder;
import frc.robot.subsystems.SS_Shooter;
import frc.robot.commands.C_Shoot;

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
