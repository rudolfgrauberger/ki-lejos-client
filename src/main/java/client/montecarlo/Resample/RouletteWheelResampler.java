package client.montecarlo.Resample;

import client.montecarlo.IMoveController;
import client.montecarlo.IParticleGenerator;
import client.montecarlo.Interval;
import client.montecarlo.Resample.IResampler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RouletteWheelResampler implements IResampler {

   private double weightSum = .0d;
   private List<Interval> wheel;
   private Random r;

   @Override
   public List<IMoveController> resample(List<IMoveController> particles, double reuseGrade, IParticleGenerator generator) {
      r = new Random();

      int reuseCount = (int)Math.ceil(particles.size() * reuseGrade);
      int renewCount = particles.size() - reuseCount;


      wheel = getIntervalsFromParticles(particles);

      List<IMoveController> resampled = new ArrayList<IMoveController>();

      while (reuseCount > 0) {
         double z = getRandomDouble();

         IMoveController p = getParticleFromRange(z);
         resampled.add(p);

         reuseCount--;
      }

      System.out.println("Renewed count: " + renewCount);

      while (renewCount > 0) {
         resampled.add(generator.getRandomParticle());
         --renewCount;
      }

      System.out.println("Resampled count: " + resampled.size());

      return resampled;
   }

   private IMoveController getParticleFromRange(double z) {
      for (int i = 0; i < wheel.size(); ++i)
      {
         Interval interval = wheel.get(i);
         if (interval.isInRange(z)) {
            return interval.getParticle();
         }
      }

      return null;
   }

   private List<Interval> getIntervalsFromParticles(List<IMoveController> particles) {
      List<Interval> wheel = new ArrayList<Interval>();
      double dLatestLimit = .0f;

      for (int i = 0; i < particles.size(); ++i) {
         double weight = particles.get(i).getBelief();
         Interval interval = new Interval(dLatestLimit, weight + dLatestLimit, particles.get(i));
         wheel.add(interval);
         dLatestLimit += weight;
      }

      weightSum = dLatestLimit;

      return wheel;
   }

   private double getRandomDouble() {
      return weightSum * r.nextDouble();
   }
}
