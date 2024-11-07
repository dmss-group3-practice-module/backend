package nus.iss.team3.backend.domainService.recipe.status;

import nus.iss.team3.backend.entity.Recipe;

public interface IRecipeState {

  Recipe addRecipe(Recipe recipe);

  boolean updateRecipe(Recipe recipe);
}
