package client.localization;

import client.montecarlo.ActionException;
import client.montecarlo.SensorDataSet;

public interface IMoveController {
    void moveForward ( int cm ) throws ActionException;
    void moveBackward ( int cm);
    void turnLeft ( double angle ) throws ActionException;
    void turnRight ( double angle ) throws ActionException;
    SensorDataSet getSensorDataSet() throws ActionException;
}
