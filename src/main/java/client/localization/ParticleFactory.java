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
      Particle tmp = createInstance(p.map, false);
      tmp.centerPoint.y = p.centerPoint.y;
      tmp.centerPoint.x = p.centerPoint.x;
      tmp.currentRotation = p.currentRotation;
      tmp.forwardIntersect = new Intersect(p.forwardIntersect.point, p.forwardIntersect.distance);
      tmp.rightIntersect = new Intersect(p.rightIntersect.point, p.rightIntersect.distance);
      tmp.leftIntersect = new Intersect(p.leftIntersect.point, p.leftIntersect.distance);
      tmp.belief = p.belief;
      tmp.isValid = p.isValid;
      tmp.color = p.color;
      tmp.r = p.r;

      return tmp;
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
