package client.montecarlo;

import client.localization.Helper;
import client.localization.Particle;

public class FrontDistanceWeightCalculator implements IWeightCalculator {

    @Override
    public double calculateWeight(SensorDataSet robotSensor, IMoveController particle) throws ActionException {
        double distanceParticle = particle.getSensorDataSet().getDistanceFront();
        double distanceRobot = robotSensor.getDistanceFront() * 100;
        double robotFront = Helper.lerp(distanceParticle, distanceRobot);
        return robotFront;
    }
}
