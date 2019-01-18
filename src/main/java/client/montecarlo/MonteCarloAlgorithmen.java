package client.montecarlo;

import client.localization.Helper;
import client.localization.Particle;

import java.util.ArrayList;
import java.util.Random;

public class MonteCarloAlgorithmen {

    public static double MAX_DISTANCE_FORWARD = 250.0;

    private IMoveController roboter;
    private ArrayList<IMoveController> partikels;
    private SensorDataSet latestRoboterDataSet;

    public MonteCarloAlgorithmen(IMoveController roboter) {
        this.roboter = roboter;
    }

    public ArrayList<IMoveController> run (ArrayList<IMoveController> partikels) throws ActionException{
        this.partikels = partikels;

        compareSensorDatas();
        moveCommand();
        compareSensorDatas();
        removePartikels();

        return  this.partikels;
    }

    private void moveCommand() throws ActionException{
        Random random = new Random();
        int commandNumber = random.nextInt(1);
        int angle;
        switch (commandNumber){
            //case forward
            case 0:
                moveForward();
                System.out.println("forward");
                break;
            //turn left
            case 1:
                angle = random.nextInt(180);
                roboter.turnLeft(angle);
                for (IMoveController partikel: partikels) {
                    partikel.turnLeft(angle);
                }
                System.out.println("left");
                break;
            //turn right
            case 2:
                angle = random.nextInt(180);
                roboter.turnRight(angle);
                for (IMoveController partikel: partikels) {
                    partikel.turnRight(angle);
                }
                System.out.println("right");
                break;
        }
    }
    private void compareSensorDatas() throws ActionException{
        this.latestRoboterDataSet = roboter.getSensorDataSet();
        System.out.println("Front"+latestRoboterDataSet.getDistanceFront());
        System.out.println("Left"+latestRoboterDataSet.getDistanceLeft());
        System.out.println("Right"+latestRoboterDataSet.getDistanceRight());

        double robotLeft = Helper.lerp(latestRoboterDataSet.getDistanceLeft()*100 , MAX_DISTANCE_FORWARD);
        double robotFront = Helper.lerp(latestRoboterDataSet.getDistanceFront()*100 , MAX_DISTANCE_FORWARD);
        double robotRight= Helper.lerp(latestRoboterDataSet.getDistanceRight()*100 , MAX_DISTANCE_FORWARD);

        /**
         *  TODO: fill getSensorData
         */

        for (IMoveController particle: partikels) {
            double particleLeft =  Helper.lerp(particle.getSensorDataSet().getDistanceLeft() , MAX_DISTANCE_FORWARD);
            double particleFront = Helper.lerp(particle.getSensorDataSet().getDistanceFront() , MAX_DISTANCE_FORWARD);
            double particleRight = Helper.lerp(particle.getSensorDataSet().getDistanceRight() , MAX_DISTANCE_FORWARD);

            double q1 = Math.min(particleLeft , robotLeft) / Math.max(particleLeft , robotLeft);
            double q2 = Math.min(particleFront , robotFront) / Math.max(particleFront , robotFront);
            double q3 = Math.min(particleRight , robotRight) / Math.max(particleRight , robotRight);
            double bel = q1 * q2 * q3;
            particle.setBelief(bel);
        }
    }
    private void removePartikels(){

    }
    //move controlles
    private void moveForward() throws ActionException{
        //get distance
        int distance = 30;
        //if at end turn around
        if((latestRoboterDataSet.getDistanceFront()*100) < 10)
        {
            roboter.turnRight(180);
            for (IMoveController partikel: partikels) {
                partikel.turnRight(180);
            }
            System.out.println("around");
        }
        else if((latestRoboterDataSet.getDistanceFront()*100) < distance-5)
        {
            distance = (int)((latestRoboterDataSet.getDistanceFront()*100)-5);
        }
        //move
        roboter.moveForward(distance);
        for (IMoveController partikel: partikels) {
            partikel.moveForward(distance);
        }
        System.out.println("distance"+distance+"; rob "+latestRoboterDataSet.getDistanceFront());
    }
}
