package client.montecarlo.Weight;

import client.localization.Particle;
import client.montecarlo.ActionException;
import client.montecarlo.IMoveController;
import client.montecarlo.SensorDataSet;

public interface IWeightCalculator {
    public static double MAX_DISTANCE_FORWARD = 250.0;

    double calculateWeight(SensorDataSet robotSensor, IMoveController particle) throws ActionException;
}
