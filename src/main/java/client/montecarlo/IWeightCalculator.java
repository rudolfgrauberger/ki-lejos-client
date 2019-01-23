package client.montecarlo;

import client.localization.Particle;

public interface IWeightCalculator {

    double calculateWeight(SensorDataSet robotSensor, IMoveController particle) throws ActionException;
}
