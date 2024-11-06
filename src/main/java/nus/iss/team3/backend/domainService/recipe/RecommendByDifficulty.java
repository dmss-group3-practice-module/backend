package nus.iss.team3.backend.domainService.recipe;

import nus.iss.team3.backend.entity.Recipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class RecommendByDifficulty implements RecommendStrategy {
  private static final Logger logger = LogManager.getLogger(RecommendByDifficulty.class);

  @Override
  public List<Recipe> recommendRecipes(IRecipeService recipeService, boolean isDesc) {
    logger.info("Getting recommend recipes by difficulty");
    // Get a list of all recipes order by difficult
    List<Recipe> recipes = recipeService.getRecipesByDifficulty(isDesc);
    logger.info("Successfully retrieved {} published recipes ", recipes.size());
    return recipes;
  }
}
