package client.localization.AbortCondition;

import client.montecarlo.IMoveController;

import java.util.List;

public class XValueRangeChecker implements IAbortConditionChecker {

    private double xValueRange;

    public XValueRangeChecker(double deviationFromRobot) {
        this.xValueRange = deviationFromRobot;
    }

    @Override
    public boolean abort(List<IMoveController> particles, IMoveController robot) {

        double robotXValue = robot.getPoint().x;
        boolean inRange = true;

        for (IMoveController c : particles) {
            double particleXValue = c.getPoint().x;

            if (!(particleXValue >= (robotXValue - xValueRange) && particleXValue <= (robotXValue + xValueRange)))
                return false;
        }

        return true;
    }
}
