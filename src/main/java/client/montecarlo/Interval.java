package client.montecarlo;

import client.localization.Particle;

public class Interval implements Cloneable {
   private double start;
   private double end;
   private IMoveController particle;

   public Interval(double start, double end, IMoveController particle) {
      this.start = start;
      this.end = end;
      this.particle = particle;
   }

   public double getStart() {
      return this.start;
   }

   public double getEnd() {
      return this.end;
   }

   public boolean isInRange(double value) {
      if (this.start == .0d) {
         return value >= this.start && value <= this.end;
      }

      return value > this.start  && value <= this.end;
   }

   public IMoveController getParticle() {
      return this.particle;
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }
}
