package frc.robot.subsystems;

import java.util.HashMap;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.drivers.TimeOfFlightSensor;

public class SS_Feeder extends SubsystemBase {
  public enum FeedMode {
    STOPPED,
    INTAKE,
    PRESHOOT,
    SHOOT,
    INTAKE_PREP
  }
  private final double FEEDER_BELT_GEAR_RATIO_MULTIPLIER = 1;

  // Feeder PID constants
  private final double KP = 0.0001;//0.007
  private final double KI = 0.0000011;//0.00000001
  private final double KD = 0;//00000001

  private final int FEED_RPM_STOPPED = 0;
  private final int FEED_RPM_SHOOT = 3500; //how fast the feeder should be running when we are shooting
  private final int FEED_RPM_PREP_SHOOT = 4500;
  private final int FEED_RPM_INTAKE = 4500; //how fast the feeder should be running when indexing the balls
  private final int FEED_RPM_PREP_INTAKE = -3500;

  // The number of revolutions of the belt motor required to cycle a ball all the way from the feeders entry to the exit.
  public final int REV_PER_FULL_FEED = 1500;

  // The threshold distance that indicates the presence of a ball at one of the sensors in millimeters.
  private final double ENTRY_BALL_DETECT_THRESHOLD = 12;
  private final double EXIT_BALL_DETECT_THRESHOLD = 17;

  // Subsystems internal data
  private CANSparkMax beltMotor;
  private CANEncoder beltEncoder;
  private CANPIDController beltPID;

  private TimeOfFlightSensor entrySensor;
  private TimeOfFlightSensor exitSensor;

  private FeedModeBase currentMode;
  private HashMap<FeedMode, FeedModeBase> modes = new HashMap<FeedMode, FeedModeBase>();  

  //network table entries for telemetry
  private NetworkTableEntry feederRPMEntry;
  private NetworkTableEntry exitRangeEntry;
  private NetworkTableEntry entryRangeEntry;
  private NetworkTableEntry exitValid;

  private boolean valid = false;
  public SS_Feeder(CANSparkMax beltMotor, TimeOfFlightSensor entrySensor, TimeOfFlightSensor exitSensor) {

    this.beltMotor = beltMotor;
    beltMotor.setIdleMode(IdleMode.kBrake);
    beltMotor.setInverted(true);

    beltPID = beltMotor.getPIDController();
    beltPID.setP(KP);
    beltPID.setI(KI);
    beltPID.setD(KD);

    beltEncoder = beltMotor.getEncoder();
    beltEncoder.setVelocityConversionFactor(FEEDER_BELT_GEAR_RATIO_MULTIPLIER); //set feeder gear ratio

    //Sensors for Feeder
    this.entrySensor = entrySensor;
    this.exitSensor = exitSensor;
    //Setting Range of Intrest for Sensors
    entrySensor.setRangeOfInterest(0, 0, 0, 15);
    exitSensor.setRangeOfInterest(0, 0, 0, 15);

    // Setup our feed modes and initialize the system into the stopped mode.
    modes.put( FeedMode.STOPPED, new StoppedMode());
    modes.put(FeedMode.INTAKE_PREP, new IntakePrep());
    modes.put(FeedMode.INTAKE, new IntakeMode());
    modes.put(FeedMode.PRESHOOT, new PreshootMode());
    modes.put(FeedMode.SHOOT, new ShootMode());
    //modes.put(FeedMode.PREINTAKE, new PreIntakeMode());
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
    exitValid = shooterTab.add("Exit Valid", false)
      .withPosition(5, 3)
      .withSize(1, 1)
      .getEntry();
  }


   /**
   * Perform background processing for feeder system.
   */
   @Override
   public void periodic() {

    //Execute the current mode, if it completes then put system in Stopped mode.
    if (currentMode.run(this)) {
      setFeedMode(FeedMode.STOPPED);
    }

    updateTelemetry();
  }


  private void updateTelemetry() {
    feederRPMEntry.setNumber(beltMotor.getEncoder().getVelocity());
    entryRangeEntry.setNumber(entrySensor.getDistance());
    exitRangeEntry.setNumber(exitSensor.getDistance());
    exitValid.setBoolean(valid);
  }

