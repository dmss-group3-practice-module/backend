package nus.iss.team3.backend.domainService.recipe.status;

import nus.iss.team3.backend.dataaccess.IRecipeDataAccess;
import nus.iss.team3.backend.entity.Recipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component(RecipeStateConfiguration.RECIPE_STATE_PUBLISHED)
public class RecipeStatePublished implements IRecipeState {
  private static final Logger logger = LogManager.getLogger(RecipeStateDraft.class);
  @Autowired private IRecipeDataAccess recipeDataAccess;

  /**
   * @param recipe
   * @return
   */
  @Override
  public Recipe addRecipe(Recipe recipe) {
    logger.info("Inside RecipeStatePublished.addRecipe!");
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
        // incoming actual, in db  draft, just update
        Recipe dbMainRecipe = recipeDataAccess.getRecipeByDraftId(recipe.getId());
        if (dbMainRecipe == null) {
          // update draft!
          if (recipeDataAccess.updateRecipe(recipe)) {
            return recipe;
          }
        }
        switch (dbMainRecipe.getStatus()) {
          case DRAFT, ARCHIVED -> {
            // not possible for a draft to be under another recipe that is draft or archived.
            logger.error(
                "ERROR with database data, please review recipe record : {} and {}",
                dbMainRecipe.getId(),
                recipe.getId());
          }
          case PUBLISHED -> {

            // update the main record with this and remove the draft record
            recipeDataAccess.deleteRecipeById(recipe.getId());
            recipe.setId(dbMainRecipe.getId());
            if (recipeDataAccess.updateRecipe(recipe)) {
              return recipe;
            }
          }
        }
      }
      case PUBLISHED, ARCHIVED -> {
        // a published record shouldnt have another main, thus, just update this....
        if (recipeDataAccess.updateRecipe(recipe)) {
          return recipe;
        }
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

    logger.info("Inside RecipeStatePublished.updateRecipe!");
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
        // incoming is Published, DB is draft, replace into DB record, remove incoming if any.
        Recipe dbMainRecipe = recipeDataAccess.getRecipeByDraftId(recipe.getId());

        if (dbMainRecipe != null) {
          recipe.setId(dbMainRecipe.getId());
          recipeDataAccess.deleteRecipeById(dbRecipe.getId());
        }
        return recipeDataAccess.updateRecipe(recipe);
      }
      case PUBLISHED -> {
        // both published, just remove... if got draft, delete...
        if (dbRecipe.getDraftRecipe() != null && dbRecipe.getDraftRecipe().getId() != null) {
          recipeDataAccess.deleteRecipeById(dbRecipe.getDraftRecipe().getId());
        }

        return recipeDataAccess.updateRecipe(recipe);
      }
      case ARCHIVED -> {
        logger.error("Unable to update recipe with ID: {} as it is archived", recipe.getId());
        return false;
      }
    }
    return false;
  }
}
