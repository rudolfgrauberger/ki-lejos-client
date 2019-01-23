package client.montecarlo.Weight;

import client.localization.Helper;
import client.montecarlo.ActionException;
import client.montecarlo.IMoveController;
import client.montecarlo.SensorDataSet;
import client.montecarlo.Weight.IWeightCalculator;

public class AllDistanceWeightCalculator implements IWeightCalculator {
    @Override
    public double calculateWeight(SensorDataSet robotSensor, IMoveController particle) throws ActionException {

        double r1 = Helper.lerp(robotSensor.getDistanceLeft() , 255.0);
        double r2 = Helper.lerp(robotSensor.getDistanceFront() , 255.0);
        double r3 = Helper.lerp(robotSensor.getDistanceRight(), 255.0);

        SensorDataSet sd = particle.getSensorDataSet();

        double p1 = Helper.lerp(sd.getDistanceLeft() , 255.0);
        double p2 = Helper.lerp(sd.getDistanceFront() , 255.0);
        double p3 = Helper.lerp(sd.getDistanceRight() , 255.0);

        System.out.println("Robotor (Partikel)");
        System.out.println("Rechts: (" + r3 + ", " + p3 + ")");
        System.out.println("Vorne: (" + r2 + ", " + p2 + ")");
        System.out.println("Links: (" + r1 + ", " + p1 + ")");


        System.out.println("Roboter Links: " + r1 +" IntersectAddr:");
        System.out.println("Roboter Vorne: " + r2 +" IntersectAddr:");
        System.out.println("Roboter Rechts: " + r3+" IntersectAddr:");

        System.out.println("Partikel Links: " + r1 );
        System.out.println("Partikel Vorne: " + r2 );
        System.out.println("Partikel Rechts: " + r3);


        double delta1 = (r1 - p1);
        double delta2 = (r2 - p2);
        double delta3 = (r3 - p3);
        double weight = ( Helper.getWeight(delta1) * Helper.getWeight(delta2) * Helper.getWeight(delta3));

        return weight;
        /*double distanceParticle = particle.getSensorDataSet().getDistanceFront();
        double distanceRobot = robotSensor.getDistanceFront() * 100;
        double robotFront = Helper.lerp(distanceParticle, distanceRobot);
        return robotFront;*/
    }
}
