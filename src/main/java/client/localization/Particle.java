package client.localization;

import client.montecarlo.ActionException;
import client.montecarlo.IMoveController;

import java.util.ArrayList;
import client.montecarlo.IMoveController;
import client.montecarlo.SensorDataSet;

public class Particle implements IMoveController {

    Point centerPoint;
    double rotation;
    Intersect intersectPoint;

    public Particle(Point centerPoint, double rotation){
        this.centerPoint = centerPoint;
        this.rotation = rotation;


    }
    public void calculateIntersect(ArrayList<Line> lines ){
        Point direction = (Helper.getRotationPoint(centerPoint,0.050 , rotation));
        ArrayList<Point> intersects = Helper.rayCast(centerPoint , direction , lines);
        Point shortestIntersect = Helper.getShortest(centerPoint , intersects);
        double realIntersectDistance = Helper.distance(Helper.absRealPoint(centerPoint) , Helper.absRealPoint(shortestIntersect));
        Intersect intersect = new Intersect(shortestIntersect , realIntersectDistance);
        this.intersectPoint = intersect;
        System.out.println("Distance: "+  realIntersectDistance );
    }
    /*public void setIntersect( Point relPoint, Point absPoint , double absDistance){
        intersectPoint = new Intersect(relPoint , absPoint , absDistance);
    }*/
    private Point absRealPoint ( Point relPoint ){
        return new Point(Helper.absRealX(relPoint.x) , Helper.absRealY(relPoint.y));
    }

    @Override
    public void moveForward(int cm) {
        Point currentAbsPosition = Helper.absRealPoint(centerPoint);
        Point maginalizedRotationalPoint = Helper.getRotationPoint(centerPoint , cm , rotation);
        Point newRealPoint = Helper.vectorAdd(currentAbsPosition , maginalizedRotationalPoint);
        centerPoint = Helper.getRelByRealPoint(newRealPoint);
    }

    @Override
    public void moveBackward(int cm) {
        Point currentAbsPosition = Helper.absRealPoint(centerPoint);
        Point maginalizedRotationalPoint = Helper.getRotationPoint(centerPoint , cm , rotation);
        Point newRealPoint = Helper.vectorSub(currentAbsPosition , maginalizedRotationalPoint);
        centerPoint = Helper.getRelByRealPoint(newRealPoint);
    }

    @Override
    public void turnLeft(int angle) {
        this.rotation -= angle;

    }

    @Override
    public void turnRight(int angle) {
        this.rotation+=angle;
        this.rotation = this.rotation % (2*Math.PI);
    }

    @Override
    public SensorDataSet getSensorDataSet() throws ActionException {
        return null;
    }

    class Intersect{
        Point relPoint;
        double absDistance;

        public Intersect(Point relPoint, double absDistance) {
            this.relPoint = relPoint;
            this.absDistance = absDistance;
        }
    }
}
