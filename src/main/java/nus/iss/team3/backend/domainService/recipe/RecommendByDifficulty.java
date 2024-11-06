package nus.iss.team3.backend.domainService.recipe;

import java.util.List;
import nus.iss.team3.backend.dataaccess.IRecipeDataAccess;
import nus.iss.team3.backend.entity.Recipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecommendByDifficulty implements RecommendStrategy {
  private static final Logger logger = LogManager.getLogger(RecipeService.class);
  private final IRecipeDataAccess recipeDataAccess;

  public RecommendByDifficulty(IRecipeDataAccess recipeDataAccess) {
    this.recipeDataAccess = recipeDataAccess;
  }

  @Override
  public List<Recipe> recommendRecipes(boolean isDesc) {
    logger.info("Getting all published recipes");
    // Get a list of all recipes order by difficult
    List<Recipe> recipes = recipeDataAccess.getAllPublishedRecipes();
    logger.info("Successfully retrieved {} published recipes ", recipes.size());
    return recipes;
  }
}
