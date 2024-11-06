package nus.iss.team3.backend.domainService.recipe.status;

import jakarta.annotation.PostConstruct;
import nus.iss.team3.backend.dataaccess.IRecipeDataAccess;
import nus.iss.team3.backend.entity.ERecipeStatus;
import nus.iss.team3.backend.entity.Recipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/** Service class with logic for handling recipe status related queries. @Author Desmond Tan */
@Service
public class RecipeStateContext implements IRecipeStateContext {

  private static final Logger logger = LogManager.getLogger(RecipeStateContext.class);
  @Autowired private IRecipeDataAccess recipeDataAccess;

  @Qualifier(RecipeStateConfiguration.RECIPE_STATE_DRAFT)
  @Autowired
  private IRecipeState recipeStateDraft;

  @Qualifier(RecipeStateConfiguration.RECIPE_STATE_PUBLISHED)
  @Autowired
  private IRecipeState recipeStatePublished;

  @Qualifier(RecipeStateConfiguration.RECIPE_STATE_ARCHIVED)
  @Autowired
  private IRecipeState recipeStateArchived;

  // rather than 1 state for every recipe, the state is based on the input recipe and is different
  // for each function

  @PostConstruct
  public void postConstruct() {
    logger.info("Recipe State Context Logic initialized.");
  }

  @Override
  public Recipe addRecipe(Recipe recipe) {
    if (recipe == null) {
      logger.error("Invalid recipe data");
      throw new IllegalArgumentException("Invalid recipe data");
    }
    if (recipe.getStatus() == null) {
      logger.error("Invalid recipe status: {}", recipe.getStatus());
      throw new IllegalArgumentException("Invalid recipe status");
    }

    IRecipeState recipeState = getRecipeState(recipe.getStatus());
    if (recipeState == null) {
      logger.error("Invalid recipe state: {}", recipe.getStatus());
      throw new IllegalArgumentException("Invalid recipe state");
    }
    return recipeState.addRecipe(recipe);
  }

  /**
   * @param recipe
   * @return
   */
  @Override
  public boolean updateRecipe(Recipe recipe) {
    if (recipe == null) {
      logger.error("Invalid recipe data");
      throw new IllegalArgumentException("Invalid recipe data");
    }
    if (recipe.getStatus() == null) {
      logger.error("Invalid recipe status: {}", recipe.getStatus());
      throw new IllegalArgumentException("Invalid recipe status");
    }

    IRecipeState recipeState = getRecipeState(recipe.getStatus());
    if (recipeState == null) {
      logger.error("Invalid recipe state: {}", recipe.getStatus());
      throw new IllegalArgumentException("Invalid recipe state");
    }
    return recipeState.updateRecipe(recipe);
  }

  private IRecipeState getRecipeState(ERecipeStatus input) {
    return switch (input) {
      case DRAFT -> recipeStateDraft;
      case PUBLISHED -> recipeStatePublished;
      case ARCHIVED -> recipeStateArchived;
      default -> null;
    };
  }
}