  /**
   * Tell caller if the feeder subsystem is currently idle (i.e. in stopped mode)
   */
  public boolean isIdle()
  {
    return currentMode.id == FeedMode.STOPPED;
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


   /**
   * Tell call if there is a ball at the entry end of the feeder subsystem.
   * @return True if a ball is present at the entry sensor, false otherwise.
   */
  public boolean ballInEntry() {
    return entrySensor.getDistance() <= ENTRY_BALL_DETECT_THRESHOLD;
  }


   /**
   * Tell call if there is a ball at the exit end of the feeder subsystem.
   * @return True if a ball is present at the exit sensor, false otherwise.
   */
  public boolean ballInExit() {
    return exitSensor.getDistance() <= EXIT_BALL_DETECT_THRESHOLD;
  }


  /**
   * Base class for all of our feed modes
   */
  private abstract class FeedModeBase {
    FeedMode id;

    private FeedModeBase( FeedMode id ) {
      this.id = id;
    }
    protected void init( SS_Feeder feeder ) {}
    protected boolean run( SS_Feeder feeder ) { return false; }
    protected void end( SS_Feeder feeder ) {}
  }


  /**
   * Implements our Stopped mode, stops motor in init and does nothing after that.
   */
  private class StoppedMode extends FeedModeBase
  {
    private StoppedMode(){
      super(FeedMode.STOPPED);
    }

    @Override
    protected void init( SS_Feeder feeder ) {
      feeder.beltPID.setReference(FEED_RPM_STOPPED, ControlType.kVelocity);
    }
  }

  private class IntakePrep extends FeedModeBase{
    private IntakePrep(){
      super(FeedMode.INTAKE_PREP);
    }

    @Override
    protected void init(SS_Feeder feeder){
      feeder.beltPID.setReference(FEED_RPM_PREP_INTAKE, ControlType.kVelocity);
    }

    @Override
    protected boolean run(SS_Feeder feeder){
      return false;
    }
  }
  /**
   * Implements Intake mode, advances belts as long as there is a ball in the entry and ends once a ball
   * reaches the exit end of the feeder.
   */
  private class IntakeMode extends FeedModeBase
  {
    private IntakeMode() {
      super(FeedMode.INTAKE);
    }

    @Override
    protected boolean run( SS_Feeder feeder ) {

      // If there is ball at the top of the feeder then stop the motor and exit complete this mode.
      if (feeder.ballInExit()) {
        feeder.beltPID.setReference(FEED_RPM_STOPPED, ControlType.kVelocity);
        //feeder.setMotorSpeed(0);;
        return true;
      }

      // If there is a ball in the intake end of the feeder then start the motor otherwise stop it.
      if (feeder.ballInEntry()) {
        feeder.beltPID.setReference(FEED_RPM_INTAKE, ControlType.kVelocity);
        //feeder.setMotorSpeed(1);
      }
      else {
        feeder.beltPID.setReference(FEED_RPM_STOPPED, ControlType.kVelocity);
        //feeder.setMotorSpeed(0);
      }

      return false;
    }

    @Override
    protected void end( SS_Feeder feeder ) {
      feeder.beltPID.setReference(FEED_RPM_STOPPED, ControlType.kVelocity);
    }
  }


 /**
   * Implements pre-Shoot mode, advances feed belt until a ball is present at the exit 
   * end of the feeder.
   */
  private class PreshootMode extends FeedModeBase
  {
    private PreshootMode(){
      super(FeedMode.PRESHOOT);
    }

    @Override
    protected void init( SS_Feeder feeder ) {
      feeder.beltEncoder.setPosition(0.0);
      feeder.beltPID.setReference(FEED_RPM_PREP_SHOOT, ControlType.kVelocity);
    }

    @Override
    protected boolean run( SS_Feeder feeder ) {
      // If we see a ball at the exit sensor then we have moved them to the top of the feeder and ready to shoot.
      if (feeder.ballInExit()) {
        valid = true;
        return true;
      }

      // If the exit sensor has not seen a ball yet but the belt has moved the full length of the feeder then there are no balls, bail out.
      if (feeder.beltEncoder.getPosition() > REV_PER_FULL_FEED)
      {
          return true;
      }

      return false;
    }
    @Override
    protected void end( SS_Feeder feeder ) {
      feeder.beltPID.setReference(FEED_RPM_STOPPED, ControlType.kVelocity);
    }
  }


  /**
   * Implements Shoot mode, advances one ball out of the feeder into the shooter,
   * advances next ball (if there is one) to the top of the feeder then terminates.
   */
  private class ShootMode extends FeedModeBase
  {
    private boolean gapSeen;

    private ShootMode(){
      super(FeedMode.SHOOT);
    }

    @Override
    protected void init( SS_Feeder feeder ) {

      gapSeen = false;
      feeder.beltPID.setReference(FEED_RPM_SHOOT, ControlType.kVelocity);
    }

    @Override
    protected boolean run( SS_Feeder feeder ) {
      boolean result = false;

      if (gapSeen) {
        if ( feeder.ballInExit()) {
          result = true;
        }
      } else {
        if (!feeder.ballInExit())
        {
          gapSeen = true;
        }
      }

      return result;
    }

    @Override
    protected void end( SS_Feeder feeder ) {
      feeder.beltPID.setReference(FEED_RPM_STOPPED, ControlType.kVelocity);
    }
  }
}
