package client.montecarlo.ParticleModifier;

import client.localization.Particle;

public class NoModilier implements IParticleModifier {
    @Override
    public Particle modifyParticle(Particle particle) {
        return particle;
    }
}
