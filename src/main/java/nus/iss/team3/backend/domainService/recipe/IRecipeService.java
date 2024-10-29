package nus.iss.team3.backend.domainService.recipe;

import java.util.List;
import nus.iss.team3.backend.entity.Recipe;
import nus.iss.team3.backend.entity.RecipeWithReviews;

/**
 * Interface for RecipeService, contains logic involving recipe.
 *
 * @author Mao Weining
 */
public interface IRecipeService {

  boolean addRecipe(Recipe recipe);

  boolean updateRecipe(Recipe recipe);

  boolean deleteRecipeById(Long recipeId);

  Recipe getRecipeById(Long recipeId);

  List<Recipe> getAllRecipes();

  List<Recipe> getAllPublishedRecipes();

  List<Recipe> getRecipesByName(String name);

  List<Recipe> getRecipesByCreatorId(int creatorId);

  // Add new method to get recipe with its reviews.
  RecipeWithReviews getRecipeWithReviewsById(Long recipeId);

  List<RecipeWithReviews> getAllRecipesWithReviews();
}
