package nus.iss.team3.backend.service;

import java.util.List;
import nus.iss.team3.backend.entity.Recipe;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!recipe")
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

  @Override
  public List<Recipe> getRecipesByName(String name) {
    return List.of();
  }
}
