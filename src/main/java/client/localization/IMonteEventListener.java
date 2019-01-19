package client.localization;

import client.montecarlo.IMoveController;

import java.util.List;

public interface IMonteEventListener {
    void onMonteDone(List<IMoveController> moveables);
}
