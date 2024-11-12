package nus.iss.team3.backend.domainService.recipe;

import java.util.List;
import nus.iss.team3.backend.entity.Recipe;

public interface IRecipePreferenceContext {

  void setRecommendStrategy(RecommendStrategy recommendStrategy);

  // apply user's preference
  List<Recipe> recommend(IRecipeService recipeService, int userId, boolean isDesc);
}
