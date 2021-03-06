package client.montecarlo;

import client.localization.Intersect;
import client.localization.Point;
import client.montecarlo.ActionException;
import client.montecarlo.SensorDataSet;
import javafx.scene.paint.Color;

public interface IMoveController {
    void moveForward ( double cm ) throws ActionException;
    void moveBackward ( double cm)throws ActionException;
    void turnLeft ( double angle )throws ActionException;
    void turnRight ( double angle )throws ActionException;
    SensorDataSet getSensorDataSet() throws ActionException;

    Point getPoint();

    double getCurrentRotation();

    Color getColor();
    double getBelief();
    void setBelief(double belief);
    boolean isValid();

    Intersect getForwardIntersect();

    Intersect getLeftIntersect();

    Intersect getRightIntersect();
}
