package frc.robot.utils;

public class ProjectileArkSimulation {

    public final double g = 8.9;
    public final double cd = 0.8;

    public ProjectileArkSimulation(){
        
    }

    public double thadaWithOutDrag(double distance, double volicety){
        return 0.5*Math.atan((distance*g)/Math.pow(volicety,2));
    }
}
