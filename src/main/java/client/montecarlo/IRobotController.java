package client.montecarlo;

public interface IRobotController {
    SensorDataSet getSensorDataSet() throws ActionException;
    void moveForward ( int cm )throws ActionException;
    void moveBackward ( int cm)throws ActionException;
    void turnLeft ( int angle )throws ActionException;
    void turnRight ( int angle )throws ActionException;
}
