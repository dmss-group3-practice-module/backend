package nus.iss.team3.backend.domainService.recipe;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import nus.iss.team3.backend.ProfileConfig;
import nus.iss.team3.backend.dataaccess.IRecipeDataAccess;
import nus.iss.team3.backend.entity.Recipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Service class with logic for handling recipe-related queries.
 *
 * @author Mao Weining
 */
@Service
@Profile(ProfileConfig.PROFILE_RECIPE)
public class RecipeService implements IRecipeService {

  private static final Logger logger = LogManager.getLogger(RecipeService.class);
  private final IRecipeDataAccess recipeDataAccess;

  public RecipeService(IRecipeDataAccess recipeDataAccess) {
    this.recipeDataAccess = recipeDataAccess;
  }

  @PostConstruct
  public void postConstruct() {
    logger.info("Recipe Service Logic initialized.");
  }

  @Override
  public boolean addRecipe(Recipe recipe) {
    validateRecipe(recipe, false);
    logger.info("Adding recipe: {}", recipe.getName());
    // Add recipes to the database using the Data Access Layer approach
    boolean result = recipeDataAccess.addRecipe(recipe);
    if (result) {
      logger.info("Successfully added recipe with ID: {}", recipe.getId());
    } else {
      logger.warn("Failed to add recipe: {}", recipe.getName());
    }
    return result;
  }

  @Override
  public boolean updateRecipe(Recipe recipe) {
    validateRecipe(recipe, true);

    Recipe existingRecipe = recipeDataAccess.getRecipeById(recipe.getId());
    if (existingRecipe == null) {
      logger.error("Recipe with ID {} not found for update", recipe.getId());
      throw new IllegalArgumentException("Recipe not found");
    }

    // Compare the existing recipe with the new recipe (excluding time-related properties)
    if (existingRecipe.equals(recipe)) {
      logger.warn("No changes detected for recipe with ID: {}", recipe.getId());
      return false; // No changes to update
    }

    logger.info("Updating recipe with ID: {}", recipe.getId());
    // Updating recipes in the database using a data access layer approach
    boolean result = recipeDataAccess.updateRecipe(recipe);
    if (result) {
      logger.info("Successfully updated recipe with ID: {}", recipe.getId());
    } else {
      logger.warn("Failed to update recipe with ID: {}", recipe.getId());
    }
    return result;
  }

  @Override
  public boolean deleteRecipeById(Long recipeId) {
    Objects.requireNonNull(recipeId, "Recipe ID cannot be null");

    // Fetch the existing recipe from the database
    Recipe existingRecipe = recipeDataAccess.getRecipeById(recipeId);
    if (existingRecipe == null) {
      logger.error("Recipe with ID {} not found for delete", recipeId);
      throw new IllegalArgumentException("Recipe not found");
    }

    logger.info("Deleting recipe with ID: {}", recipeId);
    // Delete a recipe with a specified ID using the data access layer method
    boolean result = recipeDataAccess.deleteRecipeById(recipeId);
    if (result) {
      logger.info("Successfully deleted recipe with ID: {}", recipeId);
    } else {
      logger.warn("Failed to delete recipe with ID: {}", recipeId);
    }
    return result;
  }

  @Override
  public Recipe getRecipeById(Long recipeId) {
    // Check if the incoming recipe ID is null, if it is null then throw an exception
    Objects.requireNonNull(recipeId, "Recipe ID cannot be null");
    logger.info("Getting recipe with ID: {}", recipeId);
    // Get the recipe with the specified ID using the method of the data access layer
    Recipe recipe = recipeDataAccess.getRecipeById(recipeId);
    if (recipe != null) {
      logger.info("Successfully retrieved recipe with ID: {}", recipeId);
    } else {
      logger.warn("Recipe with ID {} not found", recipeId);
    }
    return recipe;
  }

  @Override
  public List<Recipe> getAllRecipes() {
    logger.info("Getting all recipes");
    // Get a list of all recipes using the data access layer method
    List<Recipe> recipes = recipeDataAccess.getAllRecipes();
    logger.info("Successfully retrieved {} recipes", recipes.size());
    return recipes;
  }

  @Override
  public List<Recipe> getAllPublishedRecipes() {
    logger.info("Getting all published recipes");
    // Get a list of all recipes using the data access layer method
    List<Recipe> recipes = recipeDataAccess.getAllPublishedRecipes();
    logger.info("Successfully retrieved {} published recipes ", recipes.size());
    return recipes;
  }

  @Override
  public List<Recipe> getRecipesByName(String name) {
    // Checks if the name passed in is null or empty, if so returns the empty list
    if (name == null || name.trim().isEmpty()) {
      logger.warn("Search name is empty or null, returning empty list");
      return List.of();
    }
    logger.info("Searching recipes by name: {}", name);
    // Get recipes by name using data access layer methods
    List<Recipe> recipes = recipeDataAccess.getRecipesByName(name);
    logger.info("Found {} recipes with name {}", recipes.size(), name);
    return recipes;
  }

  @Override
  public List<Recipe> getRecipesByCreatorId(int creatorId) {
    // Check if the incoming recipe ID is null, if it is null then throw an exception
    logger.info("Getting recipe for creator Id: {}", creatorId);
    // Get the recipe with the specified ID using the method of the data access layer
    List<Recipe> recipeList = recipeDataAccess.getRecipeByCreatorId(creatorId);
    if (recipeList != null) {
      logger.info("Successfully retrieved recipe under creator Id: {}", creatorId);
    } else {
      logger.warn("Recipe under creator Id {} not found", creatorId);
    }
    return recipeList;
  }

  /**
   * @param recipeId
   * @param rating
   * @return
   */
  @Override
  public boolean updateRecipeRating(Long recipeId, double rating) {
    boolean result = recipeDataAccess.updateRecipeRating(recipeId, rating);
    if (result) {
      logger.info("Successfully updated recipe rating with ID: {}", recipeId);
    } else {
      logger.warn("Failed to update recipe rating with ID: {}", recipeId);
    }
    return result;
  }

  // Helper method: Validate the recipe
  private void validateRecipe(Recipe recipe, boolean isUpdate) {
    if (recipe == null) {
      logger.error("Attempted to {} a null recipe", isUpdate ? "update" : "add");
      throw new IllegalArgumentException("Recipe cannot be null");
    }
    if (isUpdate && recipe.getId() == null) {
      logger.error("Attempted to update a recipe without an ID");
      throw new IllegalArgumentException("Recipe ID cannot be null");
    }
    if (recipe.getName() == null || recipe.getName().trim().isEmpty()) {
      logger.error("Attempted to {} a recipe with an empty name", isUpdate ? "update" : "add");
      throw new IllegalArgumentException("Recipe name cannot be empty");
    }
    if (recipe.getCookingTimeInSec() == null || recipe.getCookingTimeInSec() <= 0) {
      logger.error(
          "Attempted to {} a recipe with invalid cooking time", isUpdate ? "update" : "add");
      throw new IllegalArgumentException("Cooking time must be greater than 0");
    }
    if (recipe.getDifficultyLevel() == null
        || recipe.getDifficultyLevel() <= 0
        || recipe.getDifficultyLevel() > 5) {
      logger.error(
          "Attempted to {} a recipe with invalid difficulty level", isUpdate ? "update" : "add");
      throw new IllegalArgumentException(
          "Difficulty level must be greater than 0 and less than or equal to 5");
    }
    // Default rating to 0 if not set
    if (recipe.getRating() == null) {
      recipe.setRating(0.0);
    }
  }
}
