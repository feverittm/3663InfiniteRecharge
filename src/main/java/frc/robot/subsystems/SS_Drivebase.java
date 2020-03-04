package frc.robot.subsystems;

import javax.annotation.concurrent.GuardedBy;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.shuffleboard.*;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.I2C;
import frc.robot.drivers.*;
import frc.robot.drivers.NavX.Axis;
import frc.robot.Constants;

import org.frcteam2910.common.math.*;
import org.frcteam2910.common.util.HolonomicDriveSignal;
import org.frcteam2910.common.kinematics.ChassisVelocity;
import org.frcteam2910.common.kinematics.SwerveKinematics;
import org.frcteam2910.common.kinematics.SwerveOdometry;
import org.frcteam2910.common.robot.UpdateManager;

public class SS_Drivebase extends SubsystemBase implements UpdateManager.Updatable{

    //SWERVE MODULE ANGLE ENCODER OFFSETS (in radians, obviously)
    public static final double FRONT_LEFT_MODULE_OFFSET = Math.toRadians(40);
    public static final double FRONT_RIGHT_MODULE_OFFSET = Math.toRadians(-106);
    public static final double BACK_LEFT_MODULE_OFFSET = Math.toRadians(-30.4);
    public static final double BACK_RIGHT_MODULE_OFFSET = Math.toRadians(-32);

    private final Vector2 frontLeftModulePosition = new Vector2(-Constants.TRACKWIDTH / 2.0, Constants.WHEELBASE / 2.0);
    private final Vector2 frontRightModulePosition = new Vector2(-Constants.TRACKWIDTH / 2.0, -Constants.WHEELBASE / 2.0);
    private final Vector2 backLeftModulePosition = new Vector2(Constants.TRACKWIDTH / 2.0, Constants.WHEELBASE / 2.0);
    private final Vector2 backRightModulePosition = new Vector2(Constants.TRACKWIDTH / 2.0, -Constants.WHEELBASE / 2.0);

    private final CPRSwerveModule frontLeftModule = new CPRSwerveModule(frontLeftModulePosition, 
    FRONT_LEFT_MODULE_OFFSET, new CANSparkMax(Constants.FRONT_LEFT_ANGLE_MOTOR_CANID, MotorType.kBrushless),
    new CANSparkMax(Constants.FRONT_LEFT_DRIVE_MOTOR_CANID, MotorType.kBrushless));

    private final CPRSwerveModule frontRightModule = new CPRSwerveModule(frontRightModulePosition, 
    FRONT_RIGHT_MODULE_OFFSET, new CANSparkMax(Constants.FRONT_RIGHT_ANGLE_MOTOR_CANID, MotorType.kBrushless), 
    new CANSparkMax(Constants.FRONT_RIGHT_DRIVE_MOTOR_CANID, MotorType.kBrushless));

    private final CPRSwerveModule backLeftModule = new CPRSwerveModule(backLeftModulePosition,
    BACK_LEFT_MODULE_OFFSET, new CANSparkMax(Constants.BACK_LEFT_ANGLE_MOTOR_CANID, MotorType.kBrushless), 
    new CANSparkMax(Constants.BACK_LEFT_DRIVE_MOTOR_CANID, MotorType.kBrushless));

    private final CPRSwerveModule backRightModule = new CPRSwerveModule(backRightModulePosition, 
    BACK_RIGHT_MODULE_OFFSET, new CANSparkMax(Constants.BACK_RIGHT_ANGLE_MOTOR_CANID, MotorType.kBrushless),
    new CANSparkMax(Constants.BACK_RIGHT_DRIVE_MOTOR_CANID, MotorType.kBrushless));

    private final CPRSwerveModule[] modules = {frontLeftModule, frontRightModule, backLeftModule, backRightModule};

    private final SwerveKinematics kinematics = new SwerveKinematics(
                frontLeftModulePosition, // Front Left
                frontRightModulePosition, // Front Right
                backLeftModulePosition, // Back Left
                backRightModulePosition // Back Right
        );
    
    private final SwerveOdometry odometry = new SwerveOdometry(kinematics, RigidTransform2.ZERO);

    private final Object sensorLock = new Object();
    @GuardedBy("sensorLock")
    private final NavX navX = new NavX(Port.kMXP, Constants.NAVX_UPDATE_RATE);
    //private final NavX navX = new NavX(I2C.Port.kOnboard, Constants.NAVX_UPDATE_RATE);
    // private final NavX navX = new NavX(Port.kUSB, Constants.NAVX_UPDATE_RATE);

    private final Object kinematicsLock = new Object();
    @GuardedBy("kinematicsLock")
    private RigidTransform2 pose = RigidTransform2.ZERO;

