package client.montecarlo;

import client.localization.Helper;
import client.localization.Particle;

public class FrontDistanceWeightCalculator implements IWeightCalculator {

    @Override
    public double calculateWeight(SensorDataSet robotSensor, IMoveController particle) throws ActionException {
        double distanceParticle = Helper.lerp(particle.getSensorDataSet().getDistanceFront(), MAX_DISTANCE_FORWARD);
        double distanceRobot = Helper.lerp(robotSensor.getDistanceFront() * 100, MAX_DISTANCE_FORWARD);
        //double distance = Math.abs(distanceRobot - distanceParticle);
        double robotFront = Helper.getWeight(distanceParticle - distanceRobot);
        return robotFront;
    }
}
