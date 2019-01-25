package client.localization.AbortCondition;

import client.montecarlo.IMoveController;
import sun.util.locale.provider.AvailableLanguageTags;

import java.util.List;

public class AverageWeightChecker implements IAbortConditionChecker {
   public final static double ABORT_WEIGHT = .93d;

   @Override
   public boolean abort(List<IMoveController> particles, IMoveController robot) {

      double sum = 0d;
      for (IMoveController p : particles){
         sum += p.getBelief();
      }

      System.out.println(sum / particles.size());
      return (sum / particles.size()) > ABORT_WEIGHT;
   }
}
