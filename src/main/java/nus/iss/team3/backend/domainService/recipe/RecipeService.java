package nus.iss.team3.backend.domainService.recipe;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import nus.iss.team3.backend.ProfileConfig;
import nus.iss.team3.backend.dataaccess.IRecipeDataAccess;
import nus.iss.team3.backend.domainService.recipe.status.IRecipeStateContext;
import nus.iss.team3.backend.entity.ERecipeStatus;
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
  private final IRecipeStateContext recipeStateContext;

  public RecipeService(IRecipeDataAccess recipeDataAccess, IRecipeStateContext recipeStateContext) {
    this.recipeDataAccess = recipeDataAccess;
    this.recipeStateContext = recipeStateContext;
  }

  @PostConstruct
  public void postConstruct() {
    logger.debug("Recipe Service Logic initialized.");
  }

  @Override
  public boolean addRecipe(Recipe recipe) {
    validateRecipe(recipe, false);
    logger.debug("Adding recipe: {}", recipe.getName());
    // Add recipes to the database using the Data Access Layer approach
    Recipe result = recipeStateContext.addRecipe(recipe);
    if (result != null && result.getId() != null) {
      logger.debug("Successfully added recipe with ID: {}", recipe.getId());
      return true;
    } else {
      logger.warn("Failed to add recipe: {}", recipe.getName());
    }
    return false;
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

    logger.debug("Updating recipe with ID: {}", recipe.getId());
    // Updating recipes in the database using a data access layer approach
    boolean result = recipeStateContext.updateRecipe(recipe);
    if (result) {
      logger.debug("Successfully updated recipe with ID: {}", recipe.getId());
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

    logger.debug("Deleting recipe with ID: {}", recipeId);
    // Delete a recipe with a specified ID using the data access layer method
    if (existingRecipe.getDraftRecipe() != null
        && existingRecipe.getDraftRecipe().getId() != null) {
      recipeDataAccess.deleteRecipeById(existingRecipe.getDraftRecipe().getId());
    }
    boolean result = recipeDataAccess.deleteRecipeById(recipeId);
    if (result) {
      logger.debug("Successfully deleted recipe with ID: {}", recipeId);
    } else {
      logger.warn("Failed to delete recipe with ID: {}", recipeId);
    }
    return result;
  }

  @Override
  public Recipe getRecipeById(Long recipeId) {
    // Check if the incoming recipe ID is null, if it is null then throw an exception
    Objects.requireNonNull(recipeId, "Recipe ID cannot be null");
    logger.debug("Getting recipe with ID: {}", recipeId);
    // Get the recipe with the specified ID using the method of the data access layer
    Recipe recipe = recipeDataAccess.getRecipeById(recipeId);

    if (recipe != null) {
      if (recipe.getDraftRecipe() != null && recipe.getDraftRecipe().getId() != null) {
        recipe.setDraftRecipe(recipeDataAccess.getRecipeById(recipe.getDraftRecipe().getId()));
      }
      logger.debug("Successfully retrieved recipe with ID: {}", recipeId);
    } else {
      logger.warn("Recipe with ID {} not found", recipeId);
    }
    return recipe;
  }

  @Override
  public List<Recipe> getAllRecipes() {
    logger.debug("Getting all recipes");
    // Get a list of all recipes using the data access layer method
    List<Recipe> recipes = recipeDataAccess.getAllRecipes();
    recipes = refineRecipeListByGroupingDraftInfo(recipes);
    logger.debug("Successfully retrieved {} recipes", recipes.size());
    return recipes;
  }

  /**
   * For display of recipes that are published, dont need know the draft recipes information.
   *
   * @return
   */
  @Override
  public List<Recipe> getAllPublishedRecipes() {
    logger.debug("Getting all published recipes");
    // Get a list of all recipes using the data access layer method
    List<Recipe> recipes = recipeDataAccess.getAllPublishedRecipes();
    logger.debug("Successfully retrieved {} published recipes ", recipes.size());
    return recipes;
  }

  @Override
  public List<Recipe> getRecipesByCreatorId(int creatorId) {
    // Check if the incoming recipe ID is null, if it is null then throw an exception
    logger.debug("Getting recipe for creator Id: {}", creatorId);
    // Get the recipe with the specified ID using the method of the data access layer
    List<Recipe> recipeList = recipeDataAccess.getRecipeByCreatorId(creatorId);
    recipeList = refineRecipeListByGroupingDraftInfo(recipeList);
    if (recipeList != null) {
      logger.debug("Successfully retrieved recipe under creator Id: {}", creatorId);
    } else {
      logger.warn("Recipe under creator Id {} not found", creatorId);
    }
    return recipeList;
  }

  @Override
  public List<Recipe> getRecipesByDifficulty(boolean isDesc) {
    return recipeDataAccess.getAllPublishedRecipesByDifficulty(isDesc);
  }

  @Override
  public List<Recipe> getRecipesByRating(boolean isDesc) {
    return recipeDataAccess.getAllPublishedRecipesByRating(isDesc);
  }

  @Override
  public List<Recipe> getRecipesByUserReview(int userId, boolean isDesc) {
    List<Recipe> recipeList = recipeDataAccess.getRecipeByCreatorId(userId);
    List<Recipe> sortedRecipes;

    if (recipeList == null || recipeList.isEmpty()) {
      return recipeDataAccess.getAllPublishedRecipesByDifficulty(isDesc);
    } else {
      Map<Long, Double> averageRatings =
          recipeList.stream()
              .collect(
                  Collectors.groupingBy(
                      Recipe::getId, Collectors.averagingDouble(Recipe::getRating)));

      sortedRecipes =
          recipeList.stream()
              .filter(recipe -> averageRatings.containsKey(recipe.getId()))
              .distinct()
              .sorted(
                  (r1, r2) -> {
                    Double rating1 = averageRatings.get(r1.getId());
                    Double rating2 = averageRatings.get(r2.getId());
                    return rating2.compareTo(rating1); // DESC
                  })
              .collect(Collectors.toList());

      logger.debug("get sorted recipeList: {}", sortedRecipes);

      List<Recipe> recipeListTmp = recipeDataAccess.getAllPublishedRecipesByDifficulty(isDesc);

      // use set to delete duplicate recipe
      Set<Long> existingIds = new HashSet<>();
      for (Recipe recipe : recipeList) {
        existingIds.add(recipe.getId());
      }

      // merge lists
      for (Recipe recipe : recipeListTmp) {
        if (recipe.getId() != null && !existingIds.contains(recipe.getId())) {
          sortedRecipes.add(recipe);
          existingIds.add(recipe.getId());
        }
      }
    }

    logger.debug("get merged recipeList: {}", sortedRecipes);

    return sortedRecipes;
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
      logger.debug("Successfully updated recipe rating with ID: {}", recipeId);
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
    if (recipe.getCookingTimeInMin() == null || recipe.getCookingTimeInMin() <= 0) {
      logger.error("Attempted to upsert a recipe with invalid cooking time");
      throw new IllegalArgumentException("Cooking time must be greater than 0");
    }
    if (recipe.getDifficultyLevel() == null
        || recipe.getDifficultyLevel() <= 0
        || recipe.getDifficultyLevel() > 5) {
      logger.error("Attempted to upsert a recipe with invalid difficulty level");
      throw new IllegalArgumentException(
          "Difficulty level must be greater than 0 and less than or equal to 5");
    }
    // Default rating to 0 if not set
    if (recipe.getRating() == null) {
      recipe.setRating(0.0);
    }
  }

  private List<Recipe> refineRecipeListByGroupingDraftInfo(List<Recipe> recipeList) {
    if (recipeList == null || recipeList.isEmpty()) {
      return recipeList;
    }
    List<Recipe> returnList = new ArrayList<>();
    Map<Long, Recipe> draftRecipeMap = new HashMap<>();

    for (Recipe temp : recipeList) {
      if (temp.getStatus() == ERecipeStatus.DRAFT) {
        draftRecipeMap.put(temp.getId(), temp);
      }
    }
    for (Recipe temp : recipeList) {
      if (temp.getStatus() != ERecipeStatus.DRAFT) {
        if (temp.getDraftRecipe() != null && temp.getDraftRecipe().getId() != null) {
          temp.setDraftRecipe(draftRecipeMap.getOrDefault(temp.getDraftRecipe().getId(), null));
          draftRecipeMap.remove(temp.getDraftRecipe().getId());
        }
        returnList.add(temp);
      }
    }
    draftRecipeMap.forEach((k, v) -> returnList.add(v));

    return returnList;
  }
}
