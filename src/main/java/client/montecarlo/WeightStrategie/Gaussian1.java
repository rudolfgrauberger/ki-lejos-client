package client.montecarlo.WeightStrategie;

public class Gaussian1 implements IWeightStrategy {
    @Override
    public double getWeight(double sensorDiff) {
        return 1 / Math.exp(5*Math.pow(sensorDiff, 2));
    }
}
