package nus.iss.team3.backend.service.recipe;

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

  List<Recipe> getRecipesByName(String name);

  List<Recipe> getRecipesByCreatorId(int creatorId);
}
