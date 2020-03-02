package frc.robot.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SS_Feeder extends SubsystemBase {
  public enum FeedRate{
    STOPPED,
    INTAKE_PREP,
    INTAKE,
    SHOOT_PREP,
    SHOOT_ONE,
    SHOOT_ALL
  }
  private FeedRate currentFeedRate = FeedRate.STOPPED;
  // Feeder PID constants
  private final double KP = 0.0001;//0.007
  private final double KI = 0.0000011;//0.00000001
  private final double KD = 0;//00000001
  
  private final double FEEDER_BELT_GEAR_RATIO_MULTIPLIER = 1;

  private final int FEED_RPM_STOPPED = 0;
  private final int FEED_RPM_INTAKE_PREP = -2250;
  private final int FEED_RPM_INTAKE = 2250; //how fast the feeder should be running when indexing the balls
  private final int FEED_RPM_PREP_SHOOT = 5500;
  private final int FEED_RPM_SHOOT_ONE = 3500; //how fast the feeder should be running when we are shooting
  private final int FEED_RPM_SHOOT_ALL = 6000;

  // The number of revolutions of the belt motor required to cycle a ball all the way from the feeders entry to the exit.
  public static final int REV_PER_FULL_FEED = 67;

  //Motor encoder and pid
  private CANSparkMax beltMotor;
  private CANEncoder beltEncoder;
  private CANPIDController beltPID;

  //DIO Sensors
  private DigitalInput entrySensor;
  private DigitalInput exitSensor;

  //network table entries for telemetry
  private NetworkTableEntry feederRPMEntry;
  private NetworkTableEntry exitValid;
  private NetworkTableEntry entryValid;
  private NetworkTableEntry feederEncoderPos;
  public SS_Feeder(CANSparkMax beltMotor, DigitalInput entrySensor, DigitalInput exitSensor){
    this.beltMotor = beltMotor;
    beltMotor.setIdleMode(IdleMode.kBrake);
    beltMotor.setInverted(true);

    //belt encoder
    beltEncoder = beltMotor.getEncoder();
    beltEncoder.setVelocityConversionFactor(FEEDER_BELT_GEAR_RATIO_MULTIPLIER); //set feeder gear ratio

    //belt pid
    beltPID = beltMotor.getPIDController();
    beltPID.setP(KP);
    beltPID.setI(KI);
    beltPID.setD(KD);

    //Sensors for feeder
    this.entrySensor = entrySensor;
    this.exitSensor = exitSensor;
    initTelemetry();
  }

  @Override
  public void periodic(){
    updateTelemetry();
  }

  private void initTelemetry() {
    ShuffleboardTab shooterTab = Shuffleboard.getTab("Shooter"); //use the same tab as the shooter for displaying data

    feederRPMEntry = shooterTab.add("Feeder RPM", 0)
      .withPosition(5, 0)
      .withSize(1, 1)
      .getEntry();
    entryValid = shooterTab.add("Entry Valid", false)
      .withPosition(5,2)
      .withSize(1, 1)
      .getEntry();
    exitValid = shooterTab.add("Exit Valid", false)
      .withPosition(5, 3)
      .withSize(1, 1)
      .getEntry();
    feederEncoderPos = shooterTab.add("FeederEncode", 0)
      .withPosition(5, 4)
      .withSize(1, 1)
      .getEntry();
  }

  private void updateTelemetry() {
    feederRPMEntry.setNumber(beltMotor.getEncoder().getVelocity());
    feederEncoderPos.setNumber(beltEncoder.getPosition());
    exitValid.setBoolean(ballInExit());
    entryValid.setBoolean(ballInEntry());
  }

  public void setRPM(double RPM){
    beltPID.setReference(RPM, ControlType.kVelocity);
  }

  public void setRPM(FeedRate feedRate){
    currentFeedRate = feedRate;
    double targetRPM = 0;
    switch(feedRate){
      case STOPPED:
        targetRPM = FEED_RPM_STOPPED;
        break;
      case INTAKE_PREP:
        targetRPM = FEED_RPM_INTAKE_PREP;
        break;
      case INTAKE:
        targetRPM = FEED_RPM_INTAKE;
        break;
      case SHOOT_PREP:
        targetRPM = FEED_RPM_PREP_SHOOT;
        break;
      case SHOOT_ONE:
        targetRPM = FEED_RPM_SHOOT_ONE;
        break;
      case SHOOT_ALL:
        targetRPM = FEED_RPM_SHOOT_ALL;
        break;
    }
    beltPID.setReference(targetRPM, ControlType.kVelocity);
  }

  public void resetEncoder(){
    beltEncoder.setPosition(0.0);
  }

  public FeedRate getFeedRate(){
    return currentFeedRate;
  }

  public double getPosition(){
    return beltEncoder.getPosition();
  }

  public double getRPM(){
    return beltEncoder.getVelocity();
  }

  public double getRevPerFullFeed(){
    return REV_PER_FULL_FEED;
  }
  /**
   * Tell call if there is a ball at the entry end of the feeder subsystem.
   * @return True if a ball is present at the entry sensor, false otherwise.
   */
  public boolean ballInEntry() {
    return !entrySensor.get();
  }


   /**
   * Tell call if there is a ball at the exit end of the feeder subsystem.
   * @return True if a ball is present at the exit sensor, false otherwise.
   */
  public boolean ballInExit() {
    return !exitSensor.get();
  }

  public void setSpeed(double speed){
    beltMotor.set(speed);
  }
}
