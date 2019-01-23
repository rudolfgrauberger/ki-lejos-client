package client.localization;

import client.montecarlo.ActionException;
import client.montecarlo.SensorDataSet;
import javafx.scene.paint.Color;

public class RobotSimulator extends Particle {

   public RobotSimulator(Map map, Point centerPoint, double rotation) {
      super(map, centerPoint, rotation);
      this.color = new Color(Color.GREEN.getRed(), Color.GREEN.getGreen(), Color.GREEN.getBlue(), 0.8);
      this.centerPoint.y = 75;

   }
}
