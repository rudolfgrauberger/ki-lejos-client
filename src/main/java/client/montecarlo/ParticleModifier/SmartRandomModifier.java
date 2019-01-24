package client.montecarlo.ParticleModifier;

import client.localization.Particle;
import client.montecarlo.ActionException;

import java.util.Random;

public class SmartRandomModifier implements IParticleModifier {

    private final static int MOVEMENT_VALUE = 2;
    private final static int Y_VALUE = 2;
    static Random r = new Random();

    public static int randInt(int min, int max) {
        Random rand;
        int randomNum = r.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    @Override
    public Particle modifyParticle(Particle particle) {

        int randomMovement = randInt(-MOVEMENT_VALUE , MOVEMENT_VALUE);
        int randomY = randInt(-Y_VALUE , Y_VALUE);
        //double randomRotation = ((r.nextDouble() * Math.PI * 2) / 16) - (Math.PI / 32);

        //particle.centerPoint.x += randomMovement;
        //particle.centerPoint.y += randomMovement;

        /*if (randomRotation > 0) {

            //particle.turnRight(randomRotation);
            //particle.
        } else {
            //particle.turnLeft(Math.abs(randomRotation));
        }*/

        particle.addYAxis(randomY);

        try {
            if (randomMovement > 0) {
                particle.moveForward(randomMovement);
            }
            else{
                particle.moveBackward(Math.abs(randomMovement));
            }
        } catch (ActionException e) {
            e.printStackTrace();
        }
        return particle;
    }
}