    private final Object stateLock = new Object();
    @GuardedBy("stateLock")
    private HolonomicDriveSignal driveSignal = new HolonomicDriveSignal(Vector2.ZERO, 0.0, false);

    private NetworkTableEntry poseXEntry;
    private NetworkTableEntry poseYEntry;
    private NetworkTableEntry poseAngleEntry;

    private NetworkTableEntry fieldOrientedEntry;  
    private NetworkTableEntry gyroAngleEntry;
    private NetworkTableEntry correctionAngleEntry;

    private NetworkTableEntry driveSignalXEntry;
    private NetworkTableEntry driveSignalYEntry;
    private NetworkTableEntry driveSignalRotationEntry;

    private NetworkTableEntry[] moduleAngleEntries = new NetworkTableEntry[modules.length];
    private NetworkTableEntry[] moduleEncoderVoltageEntries = new NetworkTableEntry[modules.length];
    private NetworkTableEntry[] moduleVelocityEntries = new NetworkTableEntry[modules.length];

    public SS_Drivebase() {
        synchronized (sensorLock) {
        navX.setInverted(true);      
        }        

        ShuffleboardTab drivebaseTab = Shuffleboard.getTab("Drivebase");
        poseXEntry = drivebaseTab.add("Pose X", 0.0)
                .withPosition(0, 0)
                .withSize(1, 1)
                .getEntry();
        poseYEntry = drivebaseTab.add("Pose Y", 0.0)
                .withPosition(0, 1)
                .withSize(1, 1)
                .getEntry();
        poseAngleEntry = drivebaseTab.add("Pose Angle", 0.0)
                .withPosition(0, 2)
                .withSize(1, 1)
                .getEntry();

        ShuffleboardLayout driveSignalContainer = drivebaseTab.getLayout("Drive Signal", BuiltInLayouts.kGrid)
                .withPosition(0, 3)
                .withSize(3, 1);
        driveSignalYEntry = driveSignalContainer.add("Drive Signal Strafe", 0.0).getEntry();
        driveSignalXEntry = driveSignalContainer.add("Drive Signal Forward", 0.0).getEntry();
        driveSignalRotationEntry = driveSignalContainer.add("Drive Signal Rotation", 0.0).getEntry();


        ShuffleboardLayout frontLeftModuleContainer = drivebaseTab.getLayout("Front Left Module", BuiltInLayouts.kList)
                .withPosition(5, 0)
                .withSize(2, 2);
        moduleAngleEntries[0] = frontLeftModuleContainer.add("Angle", 0.0).getEntry();
        moduleEncoderVoltageEntries[0] = frontLeftModuleContainer.add("Encoder Voltage", 0.0).getEntry();
        moduleVelocityEntries[0] = frontLeftModuleContainer.add("Velocity", 0.0).getEntry();

        ShuffleboardLayout frontRightModuleContainer = drivebaseTab.getLayout("Front Right Module", BuiltInLayouts.kList)
                .withPosition(7, 0)
                .withSize(2, 2);
        moduleAngleEntries[1] = frontRightModuleContainer.add("Angle", 0.0).getEntry();
        moduleEncoderVoltageEntries[1] = frontRightModuleContainer.add("Encoder Voltage", 0.0).getEntry();
        moduleVelocityEntries[1] = frontRightModuleContainer.add("Velocity", 0.0).getEntry();

        ShuffleboardLayout backLeftModuleContainer = drivebaseTab.getLayout("Back Left Module", BuiltInLayouts.kList)
                .withPosition(5, 2)
                .withSize(2, 2);
        moduleAngleEntries[2] = backLeftModuleContainer.add("Angle", 0.0).getEntry();
        moduleEncoderVoltageEntries[2] = backLeftModuleContainer.add("Encoder Voltage", 0.0).getEntry();
        moduleVelocityEntries[2] = backLeftModuleContainer.add("Velocity", 0.0).getEntry();

        ShuffleboardLayout backRightModuleContainer = drivebaseTab.getLayout("Back Right Module", BuiltInLayouts.kList)
                .withPosition(7, 2)
                .withSize(2, 2);
        moduleAngleEntries[3] = backRightModuleContainer.add("Angle", 0.0).getEntry();
        moduleEncoderVoltageEntries[3] = backRightModuleContainer.add("Encoder Voltage", 0.0).getEntry();
        moduleVelocityEntries[3] = backRightModuleContainer.add("Velocity", 0.0).getEntry();

        ShuffleboardLayout fieldOrientedContainer = drivebaseTab.getLayout("Field Oriented", BuiltInLayouts.kList).withPosition(1, 0).withSize(1, 1);
        fieldOrientedEntry = fieldOrientedContainer.add("Field Oriented", 0.0).getEntry();

        ShuffleboardLayout gyroContainer = drivebaseTab.getLayout("Gyro", BuiltInLayouts.kList).withPosition(1, 1).withSize(1, 1);
        gyroAngleEntry = gyroContainer.add("Gyro Angle", 0.0).getEntry();

        ShuffleboardLayout correctionContainer = drivebaseTab.getLayout("Correction", BuiltInLayouts.kList).withPosition(1, 2).withSize(1, 1);
        correctionAngleEntry = correctionContainer.add("Correction", 0.0).getEntry();
    }

