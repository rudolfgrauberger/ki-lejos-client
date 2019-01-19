package client.montecarlo;

import client.localization.Helper;
import client.localization.IMonteEventListener;

import java.util.List;
import java.util.Random;

public class MonteCarloAlgorithmen {

    public static double MAX_DISTANCE_FORWARD = 250.0;

    private static double REUSE_GRADE = 0.8d;

    private IMoveController roboter;
    private List<IMoveController> partikels;
    private SensorDataSet latestRoboterDataSet;

    private IResampler resampler;
    private IParticleGenerator generator;

    public MonteCarloAlgorithmen(IMoveController roboter, IParticleGenerator generator, IResampler resampler) {
        this.roboter = roboter;
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

        return this.partikels;
    }
    public void runAsync (List<IMoveController> partikels, IMonteEventListener listner) throws ActionException{
        List<IMoveController> newPartikels = run(partikels);
        listner.onMonteDone(newPartikels);
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

        int reuseParticleCount = (int)(this.partikels.size() * REUSE_GRADE);
        int renewParticleCount = this.partikels.size() - reuseParticleCount;
        List<IMoveController> result = resampler.resample(this.partikels, reuseParticleCount);
        this.partikels = result;

        System.out.println("Renewed count: " + renewParticleCount);

        while (renewParticleCount > 0) {
            this.partikels.add(generator.getRandomParticle());
            --renewParticleCount;
        }
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
    }
}
