package client.localization;

import client.montecarlo.ActionException;
import client.montecarlo.SensorDataSet;
import javafx.scene.paint.Color;

public class RobotSimulator extends Particle {

   public RobotSimulator(Map map, Point centerPoint, double rotation) {
      super(map, centerPoint, rotation);
      this.color = new Color(Color.GREEN.getRed(), Color.GREEN.getGreen(), Color.GREEN.getBlue(), 0.8);
   }

   /***
    * The simulated particle returns intersect distance in cm, the real robot in m. At this point it is converted to meters.
    * @return
    * @throws ActionException
    */
   @Override
   public SensorDataSet getSensorDataSet() throws ActionException {
      SensorDataSet sds = new SensorDataSet(forwardIntersect.distance , leftIntersect.distance , rightIntersect.distance);
      return sds;
   }
}
