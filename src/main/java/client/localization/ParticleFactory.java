package client.localization;

import java.util.Random;

public class ParticleFactory {
   public static Particle createNewParticle(Map map) {

      return createValidInstance(map, false);
   }

   public static Particle createNewRobot(Map map) {

      return createValidInstance(map, true);
   }

   public static Particle createParticleClone(Particle p) throws CloneNotSupportedException {
      return new Particle(p);
   }


   private static Particle createValidInstance(Map map, boolean simulate) {
      Particle p = null;
      do {
         p = createInstance(map, simulate);
      } while (!p.hasValidPosition());

      return p;
   }

   private static Particle createInstance(Map map, boolean simulate) {
      Point particleCenter = map.getPointInPolygon();
      Random rand = new Random();
      double randRotation = rand.nextDouble() * Math.PI * 2;
      if (!simulate)
         return new Particle(map, particleCenter, randRotation);
      else
         return new RobotSimulator(map, particleCenter, randRotation);
   }
}
