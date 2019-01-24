package client.montecarlo.ParticleModifier;

import client.localization.Particle;
import client.montecarlo.ActionException;

import java.util.Random;

public class SmartRandomModifier implements IParticleModifier {
    Random r = new Random();

    @Override
    public Particle modifyParticle(Particle particle) {
        double randomMovement = (r.nextDouble() * 6) - 3;
        double randomRotation = ((r.nextDouble() * Math.PI * 2) / 16) - (Math.PI / 32);

        System.out.println("Random Movement: " + randomMovement);
        System.out.println("Random Rotation: " + randomRotation);
        //particle.centerPoint.x += randomMovement;
        //particle.centerPoint.y += randomMovement;

        if (randomRotation >= 0) {
            particle.turnRight(randomRotation);
        } else {
            particle.turnLeft(Math.abs(randomRotation));
        }

        try {
            if (randomMovement >= 0) {
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
