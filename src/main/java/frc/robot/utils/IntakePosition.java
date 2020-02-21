package frc.robot.utils;

/**
 * Intake arm extend positions
 * @param FULLY_RETRACTED checks to see if both the long and short solenoids are retracted
 * @param LONG_RETRACT checks to see if ONLY the long solenoid is retracted
 * @param SHORT_RETRACT checks to see if ONLY the short solenoid is retracted
 * @param FULLY_EXTENDED checks to see if both the long and short solenoids are extended
 */

public enum IntakePosition{
    FULLY_RETRACTED, //POSITION 0 (for putting the intake arm within the frame perimeter)
    LONG_RETRACT, //POSITION 1 (for inserting power cells into the magazine)
    SHORT_RETRACT, //POSITION 2 (for storing power cells in the intake arm)
    FULLY_EXTENDED //POSITION 3 (for intaking power cells)
}
