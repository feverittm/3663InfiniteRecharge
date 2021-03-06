package frc.robot.subsystems;

import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANEncoder;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.drivers.Vision;


public class SS_Shooter extends SubsystemBase {

  private static SS_Shooter instance;

  public static SS_Shooter getInstance() {
    if(instance == null) {
      instance = new SS_Shooter();
    }
    return instance;
  }

  //Known RPMs for different distances. Column one: feet, Column two: RPM. Updated: 2/29/2020
  private final int[][] KNOWN_RPM = new int[][] {
    {5, 3490},
    {8, 3350},
    {10, 3400},
    {15, 3730},
    {20, 4090},
    {25, 4390}
  };
  private final int DISTANCE_COLUMN = 0; //column index for distance values
  private final int RPM_COLUMN = 1; //column index for RPM values
  
  private final int LOB_SHOT_SPEED = 5080;

  //the correction multiplier in the code that is fixed (the other one, correctionMultiplier, can be changed during a match)
  private final double WHEEL_GEAR_RATIO_MULTIPLIER = 1;

  //Wheel PID constants (These values are tuned correctly for the software robot)
  private final double KF = 0.00018; //0.0002// final
  private final double KP = 0.001;//0.0004; //0.0012;
  private final double KI = 0.0;//0.00000035; //1e-019;
  private final double KD = 0.0001;

  private final double CONFIDENCE_THRESHOLD = 97; //the threshold or the percent wanted to shoot at
  private final double CORRECT_RPM_PERCENTAGE = .01;
  private final double CONFIDENCE_TIME = 1; //time we want to be in the confidence band before shooting

  private Vision vision = RobotContainer.getVision();

  private CANSparkMax flywheelMotor = new CANSparkMax(Constants.SHOOTER_MOTOR_CANID, MotorType.kBrushless);
  private CANEncoder flywheelEncoder;
  private CANPIDController flywheelPid;
  private DoubleSolenoid hood = new DoubleSolenoid(Constants.HOOD_SOLENOID_FORWARD_ID, Constants.HOOD_SOLENOID_REVERSE_ID);

  private Timer confidenceTimer;

  private int targetRPM = 0; //the target RPM when not updating from vision
  private boolean wheelSpinning = false;
  private boolean updateFromVision = false;

  private double correctionMultiplier = 1;

  //Network tables for telemetry
  private NetworkTableEntry targetRPMEntry;
  private NetworkTableEntry currentRPMEntry;
  private NetworkTableEntry wheelSpinningEntry;
  private NetworkTableEntry shootingConfidenceEntry;

  public SS_Shooter() {
    flywheelMotor.setInverted(true);
    flywheelEncoder = flywheelMotor.getEncoder();
    flywheelEncoder.setVelocityConversionFactor(WHEEL_GEAR_RATIO_MULTIPLIER);
    flywheelPid = flywheelMotor.getPIDController();
    flywheelPid.setOutputRange(0, 1);

    //set Wheel PID constants
    flywheelPid.setFF(KF);
    flywheelPid.setP(KP);
    flywheelPid.setI(KI);
    flywheelPid.setD(KD);

    initTelemetry();

    confidenceTimer = new Timer();
    confidenceTimer.start();
  }

  private void initTelemetry() {
    ShuffleboardTab shooterTab = Shuffleboard.getTab("Shooter");
    targetRPMEntry = shooterTab.add("Target RPM", 0)
      .withPosition(0, 0)
      .withSize(1, 1)
      .getEntry();
    currentRPMEntry = shooterTab.add("Current RPM", 0)
      .withPosition(0, 1)
      .withSize(1, 1)
      .getEntry();
    shootingConfidenceEntry = shooterTab.add("RPM Confidence", 0)
      .withPosition(1, 0)
      .withSize(1, 1)
      .getEntry();
    wheelSpinningEntry = shooterTab.add("Spinning", false)
      .withWidget("Boolean Box")
      .withPosition(1, 1)
      .withSize(1, 1)
      .getEntry();
  }

  @Override
  public void periodic() {
    updateShooter();
    updateTelemetry();
  }

  //update the targetRPM
  private void updateShooter() {
    if(wheelSpinning) {
      if(updateFromVision) {
        vision.setLEDMode(Vision.LED_ON);
        double distance = vision.getDistance();
        if(distance > 0) {
          targetRPM = calculateRPM(distance);
          setRPM(targetRPM);
        }
      } else {
        vision.setLEDMode(Vision.LED_OFF);
        setRPM(targetRPM);
      }
    } else {
      setRPM(0);
    }
  }

  //push telemetry to Shuffleboard
  private void updateTelemetry() {
    targetRPMEntry.setNumber(targetRPM);
    currentRPMEntry.setNumber(flywheelEncoder.getVelocity());
    shootingConfidenceEntry.setNumber(getShotConfidence());
    wheelSpinningEntry.setBoolean(wheelSpinning);

    vision.updateTelemetry();
  }

  /**
   * @param spinning true if wheel should spin
   */
  public SS_Shooter setSpinning(boolean spinning) {
    wheelSpinning = spinning;
    return this;
  }

  /**
   * Set the target distance for the wheel to spin to.
   */
  public SS_Shooter setTargetDistance(double targetDistance) {
    targetRPM = calculateRPM(targetDistance);
    updateFromVision = false;
    return this;
  }

