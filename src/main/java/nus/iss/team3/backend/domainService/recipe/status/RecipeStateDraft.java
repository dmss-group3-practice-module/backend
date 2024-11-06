package nus.iss.team3.backend.domainService.recipe.status;

import nus.iss.team3.backend.dataaccess.IRecipeDataAccess;
import nus.iss.team3.backend.entity.Recipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Component(RecipeStateConfiguration.RECIPE_STATE_DRAFT)
public class RecipeStateDraft implements IRecipeState {
  private static final Logger logger = LogManager.getLogger(RecipeStateDraft.class);
  @Autowired private IRecipeDataAccess recipeDataAccess;

  /**
   * @param recipe
   * @return
   */
  @Override
  @Transactional
  public Recipe addRecipe(Recipe recipe) {

    logger.info("Inside RecipeStateDraft.addRecipe!");
    if (recipe == null) {
      logger.error("Invalid recipe data");
      throw new IllegalArgumentException("Invalid recipe data");
    }
    if (recipe.getId() == null || recipe.getId() <= 0) {
      // insert new recipe
      return recipeDataAccess.addRecipe(recipe);
    }
    // input is not a new recipe, check the database status...
    Recipe dbRecipe = recipeDataAccess.getRecipeById(recipe.getId());
    if (dbRecipe == null) {
      // no recipe in db, let's insert it
      return recipeDataAccess.addRecipe(recipe);
    }
    switch (dbRecipe.getStatus()) {
      case DRAFT -> {
        // update the recipe
        // logic enable to update recipe even when doing the add fucntion.
        if (recipeDataAccess.updateRecipe(recipe)) {
          return recipe;
        }
      }
      case PUBLISHED -> {
        // update the recipe to include the draft as a draft to the published recipe.
        dbRecipe.setDraftRecipe(recipe);

        recipeDataAccess.updateRecipe(dbRecipe);
        if (recipeDataAccess.updateRecipe(recipe)) {
          return recipe;
        }
      }
      case ARCHIVED -> {
        logger.error("Unable to update recipe with ID: {} as it is archived", recipe.getId());
        return null;
      }
    }
    return null;
  }

  /**
   * @param recipe
   * @return
   */
  @Override
  public boolean updateRecipe(Recipe recipe) {

    logger.info("Inside RecipeStateDraft.updateRecipe!");
    if (recipe == null) {
      logger.error("Invalid recipe data");
      throw new IllegalArgumentException("Invalid recipe data");
    }
    if (recipe.getId() == null || recipe.getId() <= 0) {
      // no insert, update only
      logger.error("Invalid recipe data, no ID found, update failed");
      throw new IllegalArgumentException("Invalid recipe data");
    }
    // input is not a new recipe, check the database status...
    Recipe dbRecipe = recipeDataAccess.getRecipeById(recipe.getId());
    if (dbRecipe == null) {
      // no recipe in db, no insert allowed...
      logger.error("Recipe with ID {} not found for update", recipe.getId());
      throw new IllegalArgumentException("Recipe not found");
    }
    switch (dbRecipe.getStatus()) {
      case DRAFT -> {
        // update the recipe
        logger.info("db is DRAFT");
        return recipeDataAccess.updateRecipe(recipe);
      }
      case PUBLISHED -> {
        // update the recipe to include the draft as a draft to the published recipe.
        logger.info("db is PUBLISHED");
        dbRecipe.setDraftRecipe(recipe);

        recipe = recipeDataAccess.addRecipe(recipe);

        if (recipe.getId() > 0) {
          dbRecipe.setDraftRecipe(recipe);
          return recipeDataAccess.updateRecipe(dbRecipe);
        }
        return false;
      }
      case ARCHIVED -> {
        logger.error("Unable to update recipe with ID: {} as it is archived", recipe.getId());
        return false;
      }
    }
    return false;
  }
}
