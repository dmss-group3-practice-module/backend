package nus.iss.team3.backend.domainService.recipe;

import java.util.List;
import nus.iss.team3.backend.entity.Recipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecommendByRating implements RecommendStrategy {
  private static final Logger logger = LogManager.getLogger(RecommendByRating.class);

  @Override
  public List<Recipe> recommendRecipes(IRecipeService recipeService, int userId, boolean isDesc) {
    logger.info("Getting recommend recipes by rating");
    // Get a list of all recipes order by difficult
    List<Recipe> recipes = recipeService.getRecipesByRating(isDesc);
    logger.info("Successfully retrieved {} needed recipes ", recipes.size());
    return recipes;
  }
}
