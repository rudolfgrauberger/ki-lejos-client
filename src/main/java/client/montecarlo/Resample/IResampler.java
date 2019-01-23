package client.montecarlo.Resample;

import client.localization.Particle;
import client.montecarlo.IMoveController;
import client.montecarlo.IParticleGenerator;

import java.util.List;

public interface IResampler {

   List<IMoveController> resample(List<IMoveController> particles, double reuseGrade, IParticleGenerator generator);
}
