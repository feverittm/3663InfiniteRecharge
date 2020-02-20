/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

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
    public static final byte NAVX_UPDATE_RATE = 127;

    //Intake subsystem related constants
    public static final int INTAKE_MOTOR_CANID = 9;

    // Ball feeder subsystem related constants
    public static final int FEED_MOTOR_CANID = 10;
    public static final int ENTRY_SENSOR_CANID = 20;
    public static final int EXIT_SENSOR_CANID = 21;

    //Shooter subsystem related constants
    public static final int SHOOTER_MOTOR_CANID = 11;

    //Climber subsystem related constants
    public static final int CLIMBER_EXTEND_MOTOR_CANID = 12;
    public static final int CLIMBER_WINCH_MOTOR_CANID = 13;
    public static final int CLIMBER_DRIVE_MOTOR_CANID = 14;
}

