package client.montecarlo;

import client.montecarlo.ActionException;
import client.montecarlo.SensorDataSet;

public interface IMoveController {
    void moveForward ( double cm ) throws ActionException;
    void moveBackward ( double cm)throws ActionException;
    void turnLeft ( double angle )throws ActionException;
    void turnRight ( double angle )throws ActionException;
    SensorDataSet getSensorDataSet() throws ActionException;

    double getBelief();
    void setBelief(double belief);
}
