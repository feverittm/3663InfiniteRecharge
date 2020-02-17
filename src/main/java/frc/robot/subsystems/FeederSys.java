/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.drivers.TimeOfFlightSensor;

enum FeedRate {
  STOPPED,
  INDEX_IN,
  INDEX_OUT,
  SHOOT
}

public class FeederSys extends SubsystemBase {

  private final double FEEDER_BELT_GEAR_RATIO_MULTIPLIER = 1;

  //Feeder PID constants
  private final double KP = 0.0001;
  private final double KI = 0.0000005;
  private final double KD = 0;

  // this is sets the inches per Revelushion.
  private double INCHES_PER_REVOLUTION = 1;

  private final int FEED_RPM_STOPPED = 0;
  private final int FEED_RPM_SHOOT = 100; //how fast the feeder should be running when we are shooting
  private final int FEED_RPM_INDEX_IN = 100; //how fast the feeder should be running when indexing the balls
  private final int FEED_RPM_INDEX_OUT = 100;


  public final int REV_PER_FULL_FEED = 1500; //amount of revolutions before the feeder fully indexes all balls

  private final double BALL_PRESENT_THRESHOLD = 30;

  private CANEncoder feederBeltEncoder;

  private CANSparkMax belt;
  private CANPIDController beltPID;

  private TimeOfFlightSensor entrySensor;
  private TimeOfFlightSensor exitSensor;

  private FeedRate curFeedRate;

  private int targetFeedRpm; 
  

  //network table entries for telemetry
  private NetworkTableEntry feedRateEntry;
  private NetworkTableEntry ballInEntranceEntry;
  private NetworkTableEntry ballInExitEntry;
  private NetworkTableEntry feederRPMEntry;
  private NetworkTableEntry exitRangeEntry;
  private NetworkTableEntry entryRangeEntry;

  public FeederSys() {
    belt = new CANSparkMax(Constants.FEED_MOTOR_CANID, MotorType.kBrushless);
    belt.setIdleMode(IdleMode.kBrake);
    beltPID = belt.getPIDController();
    beltPID.setP(KP);
    beltPID.setI(KI);
    beltPID.setD(KD);
    feederBeltEncoder = belt.getEncoder();
    feederBeltEncoder.setVelocityConversionFactor(FEEDER_BELT_GEAR_RATIO_MULTIPLIER); //set feeder gear ratio

    //Sensors for Feeder
    entrySensor = new TimeOfFlightSensor(Constants.ENTRY_SENSOR_CANID);
    exitSensor = new TimeOfFlightSensor(Constants.EXIT_SENSOR_CANID);
    initTelemetry();
  }

  private void initTelemetry() {
    ShuffleboardTab shooterTab = Shuffleboard.getTab("Shooter"); //use the same tab as the shooter for displaying data

    feedRateEntry = shooterTab.add("Feed Rate", "unassigned")
      .withPosition(3, 0)
      .withSize(1, 1)
      .getEntry();
      feederRPMEntry = shooterTab.add("Feeder RPM", 0)
      .withPosition(3, 1)
      .withSize(1, 1)
      .getEntry();
    ballInEntranceEntry = shooterTab.add("Ball in entrance", false)
      .withWidget("Boolean Box")
      .withPosition(4, 0)
      .withSize(1, 1)
      .getEntry();
    ballInExitEntry = shooterTab.add("ball in exit", false)
      .withWidget("Boolean Box")
      .withPosition(4, 1)
      .withSize(1, 1)
      .getEntry();
    entryRangeEntry = shooterTab.add("Entry range", 0)
      .withPosition(5, 0)
      .withSize(1, 1)
      .getEntry();
    exitRangeEntry = shooterTab.add("Exit range", 0)
      .withPosition(5, 1)
      .withSize(1, 1)
      .getEntry();
  }

  @Override
  public void periodic() {
    updateTelemetry();
  }

  private void updateTelemetry() {
    // feedRateEntry.setString(feedRate.toString());
    feederRPMEntry.setNumber(belt.getEncoder().getVelocity());
    ballInEntranceEntry.setBoolean(ballInExit());
    ballInExitEntry.setBoolean(ballInEntry());
    entryRangeEntry.setNumber(getEntryRange());
    exitRangeEntry.setNumber(getExitRange());
  }


  /**
   * Set the RPM of the feeder belt based on the FeedRate enum
   * @param rate the rate for the belt
   */
  public void setFeedRate(FeedRate rate) {
    
    switch(rate) {
      case STOPPED:
        targetFeedRpm = FEED_RPM_STOPPED;
      case INDEX_IN:
        targetFeedRpm = FEED_RPM_INDEX_IN;
        break;
      case INDEX_OUT:
        targetFeedRpm = -FEED_RPM_INDEX_OUT;
        break;
      case SHOOT:
        targetFeedRpm = FEED_RPM_SHOOT;
        break;
      
    }
    beltPID.setReference(targetFeedRpm, ControlType.kVelocity);
  }

  public void resetBeltPosision() {
    belt.getEncoder().setPosition(0);
  }

  public double getBeltDistance() {

    return belt.getEncoder().getPosition() * INCHES_PER_REVOLUTION;
  }

  public double getEntryRange() {
    return entrySensor.getDistance();
  }

  public double getExitRange() {
    return exitSensor.getDistance();
  }

  public boolean ballInEntry() {
    return entrySensor.getDistance() <= BALL_PRESENT_THRESHOLD;
  }

  public boolean ballInExit() {
    return exitSensor.getDistance() <= BALL_PRESENT_THRESHOLD;
  }
}
