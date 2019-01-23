package client.montecarlo.Weight;

import client.localization.Helper;
import client.montecarlo.ActionException;
import client.montecarlo.IMoveController;
import client.montecarlo.SensorDataSet;
import client.montecarlo.Weight.IWeightCalculator;

public class FrontDistanceWeightCalculator implements IWeightCalculator {

    @Override
    public double calculateWeight(SensorDataSet robotSensor, IMoveController particle) throws ActionException {
        double distanceParticle = Helper.lerp(particle.getSensorDataSet().getDistanceFront(), MAX_DISTANCE_FORWARD);
        double distanceRobot = Helper.lerp(robotSensor.getDistanceFront() * 100, MAX_DISTANCE_FORWARD);
        double robotFront = Helper.getWeight(distanceParticle - distanceRobot);
        return robotFront;
    }
}
