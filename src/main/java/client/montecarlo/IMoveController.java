package client.montecarlo;

import client.montecarlo.ActionException;
import client.montecarlo.SensorDataSet;

public interface IMoveController {
    void moveForward ( int cm ) throws ActionException;
    void moveBackward ( int cm)throws ActionException;
    void turnLeft ( int angle )throws ActionException;
    void turnRight ( int angle )throws ActionException;
    SensorDataSet getSensorDataSet() throws ActionException;
}
