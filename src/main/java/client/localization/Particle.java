package client.localization;

import client.montecarlo.ActionException;
import client.montecarlo.IMoveController;
import client.montecarlo.SensorDataSet;

import java.util.ArrayList;

public class Particle implements IMoveController {

    Point centerPoint;
    double currentRotation;
    Intersect forwardIntersect;
    Intersect leftIntersect;
    Intersect rightIntersect;



    public Particle(Point centerPoint, double rotation){
        this.centerPoint = centerPoint;
        this.currentRotation = rotation;
    }
    public void calculateIntersects(ArrayList<Line> lines ){
        //Helper.getRotationPoint(centerPoint , 1 , -Helper.QUARTER_CIRCLE);
        Intersect leftIntersect = calculateIntersect(Helper.getAngleOffset(currentRotation - Helper.QUARTER_CIRCLE) ,lines );
        Intersect forwardIntersect = calculateIntersect(Helper.getAngleOffset(currentRotation) ,lines );
        Intersect rightIntersect = calculateIntersect(Helper.getAngleOffset(currentRotation + Helper.QUARTER_CIRCLE) ,lines );

    }
    public Intersect calculateIntersect(double rotation, ArrayList<Line> lines ){
        Point direction = (Helper.getRotationPoint(centerPoint,0.050 , rotation));
        ArrayList<Point> intersects = Helper.rayCast(centerPoint , direction , lines);
        Point shortestIntersect = Helper.getShortest(centerPoint , intersects);
        double realIntersectDistance = Helper.distance(centerPoint , shortestIntersect);
        Intersect intersect = new Intersect(shortestIntersect , realIntersectDistance);
        //this.intersectPoint = intersect;
        return  intersect;
        //System.out.println("Distance: "+  realIntersectDistance );
    }
    private Point absRealPoint ( Point relPoint ){
        return new Point(relPoint.x , relPoint.y);
    }
    class Intersect{
        Point point;
        double distance;

        public Intersect(Point point, double distance) {
            this.point = point;
            this.distance = distance;
        }
    }

    @Override
    public void moveForward(double cm) throws ActionException {
        Point currentAbsPosition = centerPoint;
        Point maginalizedRotationalPoint = Helper.getRotationPoint(centerPoint , cm , currentRotation);
        Point newRealPoint = Helper.vectorAdd(currentAbsPosition , maginalizedRotationalPoint);
        centerPoint = Helper.getRelByRealPoint(newRealPoint);
    }

    @Override
    public void moveBackward(double cm) throws ActionException {
        Point currentAbsPosition = centerPoint;
        Point maginalizedRotationalPoint = Helper.getRotationPoint(centerPoint , cm , currentRotation);
        Point newRealPoint = Helper.vectorSub(currentAbsPosition , maginalizedRotationalPoint);
        centerPoint = Helper.getRelByRealPoint(newRealPoint);
    }

    @Override
    public void turnLeft(double angle) {
        this.currentRotation -= angle;

    }

    @Override
    public void turnRight(double angle) {
        this.currentRotation+=angle;
        this.currentRotation = this.currentRotation % (2*Math.PI);
    }

    @Override
    public SensorDataSet getSensorDataSet() throws ActionException {
        return null;
    }
}
