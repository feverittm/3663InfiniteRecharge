package frc.robot.utils;

public class ProjectileArkSimulation {

    public final double g = 8.9;
    public final double cd = 0.8;

    private final int DISTANCE_COLUMN = 0;
    private final int RPM_COLUMN = 1;
    private final int ANGLE_OF_HOOD_COLUMN = 1;

    private int[][] table;

    public ProjectileArkSimulation(int[][] table){
        this.table = table;   
    }


    public double thadaWithOutDrag(double distance, double volicety, double error){
        return error*(0.5*Math.atan((distance*g)/Math.pow(volicety,2)));
    }


    /**
     * calculats what the hood angle shood be
     * @param distance the distance between the robot and the target
     * @param table the table of Angles and Distances
     * @return returns the aproperate angle of the hood acording to the range
     */
    public double rangeToAngleOfHood(double distance) {
        distance = Math.round(distance);
        int index = 0;
        for(int i = 0; i < table.length; i++){
            double currentDistance = table[i][DISTANCE_COLUMN];

            if(currentDistance == distance){
                return table[i][ANGLE_OF_HOOD_COLUMN];
            }else if(currentDistance > distance){
                if(i == 0){
                    return table[0][ANGLE_OF_HOOD_COLUMN];
                }
                index = i;
                return linearInterpolationRPM(table, index, distance,ANGLE_OF_HOOD_COLUMN);

            }
        }
        return table[table.length - 1][ANGLE_OF_HOOD_COLUMN];
    }

    


    /**
     * calculats the RPM from the table and the distance and Interpolats inbetween the valuse
     * @param distance the distance between the robot and the target
     * @param table the table of RPMs and Distances
     * @return returns the target RPM from the table
     */
    public double rangeToRPM(double distance){
        distance = Math.round(distance);
        int index = 0;
        for(int i = 0; i < table.length; i++){
            double currentDistance = table[i][DISTANCE_COLUMN];

            if(currentDistance == distance){
                return table[i][RPM_COLUMN];
            }else if(currentDistance > distance){
                if(i == 0){
                    return table[0][RPM_COLUMN];
                }
                index = i;
                return linearInterpolationRPM(table, index, distance, RPM_COLUMN);

            }
        }
        return table[table.length - 1][RPM_COLUMN];
    }

    private int linearInterpolationRPM(int[][] table,int index,double distance, int TargetColumn){

        //find the range between the two known distances
        double distanceRange = table[index][DISTANCE_COLUMN] - table[index - 1][DISTANCE_COLUMN];
        double TargetRange = (table[index][TargetColumn] - table[index - 1][TargetColumn]);
        //calculate the new distance between the known distances linearly
        return (int)((distance - table[index - 1][DISTANCE_COLUMN]) / distanceRange * TargetRange) + table[index - 1][TargetColumn]; 

    }
}
