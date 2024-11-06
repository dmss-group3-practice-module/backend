package nus.iss.team3.backend.dataaccess;

import java.util.List;
import nus.iss.team3.backend.entity.Recipe;

/**
 * Interface class for RecipeDataAccess, should contain all functionality needed for recipe.
 *
 * @author Mao Weining
 */
public interface IRecipeDataAccess {

  boolean addRecipe(Recipe recipe);

  boolean updateRecipe(Recipe recipe);

  boolean deleteRecipeById(Long recipeId);

  Recipe getRecipeById(Long recipeId);

  List<Recipe> getAllRecipes();

  List<Recipe> getAllPublishedRecipes();

  List<Recipe> getRecipesByName(String name);

  List<Recipe> getRecipeByCreatorId(int creatorId);

  List<Recipe> getAllPublishedRecipesByDifficulty();

  List<Recipe> getAllPublishedRecipesByRating();
}
