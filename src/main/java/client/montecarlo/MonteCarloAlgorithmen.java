package client.montecarlo;

import java.util.ArrayList;
import java.util.Random;

public class MonteCarloAlgorithmen {

    private IMoveController roboter;
    private ArrayList<IMoveController> partikels;
    private SensorDataSet latestRoboterDataSet;
    private boolean looksInDriveDirection;

    public MonteCarloAlgorithmen(IMoveController roboter) {
        this.roboter = roboter;
        looksInDriveDirection = true;
    }

    public ArrayList<IMoveController> run (ArrayList<IMoveController> partikels) throws ActionException{
        this.partikels = partikels;

        compareSensorDatas();
        moveCommand();
        compareSensorDatas();
        removePartikels();
        addPartikels();

        return  this.partikels;
    }

    private void moveCommand() throws ActionException{
        Random random = new Random();
        int commandNumber = random.nextInt(4);
        switch (commandNumber){
            //case forward
            case 0:
                moveForward();
                break;
            //turn left
            case 1:
                roboter.turnLeft(90);
                for (IMoveController partikel: partikels) {
                    partikel.turnLeft(90);
                }
                looksInDriveDirection = !looksInDriveDirection;
                break;
            //turn right
            case 2:
                roboter.turnRight(90);
                for (IMoveController partikel: partikels) {
                    partikel.turnRight(90);
                }
                looksInDriveDirection = !looksInDriveDirection;
                break;
            //turn around
            case 3:
                roboter.turnRight(180);
                for (IMoveController partikel: partikels) {
                    partikel.turnRight(180);
                }
                break;
        }
    }
    private void compareSensorDatas() throws ActionException{
        this.latestRoboterDataSet = roboter.getSensorDataSet();

        for (IMoveController partikel: partikels) {
            SensorDataSet partikelDataSet = partikel.getSensorDataSet();
            //compare and set new Belife
        }
    }
    private void removePartikels(){

    }
    private void addPartikels(){

    }
    //move controlles
    private void moveForward() throws ActionException{
        Random random = new Random();
        //turn to forward direction
        if(!looksInDriveDirection){
            if( random.nextInt(2) > 0) {
                roboter.turnLeft(90);
                for (IMoveController partikel: partikels) {
                    partikel.turnLeft(90);
                }
            }
            else {
                roboter.turnRight(90);
                for (IMoveController partikel: partikels) {
                    partikel.turnRight(90);
                }
            }
            looksInDriveDirection = true;
        }
        //get distance
        int distance = 190;
        if(latestRoboterDataSet.getDistanceFront() < distance)
            distance = (int)latestRoboterDataSet.getDistanceFront() - 10;
        //move
        roboter.moveForward(distance);
        for (IMoveController partikel: partikels) {
            partikel.moveForward(distance);
        }
    }
}
