package client.montecarlo.WeightStrategie;

public class Gaussian2 implements IWeightStrategy {
    @Override
    public double getWeight(double sensorDiff) {
        return 1 / Math.exp(100*Math.pow(sensorDiff, 2));
    }
}
