package frc.robot;

/**
 * All constants for robot control are found here.
 */
public class Constants {

    //Robot dimensions
    public static final double TRACKWIDTH = 18;
    public static final double WHEELBASE = 20;

    // Controllers
    public static final int DRIVE_CONTROLLER_ID = 0;
    public static final int OPERATOR_CONTROLLER_ID = 1;
    public static final int TEST_CONTROLLER_ID = 2;

    // Cameras
    public static final int FEEDER_CAMERA_PORT = 0;
    public static final int INTAKE_CAMERA_PORT = 1;
    
    // Drivebase subsystem CAN IDs
    public static final int FRONT_LEFT_DRIVE_MOTOR_CANID = 1;
    public static final int FRONT_RIGHT_DRIVE_MOTOR_CANID = 2;
    public static final int BACK_LEFT_DRIVE_MOTOR_CANID = 3;
    public static final int BACK_RIGHT_DRIVE_MOTOR_CANID = 4;

    public static final int FRONT_LEFT_ANGLE_MOTOR_CANID = 5;
    public static final int FRONT_RIGHT_ANGLE_MOTOR_CANID = 6;
    public static final int BACK_LEFT_ANGLE_MOTOR_CANID = 7;
    public static final int BACK_RIGHT_ANGLE_MOTOR_CANID = 8;

    // Other drivebase related constants
    public static final int DRIVE_MOTOR_CURRENT_LIMIT = 25;
    public static final byte NAVX_UPDATE_RATE = 50;

    //Intake subsystem related constants
    public static final int INTAKE_MOTOR_CANID = 9;
    public static final int INTAKE_LONG_SOLENOID_FORWARD_ID = 0;
    public static final int INTAKE_LONG_SOLENOID_REVERSE_ID = 2;
    public static final int INTAKE_SHORT_SOLENOID_FORWARD_ID = 1;
    public static final int INTAKE_SHORT_SOLENOID_REVERSE_ID = 3;
    public static final int INTAKE_SENSOR = 3;

    // Ball feeder subsystem related constants
    public static final int FEED_MOTOR_CANID = 10;
    public static final int ENTRY_SENSOR_DIO_ID = 1;
    public static final int EXIT_SENSOR_DIO_ID = 2;
    
    //Shooter subsystem related constants
    public static final int SHOOTER_MOTOR_CANID = 11;
    public static final int HOOD_SOLENOID_FORWARD_ID = 5;
    public static final int HOOD_SOLENOID_REVERSE_ID = 4; 

    //Climber subsystem related constants
    public static final int CLIMBER_EXTEND_MOTOR_CANID = 12;
    public static final int CLIMBER_WINCH_MOTOR_CANID = 13;
    public static final int CLIMBER_DRIVE_MOTOR_CANID = 14;
    public static final int CLIMBER_RETRACT_MOTOR_CANID = 15;
}

