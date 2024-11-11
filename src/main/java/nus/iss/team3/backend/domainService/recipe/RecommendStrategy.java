package nus.iss.team3.backend.domainService.recipe;

import java.util.List;
import nus.iss.team3.backend.entity.Recipe;

public interface RecommendStrategy {
  List<Recipe> recommendRecipes(IRecipeService recipeService, int userId, boolean isDesc);
}
