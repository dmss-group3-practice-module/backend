package nus.iss.team3.backend.service;

import java.util.List;
import nus.iss.team3.backend.entity.Recipe;
import nus.iss.team3.backend.entity.RecipeWithReviews;
import org.springframework.stereotype.Service;

@Service
// TODO: Activate Spring Profile
// @Profile("!recipe")
public class RecipeWebCaller implements IRecipeService {

  @Override
  public boolean addRecipe(Recipe recipe) {
    throw new UnsupportedOperationException("Add operation is not supported in RecipeWebCaller.");
  }

  @Override
  public boolean updateRecipe(Recipe recipe) {
    throw new UnsupportedOperationException(
        "Update operation is not supported in RecipeWebCaller.");
  }

  @Override
  public boolean deleteRecipeById(Long recipeId) {
    throw new UnsupportedOperationException(
        "Delete operation is not supported in RecipeWebCaller.");
  }

  @Override
  public Recipe getRecipeById(Long recipeId) {
    return null;
  }

  @Override
  public List<Recipe> getAllRecipes() {
    return List.of();
  }

  // TODO: Get all published recipes.
  @Override
  public List<Recipe> getAllPublishedRecipes() {
    return List.of();
  }

  @Override
  public List<Recipe> getRecipesByName(String name) {
    return List.of();
  }

  // TODO: Get all recipes by creator ID.
  @Override
  public List<Recipe> getRecipesByCreatorId(int creatorId) {
    return List.of();
  }

  @Override
  public RecipeWithReviews getRecipeWithReviewsById(Long recipeId) {
    return null;
  }

  @Override
  public List<RecipeWithReviews> getAllRecipesWithReviews() {
    return List.of();
  }
}
