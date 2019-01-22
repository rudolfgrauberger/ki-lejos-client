package client.localization;

import javafx.scene.paint.Color;

public class RobotSimulator extends Particle {

   public RobotSimulator(Map map, Point centerPoint, double rotation) {
      super(map, centerPoint, rotation);
   }

   @Override
   public Color getColor() {
      return Color.GREEN;
   }
}
