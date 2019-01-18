package client.montecarlo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RouletteWheelResampler implements IResampler {

   private double weightSum = .0d;
   private List<Interval> wheel;
   private Random r;

   @Override
   public List<IMoveController> resample(List<IMoveController> particles, int reuse) {
      r = new Random();

      for (int i = 0; i < particles.size(); ++i) {
         System.out.println(particles.get(i).getBelief());
      }

      wheel = getIntervalsFromParticles(particles);

      List<IMoveController> resampled = new ArrayList<IMoveController>();

      while (reuse > 0) {
         double z = getRandomDouble();

         resampled.add(getParticleFromRange(z));

         --reuse;
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
