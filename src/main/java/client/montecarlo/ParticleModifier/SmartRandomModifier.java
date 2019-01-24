package client.montecarlo.ParticleModifier;

import client.localization.Particle;
import client.montecarlo.ActionException;

import java.util.Random;

public class SmartRandomModifier implements IParticleModifier {
    Random r = new Random();

    @Override
    public Particle modifyParticle(Particle particle) {
        int randomMovement = (r.nextInt(3)) - 1;
        double randomRotation = ((r.nextDouble() * Math.PI * 2) / 16) - (Math.PI / 32);

        System.out.println("Random Movement: " + randomMovement);
        System.out.println("Random Rotation: " + randomRotation);
        //particle.centerPoint.x += randomMovement;
        //particle.centerPoint.y += randomMovement;

        /*if (randomRotation > 0) {

            //particle.turnRight(randomRotation);
            //particle.
        } else {
            //particle.turnLeft(Math.abs(randomRotation));
        }*/
        particle.addYAxis(randomMovement);

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
