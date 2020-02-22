/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.RobotContainer;
import frc.robot.test.C_FeederTest;
import frc.robot.drivers.TimeOfFlightSensor;
import frc.robot.subsystems.SS_Feeder;

public class Robot extends TimedRobot {
  private final RobotContainer container = new RobotContainer();
  
  // private CANSparkMax beltMotor = new CANSparkMax(Constants.FEED_MOTOR_CANID, MotorType.kBrushless);
  // private TimeOfFlightSensor entrySensor = new TimeOfFlightSensor(Constants.ENTRY_SENSOR_CANID);
  // private TimeOfFlightSensor exitSensor = new TimeOfFlightSensor(Constants.EXIT_SENSOR_CANID);
  // private SS_Feeder feeder = new SS_Feeder(beltMotor, entrySensor, exitSensor);
  @Override
  public void robotInit() {
  }

  @Override
  public void robotPeriodic() {
    //CommandScheduler.getInstance().schedule(new C_FeederTest(feeder));
    CommandScheduler.getInstance().run();
  }
}
