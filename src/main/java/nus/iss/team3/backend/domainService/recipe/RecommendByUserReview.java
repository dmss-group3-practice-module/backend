package nus.iss.team3.backend.domainService.recipe;

import java.util.List;
import nus.iss.team3.backend.entity.Recipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecommendByUserReview implements RecommendStrategy {
  private static final Logger logger = LogManager.getLogger(RecommendByUserReview.class);

  @Override
  public List<Recipe> recommendRecipes(IRecipeService recipeService, int userId, boolean isDesc) {
    logger.info("Getting recommend recipes by user's review");
    // Get a list of all recipes order by difficult
    List<Recipe> recipes = recipeService.getRecipesByUserReview(userId, isDesc);
    logger.info("Successfully retrieved {} needed recipes ", recipes.size());
    return recipes;
  }
}