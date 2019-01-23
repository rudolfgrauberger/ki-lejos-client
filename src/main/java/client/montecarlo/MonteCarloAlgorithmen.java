package client.montecarlo;

import client.localization.Helper;
import client.localization.IMonteEventListener;
import client.localization.Particle;
import client.localization.ParticleFactory;

import java.util.List;
import java.util.Random;

public class MonteCarloAlgorithmen {

    private static double REUSE_GRADE = 0.8d;

    private IMoveController roboter;
    private List<IMoveController> partikels;

    private IResampler resampler;
    private IWeightCalculator calculator;
    private IParticleGenerator generator;

    private SensorDataSet latestRoboterDataSet;

    public MonteCarloAlgorithmen(IMoveController roboter, IParticleGenerator generator, IResampler resampler) {
        this.roboter = roboter;
        this.resampler = resampler;
        this.generator = generator;
        this.calculator = new FrontDistanceWeightCalculator();
    }

    public MonteCarloAlgorithmen(IMoveController roboter, IParticleGenerator generator) {
        this(roboter, generator, new RouletteWheelResampler());
    }

    public IResampler getResampler() {
        return resampler;
    }

    public void setResampler(IResampler resampler) {
        this.resampler = resampler;
    }

    public IWeightCalculator getCalculator() {
        return calculator;
    }

    public void setCalculator(IWeightCalculator calculator) {
        this.calculator = calculator;
    }

    public IParticleGenerator getGenerator() {
        return generator;
    }

    public void setGenerator(IParticleGenerator generator) {
        this.generator = generator;
    }

    public List<IMoveController> run (List<IMoveController> partikels) throws ActionException{
        this.partikels = partikels;
        for (IMoveController p: this.partikels) {
           Particle particle = (Particle)p;
           System.out.println("Vorher (ID: " + particle.id + ") -> " + particle.centerPoint.toString());
        }

        //calculateWeights();
        resamplePartikels();
        moveCommand();
        calculateWeights();

       for (IMoveController p: this.partikels) {
          Particle particle = (Particle)p;
          System.out.println("Nachher (ID: " + particle.id + ") -> " + particle.centerPoint.toString());
       }

        return this.partikels;
    }
    public void runAsync (List<IMoveController> partikels, IMonteEventListener listner) throws ActionException{
        List<IMoveController> newPartikels = run(partikels);
        listner.onMonteDone(newPartikels);
    }

    private void moveCommand() throws ActionException{
        Random random = new Random();
        int commandNumber = random.nextInt(3);
        int angle;
        switch (commandNumber){
            //case forward
            case 0:
                moveForward();
                break;
            //turn left
            case 1:
                turnLeft();
                break;
            //turn right
            case 2:
                turnRight();
                break;
        }
    }

    private void resamplePartikels(){

        int particleCount = this.partikels.size();
        int reuseParticleCount = (int)Math.ceil(particleCount * REUSE_GRADE);
        List<IMoveController> result = resampler.resample(this.partikels, reuseParticleCount);
        this.partikels = result;

        int renewParticleCount = particleCount - this.partikels.size();
        System.out.println("Renewed count: " + renewParticleCount);

        while (renewParticleCount > 0) {
            this.partikels.add(generator.getRandomParticle());
            --renewParticleCount;
        }
    }

    //move controlles
    private void moveForward() throws ActionException{
        System.out.println("forward");
        //get distance
        int distance = 30;


        //if at end turn around
        if((latestRoboterDataSet.getDistanceFront()) < 10)
        {
            roboter.turnRight(180);
            for (IMoveController partikel: partikels) {
                partikel.turnRight(180);
            }
            System.out.println("around");
        }
        else if((latestRoboterDataSet.getDistanceFront()) < distance-5)
        {
            distance = (int)((latestRoboterDataSet.getDistanceFront()*100)-5);
        }

        //move
        roboter.moveForward(distance);
        for (IMoveController partikel: partikels) {
            partikel.moveForward(distance);
        }
    }

    private void turnLeft() throws ActionException {
        System.out.println("left");
        Random random = new Random();
        int angle = random.nextInt(180);

        roboter.turnLeft(angle);
        for (IMoveController partikel: partikels) {
            partikel.turnLeft(angle);
        }
    }

    private void turnRight() throws ActionException {
        System.out.println("right");
        Random random = new Random();
        int angle = random.nextInt(180);
        roboter.turnRight(angle);
        for (IMoveController partikel: partikels) {
            partikel.turnRight(angle);
        }
    }

    private void calculateWeights() throws ActionException {
        latestRoboterDataSet = roboter.getSensorDataSet();
        for (IMoveController particle: this.partikels) {
            particle.setBelief(calculator.calculateWeight(latestRoboterDataSet, particle));
        }
    }
}
