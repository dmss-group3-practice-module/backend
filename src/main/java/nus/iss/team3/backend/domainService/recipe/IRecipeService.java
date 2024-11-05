package nus.iss.team3.backend.domainService.recipe;

import nus.iss.team3.backend.entity.Recipe;

import java.util.List;

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


}
