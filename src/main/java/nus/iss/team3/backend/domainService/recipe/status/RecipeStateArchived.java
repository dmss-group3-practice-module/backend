package nus.iss.team3.backend.domainService.recipe.status;

import nus.iss.team3.backend.dataaccess.IRecipeDataAccess;
import nus.iss.team3.backend.entity.Recipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component(RecipeStateConfiguration.RECIPE_STATE_ARCHIVED)
public class RecipeStateArchived implements IRecipeState {
  private static final Logger logger = LogManager.getLogger(RecipeStateDraft.class);
  @Autowired private IRecipeDataAccess recipeDataAccess;

  /**
   * @param recipe
   * @return
   */
  @Override
  public Recipe addRecipe(Recipe recipe) {

    logger.info("Inside RecipeStateDraft.addRecipe!");
    if (recipe == null) {
      logger.error("Invalid recipe data");
      throw new IllegalArgumentException("Invalid recipe data");
    }
    if (recipe.getId() == null || recipe.getId() <= 0) {
      // insert new recipe
      // somehow straight away is insert a archived recipe....
      return recipeDataAccess.addRecipe(recipe);
    }
    Recipe dbRecipe = recipeDataAccess.getRecipeById(recipe.getId());
    if (dbRecipe == null) {
      return recipeDataAccess.addRecipe(recipe);
    }
    switch (dbRecipe.getStatus()) {
      case DRAFT -> {
        // as db is draft, might have another main record....
        Recipe dbMainRecipe = recipeDataAccess.getRecipeByDraftId(recipe.getId());
        if (dbMainRecipe == null) {
          // just update the db from draft to archive
          return recipeDataAccess.addRecipe(recipe);
        }
        if (dbMainRecipe.getStatus() == null) {
          logger.error(
              "ERROR with database data, please review recipe record : {}", dbRecipe.getId());
          return null;
        }
        switch (dbMainRecipe.getStatus()) {
          case PUBLISHED -> {
            // update the main record with this and remove the draft record
            recipeDataAccess.deleteRecipeById(recipe.getId());
            recipe.setId(dbMainRecipe.getId());
            recipeDataAccess.updateRecipe(recipe);
            return recipe;
          }
          default -> {
            // not possible to have draft or archived with another draft...
            logger.error(
                "ERROR with database data, please review recipe record : {} and {}",
                dbMainRecipe.getId(),
                recipe.getId());
          }
        }
      }
      case PUBLISHED, ARCHIVED -> {
        // update the record to archive with the latest info in the input item
        return recipeDataAccess.addRecipe(recipe);
      }
      case null -> {
        logger.error(
            "ERROR with database data, please review recipe record : {}", dbRecipe.getId());
      }
      default -> {
        logger.error(
            "ERROR with database data, please review recipe record : {}", dbRecipe.getId());
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

    logger.info("Inside RecipeStateArchived.updateRecipe!");
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
        // incoming is Archived, DB is draft OR published with Draft, replace into DB record, remove
        // incoming if any.
        Recipe dbMainRecipe = recipeDataAccess.getRecipeByDraftId(recipe.getId());

        if (dbMainRecipe != null) {
          recipe.setId(dbMainRecipe.getId());
          recipeDataAccess.deleteRecipeById(dbRecipe.getId());
        }
        return recipeDataAccess.updateRecipe(recipe);
      }
      case PUBLISHED -> {
        // incoming Archived, db is published, just remove... if got draft, delete...
        if (dbRecipe.getDraftRecipe() != null && dbRecipe.getDraftRecipe().getId() != null) {
          recipeDataAccess.deleteRecipeById(dbRecipe.getDraftRecipe().getId());
        }

        return recipeDataAccess.updateRecipe(recipe);
      }
      case ARCHIVED -> {
        logger.error("Unable to update recipe with ID: {} as it is archived", recipe.getId());
        return false;
      }
      case null -> {
        logger.error(
            "Invalid recipe status for recipe with ID: {}, unable to update", recipe.getId());
        return false;
      }
      default -> {
        logger.error(
            "Invalid recipe status for recipe with ID: {}, unable to update", recipe.getId());
        return false;
      }
    }
  }
}
