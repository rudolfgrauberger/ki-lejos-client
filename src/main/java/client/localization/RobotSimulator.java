package client.localization;

import client.montecarlo.ActionException;
import client.montecarlo.SensorDataSet;
import javafx.scene.paint.Color;

public class RobotSimulator extends Particle {

   public RobotSimulator(Map map, Point centerPoint, double rotation) {
      super(map, centerPoint, rotation);
   }

   /***
    * The simulated particle returns intersect distance in cm, the real robot in m. At this point it is converted to meters.
    * @return
    * @throws ActionException
    */
   @Override
   public SensorDataSet getSensorDataSet() throws ActionException {
      SensorDataSet sds = new SensorDataSet(forwardIntersect.distance / 100 , leftIntersect.distance / 100, rightIntersect.distance / 100);
      return sds;
   }

   @Override
   public Color getColor() {
      return Color.GREEN;
   }
}
