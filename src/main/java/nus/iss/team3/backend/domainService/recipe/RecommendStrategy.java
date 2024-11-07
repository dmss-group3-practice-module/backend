package nus.iss.team3.backend.domainService.recipe;

import nus.iss.team3.backend.entity.Recipe;

import java.util.List;

public interface RecommendStrategy {
  List<Recipe> recommendRecipes(IRecipeService recipeService, boolean isDesc);
}
