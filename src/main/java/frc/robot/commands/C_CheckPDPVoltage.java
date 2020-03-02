package frc.robot.commands;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class C_CheckPDPVoltage extends CommandBase {
  private PowerDistributionPanel PDP;
  private Joystick operatorRumbleJoystick;
  private Timer timer;
  private NetworkTableEntry pdpVoltage;
  private final double PDP_MIN_VOLTAGE_THRESHOLD = 11.5;

  public C_CheckPDPVoltage(PowerDistributionPanel PDP, Joystick operatorRumbleJoystick) {
    this.PDP = PDP;
    this.operatorRumbleJoystick = operatorRumbleJoystick;
    timer = new Timer();
  }

  @Override
  public void initialize() {
    ShuffleboardTab driveBaseTab = Shuffleboard.getTab("Shooter");
    pdpVoltage = driveBaseTab.add("PDP Voltage", 0).withPosition(4, 4).withSize(1, 1).getEntry();
  }

  @Override
  public void execute() {
    pdpVoltage.setNumber(PDP.getVoltage());
    if (PDP.getVoltage() < PDP_MIN_VOLTAGE_THRESHOLD) {
      timer.start();
    } else {
      timer.reset();
    }

    if (timer.get() > 5) {
      new C_LetsGetReadyToRUMBLE(operatorRumbleJoystick, 10, 1, 1).schedule();
    }

  }

  @Override
  public void end(boolean interrupted) {
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
