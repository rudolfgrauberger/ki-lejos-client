package client.localization;

import client.montecarlo.ActionException;
import client.montecarlo.IMoveController;
import client.montecarlo.SensorDataSet;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class Particle implements IMoveController {

    public UUID id = UUID.randomUUID();

    Map map;
    private Point centerPoint;
    private double currentRotation;
    private Intersect forwardIntersect;
    private Intersect leftIntersect;
    private Intersect rightIntersect;

    Random r = new Random();
    private double belief = 1d;

    private boolean isValid = true;

    protected Color color;



    public Particle(Map map, Point centerPoint, double rotation){
        this.map = map;
        this.centerPoint = centerPoint;
        int rot = r.nextInt(2);

        if ( rot == 0) {
            this.currentRotation = 0;
        }
        if ( rot == 1 ){
            this.currentRotation = 2* Helper.QUARTER_CIRCLE;
        }
        //afterMoveEvent();
        //System.out.println("rotation: "+currentRotation);
        //this.currentRotation = rotation;
        //this.color = new Color(Color.DARKRED.getRed(), Color.DARKRED.getGreen(), Color.DARKRED.getBlue(), 0.3);
    }

    public void addYAxis(int value){
        this.centerPoint.y += value;
        afterMoveEvent();
    }

    public Particle(Particle other) {
        this(other.map, other.centerPoint, other.currentRotation);
        this.currentRotation = other.currentRotation;
        this.forwardIntersect = new Intersect(other.forwardIntersect.point, other.forwardIntersect.distance);
        this.rightIntersect = new Intersect(other.rightIntersect.point, other.rightIntersect.distance);
        this.leftIntersect = new Intersect(other.leftIntersect.point, other.leftIntersect.distance);
        this.belief = other.belief;
        this.isValid = other.isValid;
        this.color = other.color;
        this.r = other.r;
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
    public Point getPoint() {
        return this.centerPoint;
    }

    @Override
    public double getCurrentRotation() {
        return this.currentRotation;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public double getBelief() {
        return this.belief;
    }

    @Override
    public void setBelief(double belief) {
        this.belief = belief;
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    @Override
    public Intersect getForwardIntersect() {
        return this.forwardIntersect;
    }

    @Override
    public Intersect getLeftIntersect() {
        return this.leftIntersect;
    }

    @Override
    public Intersect getRightIntersect() {
        return this.rightIntersect;
    }


    public boolean hasValidPosition(){
        boolean inPolygon = map.checkPointInsidePolygon(this.centerPoint);
        boolean intersects = calculateIntersects();
        /*try {
            System.out.println("Partikel Links: " + getSensorDataSet().getDistanceLeft());
            System.out.println("Partikel Vorne: " + getSensorDataSet().getDistanceFront());
            System.out.println("Partikel Rechts: " + getSensorDataSet().getDistanceRight());
        } catch (ActionException e) {
            e.printStackTrace();
        }*/
        return inPolygon && intersects;




    }

    private void afterMoveEvent(){

        isValid = hasValidPosition();
    }
}
