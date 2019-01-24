package client.montecarlo.Weight;

import client.localization.Helper;
import client.montecarlo.ActionException;
import client.montecarlo.IMoveController;
import client.montecarlo.SensorDataSet;
import client.montecarlo.Weight.IWeightCalculator;
import client.montecarlo.WeightStrategie.Gaussian1;
import client.montecarlo.WeightStrategie.Gaussian2;
import client.montecarlo.WeightStrategie.Gaussian3;
import client.montecarlo.WeightStrategie.IWeightStrategy;

public class AllDistanceWeightCalculator implements IWeightCalculator {

    IWeightStrategy ws = new Gaussian3();

    @Override
    public double calculateWeight(SensorDataSet robotSensor, IMoveController particle) throws ActionException {

        double r1 = Helper.lerp(robotSensor.getDistanceLeft() , MAX_DISTANCE_FORWARD);
        double r2 = Helper.lerp(robotSensor.getDistanceFront() , MAX_DISTANCE_FORWARD);
        double r3 = Helper.lerp(robotSensor.getDistanceRight(), MAX_DISTANCE_FORWARD);

        SensorDataSet sd = particle.getSensorDataSet();

        double p1 = Helper.lerp(sd.getDistanceLeft() , MAX_DISTANCE_FORWARD);
        double p2 = Helper.lerp(sd.getDistanceFront() , MAX_DISTANCE_FORWARD);
        double p3 = Helper.lerp(sd.getDistanceRight() , MAX_DISTANCE_FORWARD);

        /*System.out.println("Robotor (Partikel)");
        System.out.println("Rechts: (" + r3 + ", " + p3 + ")");
        System.out.println("Vorne: (" + r2 + ", " + p2 + ")");
        System.out.println("Links: (" + r1 + ", " + p1 + ")");


        System.out.println("Roboter Links: " + r1 +" IntersectAddr:");
        System.out.println("Roboter Vorne: " + r2 +" IntersectAddr:");
        System.out.println("Roboter Rechts: " + r3+" IntersectAddr:");

        System.out.println("Partikel Links: " + r1 );
        System.out.println("Partikel Vorne: " + r2 );
        System.out.println("Partikel Rechts: " + r3);*/


        double delta1 = (r1 - p1);
        //double delta2 = (r2 - p2);
        double delta3 = (r3 - p3);
        double weight = ( ws.getWeight(delta1) * ws.getWeight(delta3));

        return weight;
    }
}
