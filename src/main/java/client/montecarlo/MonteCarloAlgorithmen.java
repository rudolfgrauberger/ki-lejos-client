package client.montecarlo;

import client.localization.Helper;
import client.localization.Particle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MonteCarloAlgorithmen {

    public static double MAX_DISTANCE_FORWARD = 250.0;

    private static double REUSE_GRADE = 0.8d;

    private IMoveController roboter;
    private List<IMoveController> partikels;
    private SensorDataSet latestRoboterDataSet;
    private boolean looksInDriveDirection;

    private IResampler resampler;
    private IParticleGenerator generator;

    public MonteCarloAlgorithmen(IMoveController roboter, IParticleGenerator generator, IResampler resampler) {
        this.roboter = roboter;
        looksInDriveDirection = true;
        this.resampler = resampler;
        this.generator = generator;
    }

    public MonteCarloAlgorithmen(IMoveController roboter, IParticleGenerator generator) {
        this(roboter, generator, new RouletteWheelResampler());
    }

    public List<IMoveController> run (List<IMoveController> partikels) throws ActionException{
        this.partikels = partikels;

        compareSensorDatas();
        moveCommand();
        compareSensorDatas();
        resamplePartikels();
        addPartikels();

        return this.partikels;
    }

    private void moveCommand() throws ActionException{
        Random random = new Random();
        int commandNumber = random.nextInt(1);
        switch (commandNumber){
            //case forward
            case 0:
                moveForward();
                System.out.println("forward");
                break;
            //turn left
            case 1:
                roboter.turnLeft(90);
                for (IMoveController partikel: partikels) {
                    partikel.turnLeft(90);
                }
                looksInDriveDirection = !looksInDriveDirection;
                System.out.println("left");
                break;
            //turn right
            case 2:
                roboter.turnRight(90);
                for (IMoveController partikel: partikels) {
                    partikel.turnRight(90);
                }
                looksInDriveDirection = !looksInDriveDirection;
                System.out.println("fuck off");
                break;
            //turn around
            case 3:
                roboter.turnRight(180);
                for (IMoveController partikel: partikels) {
                    partikel.turnRight(180);
                }
                System.out.println("around");
                break;
            //case forward
            case 4:
                moveForward();
                System.out.println("forward");
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
    private void resamplePartikels(){

        int reuseParticles = (int)(this.partikels.size() * REUSE_GRADE);
        List<IMoveController> result = resampler.resample(this.partikels, reuseParticles);
        this.partikels = result;
    }

    private void addPartikels(){

        int renew = this.partikels.size() - (int)(this.partikels.size() * REUSE_GRADE);

        System.out.println("Renewed count: " + renew);

        while (renew > 0) {
            this.partikels.add(generator.getRandomParticle());
            --renew;
        }

        System.out.println("New particle count: " + this.partikels.size());
    }

    //move controlles
    private void moveForward() throws ActionException{
        Random random = new Random();
        //turn to forward direction
        if(!looksInDriveDirection){
            if( random.nextInt(2) > 0) {
                roboter.turnLeft(90);
                for (IMoveController partikel: partikels) {
                    partikel.turnLeft(90);
                }
            }
            else {
                roboter.turnRight(90);
                for (IMoveController partikel: partikels) {
                    partikel.turnRight(90);
                }
            }
            looksInDriveDirection = true;
        }
        //get distance
        int distance = 50;
        if((latestRoboterDataSet.getDistanceFront()*100) < distance)
            distance = (int)((latestRoboterDataSet.getDistanceFront() - 0.05)*100);
        //move
        roboter.moveForward(distance);
        for (IMoveController partikel: partikels) {
            partikel.moveForward(distance);
        }
        System.out.println("distance"+distance+"; rob "+latestRoboterDataSet.getDistanceFront());
    }
}
