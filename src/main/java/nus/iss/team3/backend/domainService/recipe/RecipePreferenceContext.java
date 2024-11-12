package nus.iss.team3.backend.domainService.recipe;

import java.util.List;
import nus.iss.team3.backend.entity.Recipe;
import org.springframework.stereotype.Service;

@Service
public class RecipePreferenceContext implements IRecipePreferenceContext {
  private RecommendStrategy recommendStrategy;

  // set user's preference
  @Override
  public void setRecommendStrategy(RecommendStrategy recommendStrategy) {
    this.recommendStrategy = recommendStrategy;
  }

  // apply user's preference
  @Override
  public List<Recipe> recommend(IRecipeService recipeService, int userId, boolean isDesc) {
    return recommendStrategy.recommendRecipes(recipeService, userId, isDesc);
  }
}
