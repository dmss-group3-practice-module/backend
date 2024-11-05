package nus.iss.team3.backend.domainService.recipe;

import nus.iss.team3.backend.dataaccess.IRecipeDataAccess;
import nus.iss.team3.backend.entity.Recipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class RecommendByRating implements RecommendStrategy {
    private static final Logger logger = LogManager.getLogger(RecipeService.class);
    private final IRecipeDataAccess recipeDataAccess;

    public RecommendByRating(IRecipeDataAccess recipeDataAccess) {
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
