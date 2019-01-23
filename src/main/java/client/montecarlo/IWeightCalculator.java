package client.montecarlo;

import client.localization.Particle;

public interface IWeightCalculator {
    public static double MAX_DISTANCE_FORWARD = 250.0;

    double calculateWeight(SensorDataSet robotSensor, IMoveController particle) throws ActionException;
}
