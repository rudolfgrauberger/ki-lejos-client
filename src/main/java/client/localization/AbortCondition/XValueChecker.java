package client.localization.AbortCondition;

import client.montecarlo.IMoveController;

import java.util.List;

public class XValueChecker implements IAbortConditionChecker {

    @Override
    public boolean abort(List<IMoveController> particles, IMoveController robot) {
        return false;
    }
}
