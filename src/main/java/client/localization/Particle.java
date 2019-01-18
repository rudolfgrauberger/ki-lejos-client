package client.localization;

import client.montecarlo.ActionException;
import client.montecarlo.IMoveController;
import client.montecarlo.SensorDataSet;

import java.util.ArrayList;
import java.util.Random;

public class Particle implements IMoveController {

    Map map;
    Point centerPoint;
    double currentRotation;
    Intersect forwardIntersect;
    Intersect leftIntersect;
    Intersect rightIntersect;

    Random r = new Random();
    double belief = r.nextDouble();

    boolean isValid = true;



    public static Particle createParticle(Map map){
        Point particleCenter = map.getPointInPolygon();
        Random rand = new Random();
        double randRotation = rand.nextDouble() * Math.PI * 2;
        Particle p = new Particle(map,particleCenter,randRotation);

        boolean valid = p.hasValidPosition();
        if ( !valid ) {
            return Particle.createParticle(map);
        }
        return p;
    }

    public Particle(Map map, Point centerPoint, double rotation){
        this.map = map;
        this.centerPoint = centerPoint;
        this.currentRotation = rotation;

    }
    public boolean calculateIntersects(){
        //Helper.getRotationPoint(centerPoint , 1 , -Helper.QUARTER_CIRCLE);
        Intersect leftIntersect = calculateIntersect(Helper.getAngleOffset(currentRotation - Helper.QUARTER_CIRCLE) ,map.getLines() );
        Intersect forwardIntersect = calculateIntersect(Helper.getAngleOffset(currentRotation) ,map.getLines() );
        Intersect rightIntersect = calculateIntersect(Helper.getAngleOffset(currentRotation + Helper.QUARTER_CIRCLE) ,map.getLines() );
        if ( leftIntersect == null || forwardIntersect == null || rightIntersect == null){
            return false;
        }
        this.forwardIntersect = forwardIntersect;
        this.leftIntersect = leftIntersect;
        this.rightIntersect= rightIntersect;
        return true;
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
        //System.out.println(centerPoint.toString() + " rotation: " + rotation);
        //System.out.println("ERROR DAMN!");
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
        afterMoveEvent();

    }

    @Override
    public void moveBackward(double cm) throws ActionException {
        Point currentAbsPosition = centerPoint;
        Point maginalizedRotationalPoint = Helper.getRotationPoint(centerPoint , cm , currentRotation + 2*Helper.QUARTER_CIRCLE);
        //Point newRealPoint = Helper.vectorSub(currentAbsPosition , maginalizedRotationalPoint);
        centerPoint = maginalizedRotationalPoint;
        afterMoveEvent();
    }

    @Override
    public void turnLeft(double angle) {
        this.currentRotation-=Helper.degreeToRadiand(angle);
        afterMoveEvent();
    }

    @Override
    public void turnRight(double angle) {
        this.currentRotation+=Helper.degreeToRadiand(angle);
        afterMoveEvent();
    }
    @Override
    public SensorDataSet getSensorDataSet() throws ActionException {
        SensorDataSet sds = new SensorDataSet(forwardIntersect.distance , leftIntersect.distance , rightIntersect.distance);
        return sds;
    }

    @Override
    public double getBelief() {
        return this.belief;
    }

    @Override
    public void setBelief(double belief) {
        this.belief = belief;
    }


    public boolean hasValidPosition(){
        boolean inPolygon = map.checkPointInsidePolygon(this.centerPoint);
        boolean intersects = calculateIntersects();
        return inPolygon && intersects;
    }
    public boolean afterMoveEvent(){
        boolean valPos = hasValidPosition();
        // Generate new Position
        if ( ! valPos ){
            Particle p = Particle.createParticle(map);

            this.map = p.map;
            this.centerPoint = p.centerPoint;
            this.currentRotation = p.currentRotation;
            this.forwardIntersect = p.forwardIntersect;
            this.leftIntersect = p.leftIntersect;
            this.rightIntersect = p.rightIntersect;
            this.belief = p.belief;
        }
        return valPos;
    }
}
