package nus.iss.team3.backend.domainService.recipe;

import java.util.List;
import nus.iss.team3.backend.entity.Recipe;

public class PreferenceCtx {
  private RecommendStrategy recommendStrategy;

  // set user's preference
  public void setRecommendStrategy(RecommendStrategy recommendStrategy) {
    this.recommendStrategy = recommendStrategy;
  }

  // apply user's preference
  public List<Recipe> recommend(boolean isDesc) {
    return recommendStrategy.recommendRecipes(isDesc);
  }
}
