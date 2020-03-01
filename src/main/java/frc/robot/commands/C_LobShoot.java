package frc.robot.commands;

import org.frcteam2910.common.robot.input.Controller;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SS_Feeder;
import frc.robot.subsystems.SS_Feeder.FeedMode;

public class C_LobShoot extends CommandBase {

  Controller controller;
  SS_Feeder feeder;

  private boolean pressed = false;

  public C_LobShoot(Controller controller, SS_Feeder feeder) {
    addRequirements(feeder);
    this.controller = controller;
    this.feeder = feeder;
  }

  @Override
  public void execute() {
    if(controller.getAButton().get() && !pressed) {
      feeder.setFeedMode(FeedMode.SHOOT);
      pressed = true;
    }
  }

  @Override
  public boolean isFinished() {
    return feeder.isIdle();
  }
}
