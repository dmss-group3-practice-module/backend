package nus.iss.team3.backend.domainService.recipe;

import java.util.List;
import nus.iss.team3.backend.entity.Recipe;

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

  List<Recipe> getRecipesByCreatorId(int creatorId);

  List<Recipe> getRecipesByDifficulty(boolean isDesc);

  List<Recipe> getRecipesByRating(boolean isDesc);

  List<Recipe> getRecipesByUserReview(int userId, boolean isDesc);

  boolean updateRecipeRating(Long recipeId, double rating);
}
