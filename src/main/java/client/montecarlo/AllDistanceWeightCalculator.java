package client.montecarlo;

import client.localization.Helper;

public class AllDistanceWeightCalculator implements IWeightCalculator {
    @Override
    public double calculateWeight(SensorDataSet robotSensor, IMoveController particle) throws ActionException {

        double r1 = Helper.lerp(robotSensor.getDistanceLeft() , 255.0);
        double r2 = Helper.lerp(robotSensor.getDistanceFront() , 255.0);
        double r3 = Helper.lerp(robotSensor.getDistanceRight(), 255.0);

        double p1 = Helper.lerp(particle.getSensorDataSet().getDistanceLeft() , 255.0);
        double p2 = Helper.lerp(particle.getSensorDataSet().getDistanceFront() , 255.0);
        double p3 = Helper.lerp(particle.getSensorDataSet().getDistanceRight() , 255.0);


        double delta1 = (r1 - p1);
        double delta2 = (r2 - p2);
        double delta3 = (r3 - p3);
        double weight =Helper.getWeight(delta1) * Helper.getWeight(delta2) * Helper.getWeight(delta3);

        return weight;
        /*double distanceParticle = particle.getSensorDataSet().getDistanceFront();
        double distanceRobot = robotSensor.getDistanceFront() * 100;
        double robotFront = Helper.lerp(distanceParticle, distanceRobot);
        return robotFront;*/
    }
}
