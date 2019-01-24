package client.localization.AbortCondition;

import client.montecarlo.IMoveController;

import java.util.List;

public interface IAbortConditionChecker {
    boolean abort(List<IMoveController> particles);
}
