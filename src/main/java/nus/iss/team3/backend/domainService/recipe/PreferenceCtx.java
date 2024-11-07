package nus.iss.team3.backend.domainService.recipe;

import nus.iss.team3.backend.entity.Recipe;

import java.util.List;

public class PreferenceCtx {
  private RecommendStrategy recommendStrategy;

  // set user's preference
  public void setRecommendStrategy(RecommendStrategy recommendStrategy) {
    this.recommendStrategy = recommendStrategy;
  }

  // apply user's preference
  public List<Recipe> recommend(IRecipeService recipeService, boolean isDesc) {
    return recommendStrategy.recommendRecipes(recipeService, isDesc);
  }
}
