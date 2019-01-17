package client.localization;

import java.util.ArrayList;

public class Helper {


    public static final int BUILDING_WIDTH_CM = 600;
    public static final int BUILDING_HEIGHT_CM = 150;


    // Math constancs

    public static final double QUARTER_CIRCLE = Math.PI / 2.0;

    /*
        Helpers´ by the master himself ;)
     */
    /*public static double rel ( int max , int cur){
        return ( (double) cur / (double) max);
    }/*
    /*public static int abs ( int  max , double rel){
        return  ( int ) ((double)max * (double)rel);
    }*/
    /*public static int absMapX ( double rel){
        int a = abs(CANVAS_WITDH ,rel);
        return  a;
    }*/
    /*public static int absMapY ( double rel){
        return  abs(CANVAS_HEIGHT, rel);
    }*/
    /*static public Point absMapPoint ( Point point){
        return new Point(abs(CANVAS_WITDH , point.x) , abs(CANVAS_HEIGHT , point.y));
    }*/
    static public double distance ( Point a , Point b ){
        return Math.sqrt(Math.pow(b.x - a.x , 2) + Math.pow(b.y - a.y , 2) );
    }
    static public boolean isBetween( Point a , Point b , Point c){
        double dline =  Math.sqrt(Math.pow(b.x - a.x , 2) + Math.pow(b.y - b.y , 2) );
        double dPoint =  Math.sqrt(Math.pow(c.x - a.x , 2) + Math.pow(c.y - b.y , 2) );
        return dPoint <= dline;
    }
    static public double getAbsDistance(Point relA , Point relB){
        double pixelsA_X = relA.x;
        double pixelsA_Y = relA.y;
        double pixelsB_X = relB.x;
        double pixelsB_Y = relB.y;
        return distance(new Point(pixelsA_X , pixelsA_Y) ,  new Point(pixelsB_X , pixelsB_Y));
    }
    /*static public double absRealX ( double rel){
        return rel * (double)BUILDING_WIDTH_CM;
    }*/
    /*static public double absRealY ( double rel){
        return rel * (double)BUILDING_HEIGHT_CM;
    }*/
    /*static public Point absRealPoint ( Point relPoint ){
        return new Point(absRealX(relPoint.x) , absRealY(relPoint.y));
    }*/
    static public double length(Point v){
        return Math.sqrt( Math.pow(v.x , 2) + Math.pow(v.y , 2) );
    }
    static public Point magnitude(Point v){
        double x = (1/length(v)) * v.x;
        double y = (1/length(v)) * v.y;
        return new Point(x,y);
    }
    static public Point getRotationPoint(Point center , double radius , double rotation ){
        double x = center.x + Math.cos(rotation) * radius;
        double y = center.y  + Math.sin(rotation) * radius;   // <-- Good quess

        return new Point(x,y);
    }
    static Point vectorAdd( Point a , Point b){
        return new Point(a.x + b.x , a.y + b.y);
    }
    static Point vectorSub( Point a , Point b){
        return new Point(a.x - b.x , a.y - b.y);
    }
    static Point getRelByRealPoint(Point real){
        return new Point( real.x / BUILDING_WIDTH_CM, real.y / BUILDING_HEIGHT_CM );
    }
    static double degreeToRadiand(int degree){
        return ( (double) degree/360.0) * (Math.PI * 2);
    }

    public static ArrayList<Point> rayCast (Point from , Point to , ArrayList<Line> lines){
        ArrayList<Point> points = new ArrayList<>();
        Point shortest = null;
        for (Line l : lines) {
            Point lineA = new Point(l.x1, l.y1);
            Point lineB = new Point(l.x2, l.y2);
            Point interception = calculateInterceptionPoint(from.translate(), to.translate(), lineA.translate(), lineB.translate());

            if (interception != null) {
                Point  xp = interception.translate();
                boolean add = false;
                if ( lineA.x == lineB.x){
                    if ( xp.y >= lineA.y && xp.y <= lineB.y){
                        add = true;
                    }
                    if ( xp.y >= lineB.y && xp.y <= lineA.y){
                        add = true;
                    }
                }
                else if ( lineA.y == lineB.y){
                    if ( xp.x >= lineA.x && xp.x <= lineB.x){
                        add = true;
                    }
                    if ( xp.x >= lineB.x && xp.x <= lineA.x){
                        add = true;
                    }
                }
                if ( add) {
                    /**
                     *  TODO: check if angle ist greater than 90 degrees
                     */
                    Point pointCompareA = new Point(to.x - from.x , to.y - from.y);
                    Point pointCompareB = new Point(xp.x - from.x , xp.y - from.y);
                    double dotProductA = pointCompareA.x * pointCompareB.x + pointCompareA.y * pointCompareB.y;
                    double negProd = Helper.length(pointCompareA) * Helper.length(pointCompareB) * -1;

                    if ( dotProductA / negProd < 0.002){
                        points.add(xp);
                    }
                }
            }
        }
        return points;
    }
    public static Point calculateInterceptionPoint(Point A, Point B, Point C, Point D) {
        // Line AB represented as a1x + b1y = c1
        double a1 = B.y - A.y;
        double b1 = A.x - B.x;
        double c1 = a1 * (A.x) + b1 * (A.y);


        // Line CD represented as a2x + b2y = c2
        double a2 = D.y - C.y;
        double b2 = C.x - D.x;
        double c2 = a2 * (C.x) + b2 * (C.y);

        double determinant = a1 * b2 - a2 * b1;

        if (determinant == 0) {
            // The lines are parallel. This is simplified
            // by returning a pair of FLT_MAX
            return null;//new Point(Double.MAX_VALUE, Double.MAX_VALUE);
        } else {
            double x = (b2 * c1 - b1 * c2) / determinant;
            double y = (a1 * c2 - a2 * c1) / determinant;
            return new Point(x, y);
        }
    }
    public static Point getShortest( Point from , ArrayList<Point> points){
        Point shortest = null;
        for ( Point p : points){
            if ( shortest == null){
                shortest = p;
            }else{
                if ( Helper.distance(from , p)  <  Helper.distance(from , shortest)){
                    shortest = p;
                }
            }
        }
        return shortest;
    }
    public static double getAngleOffset(double angle){
        if ( angle>=0){
            return angle % 360;
        }else{
            return (Math.PI * 2 ) - Math.abs(angle) % (Math.PI * 2);
        }

    }
}
