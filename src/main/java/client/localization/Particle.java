package client.localization;

import client.montecarlo.ActionException;
import client.montecarlo.IMoveController;
import client.montecarlo.SensorDataSet;

import java.util.ArrayList;

public class Particle implements IMoveController {

    Map map;
    Point centerPoint;
    double currentRotation;
    Intersect forwardIntersect;
    Intersect leftIntersect;
    Intersect rightIntersect;




    public Particle(Map map, Point centerPoint, double rotation){
        this.map = map;
        this.centerPoint = centerPoint;
        this.currentRotation = rotation;
        calculateIntersects();

    }
    public void calculateIntersects(){
        //Helper.getRotationPoint(centerPoint , 1 , -Helper.QUARTER_CIRCLE);
        Intersect leftIntersect = calculateIntersect(Helper.getAngleOffset(currentRotation - Helper.QUARTER_CIRCLE) ,map.getLines() );
        Intersect forwardIntersect = calculateIntersect(Helper.getAngleOffset(currentRotation) ,map.getLines() );
        Intersect rightIntersect = calculateIntersect(Helper.getAngleOffset(currentRotation + Helper.QUARTER_CIRCLE) ,map.getLines() );

        this.forwardIntersect = forwardIntersect;
        this.leftIntersect = leftIntersect;
        this.rightIntersect= rightIntersect;
    }
    public Intersect calculateIntersect(double rotation, ArrayList<Line> lines ){
        Point direction = (Helper.getRotationPoint(centerPoint,1 , rotation));
        ArrayList<Point> intersects = Helper.rayCast(centerPoint , direction , lines);
        Point shortestIntersect = Helper.getShortest(centerPoint , intersects);
        if ( shortestIntersect != null){
            double realIntersectDistance = Helper.distance(centerPoint , shortestIntersect);
            Intersect intersect = new Intersect(shortestIntersect , realIntersectDistance);
            return  intersect;
        }
        System.out.println("ERROR DAMN!");
        //this.intersectPoint = intersect;
        return null;
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
        //Point newRealPoint = Helper.vectorAdd(currentAbsPosition , maginalizedRotationalPoint);
        centerPoint = maginalizedRotationalPoint;
        move();

    }

    @Override
    public void moveBackward(double cm) throws ActionException {
        Point currentAbsPosition = centerPoint;
        Point maginalizedRotationalPoint = Helper.getRotationPoint(centerPoint , cm , currentRotation + 2*Helper.QUARTER_CIRCLE);
        //Point newRealPoint = Helper.vectorSub(currentAbsPosition , maginalizedRotationalPoint);
        centerPoint = maginalizedRotationalPoint;
        move();
    }

    @Override
    public void turnLeft(double angle) {
        this.currentRotation -= angle;
        move();
    }

    @Override
    public void turnRight(double angle) {
        this.currentRotation+=angle;
        this.currentRotation = this.currentRotation % (2*Math.PI);
        move();
    }
    @Override
    public SensorDataSet getSensorDataSet() throws ActionException {
        return null;
    }

    public void move(){
        boolean inPolygon = map.checkPointInsidePolygon(this.centerPoint);
        if ( inPolygon) {
            calculateIntersects();
        }
    }
}
