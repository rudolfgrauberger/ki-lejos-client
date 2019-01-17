package client.localization;

import client.montecarlo.ActionException;
import client.montecarlo.SensorDataSet;

public interface IMoveController {
    void moveForward ( int cm );
    void moveBackward ( int cm);
    void turnLeft ( double angle );
    void turnRight ( double angle );
    SensorDataSet getSensorDataSet() throws ActionException;
}
