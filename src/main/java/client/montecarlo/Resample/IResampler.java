package client.montecarlo.Resample;

import client.montecarlo.IMoveController;
import client.montecarlo.ParticleGenerator.IParticleGenerator;

import java.util.List;

public interface IResampler {

   List<IMoveController> resample(List<IMoveController> particles, double reuseGrade, IParticleGenerator generator);
}
