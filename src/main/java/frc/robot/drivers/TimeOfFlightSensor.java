/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.drivers;

import com.playingwithfusion.TimeOfFlight;
import com.playingwithfusion.TimeOfFlight.RangingMode;

/**
 * Basic driver for PlayingWithFusion's Time-of-Flight sensor
 */
public class TimeOfFlightSensor {

    private TimeOfFlight sensor;
    private static double DEFAULT_SAMPLE_RATE = 24;
    
    public TimeOfFlightSensor(int canId) {
        this.sensor = new TimeOfFlight(canId);
        sensor.setRangingMode(RangingMode.Short, DEFAULT_SAMPLE_RATE);
    }

    public void setRangeOfInterest(int topLeftX, int topLeftY, int bottomRightX, int bottomRightY){
        sensor.setRangeOfInterest(topLeftX, topLeftY, bottomRightX, bottomRightY);
    }
    /**
     * 
     * @return Target's distance from the sensor in millimeters
     */
    public double getDistanceMillis() {
        return sensor.getRange();
    }

    /** 
     * 
     * @return Target's distance from the sensor in centimeters
     */
    public double getDistance() {
        return sensor.getRange() * 0.1;
    }

    /**
     * 
     * @return Sensor status of last distance measurement
     */
    public TimeOfFlight.Status getStatus() {
        return sensor.getStatus();
    }

    /**
     * 
     * @return the low-level sensor object
     */
    public TimeOfFlight getSensor() {
        return sensor;
    }
}
