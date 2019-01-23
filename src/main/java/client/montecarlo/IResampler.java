package client.montecarlo;

import client.localization.Particle;

import java.util.List;

public interface IResampler {

   List<IMoveController> resample(List<IMoveController> particles, double reuseGrade, IParticleGenerator generator);
}
