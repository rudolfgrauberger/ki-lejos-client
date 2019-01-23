package client.montecarlo;

public class SensorDataSet {
    private double distFront;
    private double distLeft;
    private double distRight;

    public  SensorDataSet(double distanceFront, double distanceLeft, double distanceRight){
        this.distFront = distanceFront;
        this.distLeft = distanceLeft;
        this.distRight = distanceRight;
    }

    //getter
    public double getDistanceFront() {
        return distFront;
    }
    public double getDistanceLeft(){
        return distLeft;
    }
    public double getDistanceRight(){
        return distRight;
    }
}