  /**
   * Sets the speed of the shooter to the correct speed for a lob shot
   */
  public SS_Shooter setLobSpeed() {
    targetRPM = LOB_SHOT_SPEED;
    updateFromVision = false;
    return this;
  }

  /**
   * have the shooter automatically update the target distance from vision, otherwise it will use the target distance
   */
  public SS_Shooter updateFromVision(boolean useVision) {
    updateFromVision = useVision;
    return this;
  }

  /**
   * set the position of the hood
   * @param extended if true, set hood to position for far shooting, otherwise set it to the near position
   */
  public SS_Shooter extendHood(boolean extended) {
    if(extended) {
      hood.set(Value.kForward);
    } else {
      hood.set(Value.kReverse);
    }

    return this;
  }

  /**
   * Sets the multiplier for correcting shooting distance
   * @param correctionMultiplier the new correction multiplier
   */
  public void setCorrectionMultiplier(double correctionMultiplier) {
    this.correctionMultiplier = correctionMultiplier;
  }

  /**
   * Work in Progress, don't use yet
   * @return the percentage of confidence for the shot based on wheel velocity (from 0-100)
   */
  public int getShotConfidence() {
    //if the wheel is not spinning, we have no confidence in making a shot
    if(!wheelSpinning) {
      return 0;
    }

    //get percentage of current speed to target speed
    double confidence = (flywheelEncoder.getVelocity() / targetRPM) * 100;
    //fix percentage values over 100
    if(confidence > 100) {
      confidence = 100 - (confidence - 100);
    }

    //reset the confidence timer if we are not within the band of error percent we want
    if(confidence < CONFIDENCE_THRESHOLD) {
      confidenceTimer.reset();
    }
    //calculate the percent of the amount of time we want to be in the confidence range to how long we actually are in it
    double confidenceTimePercent = (confidenceTimer.get() / CONFIDENCE_TIME) * 100;
    //prevents from returning confidences over 100
    return (int)Math.min(100, confidenceTimePercent);
  }

  public boolean atCorrectRPM(){
    return Math.abs(flywheelEncoder.getVelocity() - targetRPM) <= CORRECT_RPM_PERCENTAGE * targetRPM;
  }
  /**
   * Returns the non-fixed correction multiplier
   * @return the correction multiplier
   */
  public double getCorrection() {
    return correctionMultiplier;
  }

  public boolean isSpinning() {
    return wheelSpinning;
  }

  /**
   * Set target RPM for the wheel
   * @param RPM target RPM for the wheel
   */
  private void setRPM(int RPM) {
    flywheelPid.setReference(RPM * correctionMultiplier, ControlType.kVelocity);
  }

  /**
   * FOR TEST COMMANDS ONLY! Directly sets shooter target RPM
   * Specifically meant for the C_ShootRPMTesting command which is used for finding the correct RPM for various distances
   */
  public void testSetTargetRPM(int RPM) {
    targetRPM = RPM;
  }

  //updated 2-29-2020
  private int calculateRPMPolynomial(double feet) {
    //fourth order
    // double RPM = (0.0208 * Math.pow(feet, 4)) - (1.5672 * Math.pow(feet, 3)) + (42.513 * Math.pow(feet, 2)) - (420.63 * feet) + 4713.0;
    //second order
    double RPM = (2.324 * Math.pow(feet, 2)) - (17.533 * feet) + 3426.8;
    return (int)RPM;
  }

  /**
   * Convert distance to correct RPM for shooting power cells
   * @param distance distance to convert to RPMs (in feet)
   * @return RPMs converted from distance
   */
  private int calculateRPM(double distance) {
    distance = Math.round(distance); //round the distance so that 5.1 feet does not use the value for 6 feet
    int index = 0;
    for(int i = 0; i < KNOWN_RPM.length; i++) {
      double currentDistance = KNOWN_RPM[i][DISTANCE_COLUMN];
      //If the rounded distance equals a known distance, use that known distance
      if(currentDistance == distance) {
        return KNOWN_RPM[i][RPM_COLUMN];
      } else if(currentDistance > distance) {
        //if the index is at the lowest distance, return the lowest RPM
        if(i == 0) {
          return KNOWN_RPM[0][RPM_COLUMN];
        }
        index = i;
        return linearInterpolation(index, distance);
      }
    }
    return KNOWN_RPM[KNOWN_RPM.length - 1][RPM_COLUMN];
  }

  /**
   * Interpolate the RPM of that the shooter should spin based on the distance and the index of the closest greater RPM in the table
   * @param index  the index of the closest greater RPM in the table
   * @param distance the distance away from the target
   * @return the needed RPM for the distance
   */
  private int linearInterpolation(int index, double distance) {
    //find the range between the two known distances
    double distanceRange = KNOWN_RPM[index][DISTANCE_COLUMN] - KNOWN_RPM[index - 1][DISTANCE_COLUMN];
    double RPMRange = (KNOWN_RPM[index][RPM_COLUMN] - KNOWN_RPM[index - 1][RPM_COLUMN]);
    //calculate the new distance between the known distances linearly
    return (int)((distance - KNOWN_RPM[index - 1][DISTANCE_COLUMN]) / distanceRange * RPMRange) + KNOWN_RPM[index - 1][RPM_COLUMN];
  }

}
