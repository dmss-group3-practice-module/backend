package nus.iss.team3.backend.dataaccess;

import nus.iss.team3.backend.entity.Recipe;

import java.util.List;

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
