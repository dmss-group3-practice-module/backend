package nus.iss.team3.backend.dataaccess;

import java.util.List;
import nus.iss.team3.backend.entity.Recipe;

/**
 * Interface class for RecipeDataAccess, should contain all functionality needed for recipe.
 *
 * @author Mao Weining
 */
public interface IRecipeDataAccess {

  Recipe addRecipe(Recipe recipe);

  boolean updateRecipe(Recipe recipe);

  boolean deleteRecipeById(Long recipeId);

  Recipe getRecipeById(Long recipeId);

  Recipe getRecipeByDraftId(Long draftRecipeId);

  List<Recipe> getAllRecipes();

  List<Recipe> getAllPublishedRecipes();

  List<Recipe> getRecipesByName(String name);

  List<Recipe> getRecipeByCreatorId(int creatorId);
}
