/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import java.util.Dictionary;
import java.util.HashMap;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.playingwithfusion.TimeOfFlight;
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

enum FeedMode {
  STOPPED,
  INTAKE,
  SHOOT
}

public class FeederSys extends SubsystemBase {

  private final double FEEDER_BELT_GEAR_RATIO_MULTIPLIER = 1;

  //Feeder PID constants
  private final double KP = 0.0001;
  private final double KI = 0.0000005;
  private final double KD = 0;

  private final int FEED_RPM_STOPPED = 0;
  private final int FEED_RPM_SHOOT = 100; //how fast the feeder should be running when we are shooting
  private final int FEED_RPM_INTAKE = 100; //how fast the feeder should be running when indexing the balls

  public final int REV_PER_FULL_FEED = 1500; //amount of revolutions before the feeder fully indexes all balls

  private final double BALL_DETECT_THRESHOLD = 50;

  private CANEncoder feederBeltEncoder;

  private CANSparkMax beltMotor;
  private CANPIDController beltPID;

  private TimeOfFlight entrySensor;
  private TimeOfFlight exitSensor;

  private FeedModeBase currentMode;
  private HashMap<FeedMode, FeedModeBase> modes = new HashMap<FeedMode, FeedModeBase>();  

  //network table entries for telemetry
  private NetworkTableEntry feederRPMEntry;
  private NetworkTableEntry exitRangeEntry;
  private NetworkTableEntry entryRangeEntry;


  public FeederSys(CANSparkMax beltMotor, TimeOfFlight entrySensor, TimeOfFlight exitSensor) {

    this. beltMotor = beltMotor;
    beltMotor.setIdleMode(IdleMode.kBrake);

    beltPID = beltMotor.getPIDController();
    beltPID.setP(KP);
    beltPID.setI(KI);
    beltPID.setD(KD);

    feederBeltEncoder = beltMotor.getEncoder();
    feederBeltEncoder.setVelocityConversionFactor(FEEDER_BELT_GEAR_RATIO_MULTIPLIER); //set feeder gear ratio

    //Sensors for Feeder
    this.entrySensor = entrySensor;
    this.exitSensor = exitSensor;

    // Setup our feed modes and initialize the system into the stopped mode.
    modes.put( FeedMode.STOPPED, new StoppedMode());
    modes.put(FeedMode.INTAKE, new IntakeMode());
    modes.put(FeedMode.SHOOT, new ShootMode());
    currentMode = modes.get(FeedMode.STOPPED);

    initTelemetry();
  }


  private void initTelemetry() {
    ShuffleboardTab shooterTab = Shuffleboard.getTab("Shooter"); //use the same tab as the shooter for displaying data

      feederRPMEntry = shooterTab.add("Feeder RPM", 0)
      .withPosition(5, 0)
      .withSize(1, 1)
      .getEntry();

    entryRangeEntry = shooterTab.add("Entry range", 0)
      .withPosition(5, 1)
      .withSize(1, 1)
      .getEntry();

    exitRangeEntry = shooterTab.add("Exit range", 0)
      .withPosition(5, 2)
      .withSize(1, 1)
      .getEntry();
  }


  @Override
  public void periodic() {

    currentMode.run(this);

    updateTelemetry();
  }


  private void updateTelemetry() {
    feederRPMEntry.setNumber(beltMotor.getEncoder().getVelocity());
    entryRangeEntry.setNumber(getEntryRange());
    exitRangeEntry.setNumber(getExitRange());
  }


  /**
   * Set the current mode of the feeder system.
   * @param mode New mode for feeder.
   */
  public void setFeedMode(FeedMode mode) {

    currentMode.end(this);
    currentMode = modes.get(mode);
    currentMode.init(this);
  }

  public double getEntryRange() {
    return entrySensor.getRange();
  }

  public double getExitRange() {
    return exitSensor.getRange();
  }


  private abstract class FeedModeBase {
    FeedMode id;

    private FeedModeBase( FeedMode id ) {
      this.id = id;
    }

    abstract void init( FeederSys feeder );
    abstract void run( FeederSys feeder );
    abstract void end( FeederSys feeder );
  }

  private class StoppedMode extends FeedModeBase
  {
    private StoppedMode(){
      super(FeedMode.STOPPED);
    }

    @Override void init( FeederSys feeder ) {
      feeder.beltPID.setReference(FEED_RPM_STOPPED, ControlType.kVelocity);
    }

    @Override void run( FeederSys feeder ) {}

    @Override void end( FeederSys feeder ) {}
  }

  private class IntakeMode extends FeedModeBase
  {
    private IntakeMode() {
      super(FeedMode.INTAKE);
    }

   @Override void init( FeederSys feeder ) {

   }

    @Override void run( FeederSys feeder ) {

      // If there is ball at the top of the feeder then stop the motor and return.
      if (feeder.exitSensor.getRange() <= BALL_DETECT_THRESHOLD) {
        feeder.beltPID.setReference(FEED_RPM_STOPPED, ControlType.kVelocity);
        return;
      }

      // If there is a ball in the intake end of the feeder then start the motor otherwise stop it.
      if (feeder.exitSensor.getRange() <= BALL_DETECT_THRESHOLD) {
        feeder.beltPID.setReference(FEED_RPM_INTAKE, ControlType.kVelocity);
      }
      else {
        feeder.beltPID.setReference(FEED_RPM_STOPPED, ControlType.kVelocity);
      }
    }

    @Override void end( FeederSys feeder ) {
      feeder.beltPID.setReference(FEED_RPM_STOPPED, ControlType.kVelocity);
    }
  }


  private class ShootMode extends FeedModeBase
  {
    private ShootMode(){
      super(FeedMode.SHOOT);
    }
      @Override void init( FeederSys feeder ) {
        feeder.beltPID.setReference(FEED_RPM_SHOOT, ControlType.kVelocity);
      }
  
      @Override void run( FeederSys feeder ) {
        if (feeder.exitSensor.getRange() > BALL_DETECT_THRESHOLD){
          feeder.beltPID.setReference(FEED_RPM_STOPPED, ControlType.kVelocity);
        }
      }
  
      @Override void end( FeederSys feeder ) {
        feeder.beltPID.setReference(FEED_RPM_STOPPED, ControlType.kVelocity);
      }
  }
}
