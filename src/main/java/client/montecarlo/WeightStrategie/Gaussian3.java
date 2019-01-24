package client.montecarlo.WeightStrategie;

public class Gaussian3 implements IWeightStrategy {
    @Override
    public double getWeight(double sensorDiff) {
        return 1 / Math.exp(700*Math.pow(sensorDiff, 2));
    }
}