    public RigidTransform2 getPose() {
        synchronized (kinematicsLock) {
            return pose;
        }
    }

    public void drive(Vector2 translationalVelocity, double rotationalVelocity, boolean fieldOriented) {
        synchronized (stateLock) {
            driveSignal = new HolonomicDriveSignal(translationalVelocity, rotationalVelocity, fieldOriented);
        }
    }

    public void drive(HolonomicDriveSignal driveSignal) {
        synchronized(stateLock) {
            this.driveSignal = driveSignal;
        }
    }

    public void resetGyroAngle(Rotation2 angle) {
        synchronized (sensorLock) {
            navX.setAdjustmentAngle(
                    navX.getUnadjustedAngle().rotateBy(angle.inverse())
            );
        }
    }

    public void resetPose() {
        synchronized(kinematicsLock) {
            pose = new RigidTransform2(Vector2.ZERO, Rotation2.ZERO);
        }
    }

    public void resetPoseTranslation() {
        synchronized(kinematicsLock) {
            RigidTransform2 previousPose = pose;
            odometry.resetPose(new RigidTransform2(Vector2.ZERO, previousPose.rotation));
        }
    }

    public CPRSwerveModule[] getModules() {
        return modules;
    }

    @Override
    public void update(double timestamp, double dt) {
        updateOdometry(dt);

        HolonomicDriveSignal driveSignal;
        synchronized (stateLock) {
            driveSignal = this.driveSignal;
        }

        updateModules(driveSignal, dt);
    }

    private void updateOdometry(double dt) {
        Vector2[] moduleVelocities = new Vector2[modules.length];
        for (int i = 0; i < modules.length; i++) {
            var module = modules[i];
            module.updateSensors();

            moduleVelocities[i] = Vector2.fromAngle(Rotation2.fromRadians(module.readAngle())).scale(module.getCurrentVelocity());
        }

        Rotation2 angle;
        synchronized (sensorLock) {
            angle = navX.getAngle();
            // angle = Rotation2.fromDegrees(navX.getAxis(Axis.ROLL));
        }

        RigidTransform2 pose = odometry.update(angle, dt, moduleVelocities);

        synchronized (kinematicsLock) {
            this.pose = pose;
        }
    }

    private void updateModules(HolonomicDriveSignal signal, double dt) {
        RigidTransform2 pose = getPose();
        ChassisVelocity velocity;
      
        if (signal == null) {
            velocity = new ChassisVelocity(Vector2.ZERO, 0.0);
        } else if (signal.isFieldOriented()) {

            Rotation2 correction = pose.rotation;
            velocity = new ChassisVelocity(
                    signal.getTranslation().rotateBy(correction),
                    signal.getRotation()
            );

            correctionAngleEntry.setDouble(correction.toRadians());

        } else {
            velocity = new ChassisVelocity(signal.getTranslation(), signal.getRotation());
        }

        Vector2[] moduleOutputs = kinematics.toModuleVelocities(velocity);
        SwerveKinematics.normalizeModuleVelocities(moduleOutputs, 1.0);

        for (int i = 0; i < modules.length; i++) {
          var module = modules[i];
          module.setTargetVelocity(moduleOutputs[i]);
          module.updateState(dt);
        }
        poseXEntry.setDouble(pose.translation.x);
        poseYEntry.setDouble(pose.translation.y);
        poseAngleEntry.setDouble(pose.rotation.toDegrees());

        fieldOrientedEntry.setDouble( signal == null ? 0 : (signal.isFieldOriented() ? 1 : 0));        
        gyroAngleEntry.setDouble(navX.getAngle().toDegrees());
        // gyroAngleEntry.setDouble(navX.getAxis(Axis.ROLL));
    }

    @Override
    public void periodic() {
        for (int i = 0; i < modules.length; i++) {
            var module = modules[i];
            moduleAngleEntries[i].setDouble(Math.toDegrees(module.readAngle()));
            moduleEncoderVoltageEntries[i].setDouble(module.getEncoderVoltage());
            moduleVelocityEntries[i].setDouble(module.getCurrentVelocity());
        }
        synchronized(stateLock) {
            driveSignalYEntry.setDouble(driveSignal.getTranslation().y);
            driveSignalXEntry.setDouble(driveSignal.getTranslation().x);
            driveSignalRotationEntry.setDouble(driveSignal.getRotation());
        }
    }
}