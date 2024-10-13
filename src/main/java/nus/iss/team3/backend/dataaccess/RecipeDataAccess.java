package nus.iss.team3.backend.dataaccess;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nus.iss.team3.backend.dataaccess.postgres.PostgresDataAccess;
import nus.iss.team3.backend.entity.CookingStep;
import nus.iss.team3.backend.entity.ERecipeStatus;
import nus.iss.team3.backend.entity.Ingredient;
import nus.iss.team3.backend.entity.Recipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository class to connect to postgres for recipe data.
 *
 * @author Mao Weining
 */
@Repository // Indicates that this is a repository component
public class RecipeDataAccess implements IRecipeDataAccess {

  private static final Logger logger = LogManager.getLogger(RecipeDataAccess.class);
  private final PostgresDataAccess postgresDataAccess;

  // Constructor for dependency injection of PostgresDataAccess
  public RecipeDataAccess(PostgresDataAccess postgresDataAccess) {
    this.postgresDataAccess = postgresDataAccess;
  }

  @Override
  @Transactional // Ensures the entire method is executed in a single transaction
  public boolean addRecipe(Recipe recipe) {
    logger.info("Starting to add recipe: {}", recipe.getName());
    try {
      // Validate the recipe
      validateRecipe(recipe);
      logger.debug("Adding recipe: Recipe validation passed");

      // Build a map of recipe parameters
      Map<String, Object> recipeParams = buildRecipeParams(recipe);

      // Execute the insert statement
      List<Map<String, Object>> result =
          postgresDataAccess.queryStatement(PostgresSqlStatement.SQL_RECIPE_ADD, recipeParams);

      if (result.isEmpty()) {
        logger.warn("Failed to insert recipe, no generated ID returned");
        return false; // Return false if insertion fails
      }

      // Get the generated recipe ID and set it to the recipe object
      Long recipeId =
          ((Number) result.getFirst().get(PostgresSqlStatement.COLUMN_RECIPE_ID)).longValue();
      recipe.setId(recipeId);
      logger.debug("Recipe inserted successfully, generated ID: {}", recipeId);

      // Before inserting ingredients, check if the collection is empty to avoid unnecessary
      // database operations
      if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
        insertIngredients(recipe);
        logger.debug("Ingredients inserted successfully");
      } else {
        logger.debug("No ingredients to insert for the recipe");
      }

      // Before inserting cooking steps, check if the collection is empty to avoid unnecessary
      // database operations
      if (recipe.getCookingSteps() != null && !recipe.getCookingSteps().isEmpty()) {
        insertCookingSteps(recipe);
        logger.debug("Cooking steps inserted successfully");
      } else {
        logger.debug("No cooking steps to insert for the recipe");
      }

      logger.info("Recipe addition completed: ID={}", recipeId);
      return true;
    } catch (Exception e) {
      logger.error("Exception occurred while adding recipe: {}", e.getMessage(), e);
      throw e; // Rethrow the exception to trigger transaction rollback
    }
  }

  @Override
  @Transactional // Ensures the entire method is executed in a single transaction
  public boolean updateRecipe(Recipe recipe) {
    logger.info("Starting to update recipe: ID={}", recipe.getId());
    try {
      // Validate the recipe
      validateRecipe(recipe);
      logger.debug("Updating recipe: Recipe validation passed");

      // Build the update SQL statement and parameters
      Map<String, Object> recipeParams = buildRecipeParams(recipe);
      recipeParams.put(PostgresSqlStatement.COLUMN_RECIPE_ID, recipe.getId());

      // Execute the update statement
      int updatedRows =
          postgresDataAccess.upsertStatement(PostgresSqlStatement.SQL_RECIPE_UPDATE, recipeParams);

      if (updatedRows == 0) {
        logger.warn("No recipe found with ID={} for update", recipe.getId());
        return false; // Return false if no rows were updated
      }
      logger.debug("Recipe updated successfully, number of updated rows: {}", updatedRows);

      // Update ingredients: first delete existing ingredients, then insert new ones
      deleteIngredients(recipe.getId());
      insertIngredients(recipe);
      logger.debug("Ingredients updated successfully");

      // Update cooking steps: first delete existing steps, then insert new ones
      deleteCookingSteps(recipe.getId());
      insertCookingSteps(recipe);
      logger.debug("Cooking steps updated successfully");

      logger.info("Recipe update completed: ID={}", recipe.getId());
      return true;
    } catch (Exception e) {
      logger.error("Exception occurred while updating recipe: {}", e.getMessage(), e);
      throw e; // Rethrow the exception to trigger transaction rollback
    }
  }

  @Override
  @Transactional // Ensures the entire method is executed in a single transaction
  public boolean deleteRecipeById(Long recipeId) {
    logger.info("Starting to delete recipe: ID={}", recipeId);
    try {
      // Delete related ingredients
      deleteIngredients(recipeId);
      logger.debug("Ingredients deleted successfully");

      // Delete related cooking steps
      deleteCookingSteps(recipeId);
      logger.debug("Cooking steps deleted successfully");

      // Execute the delete statement
      int deletedRows =
          postgresDataAccess.upsertStatement(
              PostgresSqlStatement.SQL_RECIPE_DELETE_BY_ID,
              Collections.singletonMap(PostgresSqlStatement.INPUT_RECIPE_ID, recipeId));

      if (deletedRows > 0) {
        logger.info("Recipe deleted successfully: ID={}", recipeId);
        return true;
      } else {
        logger.warn("No recipe found with ID={} for deletion", recipeId);
        return false;
      }
    } catch (Exception e) {
      logger.error("Exception occurred while deleting recipe: {}", e.getMessage(), e);
      throw e; // Rethrow the exception to trigger transaction rollback
    }
  }

  @Override
  public Recipe getRecipeById(Long recipeId) {
    logger.info("Querying recipe: ID={}", recipeId);
    try {
      // Execute the query
      List<Map<String, Object>> result =
          postgresDataAccess.queryStatement(
              PostgresSqlStatement.SQL_RECIPE_GET_BY_ID,
              Collections.singletonMap(PostgresSqlStatement.INPUT_RECIPE_ID, recipeId));

      if (result.isEmpty()) {
        logger.warn("No recipe found with ID={}", recipeId);
        return null; // Return null if no recipe is found
      }

      // Map the query result to a Recipe object
      Recipe recipe = mapToRecipe(result.getFirst());
      logger.debug("Recipe mapping successful: ID={}", recipeId);

      // Query and set the recipe's ingredients
      recipe.setIngredients(getIngredientsForRecipe(recipeId));
      logger.debug("Ingredients loaded successfully: Recipe ID={}", recipeId);

      // Query and set the recipe's cooking steps
      recipe.setCookingSteps(getCookingStepsForRecipe(recipeId));
      logger.debug("Cooking steps loaded successfully: Recipe ID={}", recipeId);

      logger.info("Recipe query completed: ID={}", recipeId);
      return recipe;
    } catch (Exception e) {
      logger.error("Exception occurred while querying recipe: {}", e.getMessage(), e);
      throw e;
    }
  }

  @Override
  public List<Recipe> getAllRecipes() {
    logger.info("Querying all recipes");
    try {
      // Execute the query
      List<Map<String, Object>> result =
          postgresDataAccess.queryStatement(PostgresSqlStatement.SQL_RECIPE_GET_ALL, null);

      List<Recipe> recipes = new ArrayList<>();
      for (Map<String, Object> row : result) {
        // Map each row to a Recipe object
        Recipe recipe = mapToRecipe(row);
        // Query and set the recipe's ingredients
        recipe.setIngredients(getIngredientsForRecipe(recipe.getId()));
        // Query and set the recipe's cooking steps
        recipe.setCookingSteps(getCookingStepsForRecipe(recipe.getId()));
        recipes.add(recipe); // Add to the recipe list
      }

      logger.info("Querying all recipes completed, count: {}", recipes.size());
      return recipes;
    } catch (Exception e) {
      logger.error("Exception occurred while querying all recipes: {}", e.getMessage(), e);
      throw e;
    }
  }

  @Override
  public List<Recipe> getRecipesByName(String name) {
    logger.info("Querying recipes by name: Name contains '{}'", name);
    try {
      // Execute the query: use wildcard for fuzzy query
      List<Map<String, Object>> result =
          postgresDataAccess.queryStatement(
              PostgresSqlStatement.SQL_RECIPE_GET_BY_NAME,
              Collections.singletonMap(PostgresSqlStatement.INPUT_RECIPE_NAME, "%" + name + "%"));

      List<Recipe> recipes = new ArrayList<>();
      for (Map<String, Object> row : result) {
        // Map each row to a Recipe object
        Recipe recipe = mapToRecipe(row);
        // Query and set the recipe's ingredients
        recipe.setIngredients(getIngredientsForRecipe(recipe.getId()));
        // Query and set the recipe's cooking steps
        recipe.setCookingSteps(getCookingStepsForRecipe(recipe.getId()));
        recipes.add(recipe); // Add to the recipe list
      }

      logger.info("Querying recipes by name completed, found {} records", recipes.size());
      return recipes;
    } catch (Exception e) {
      logger.error("Exception occurred while querying recipes by name: {}", e.getMessage(), e);
      throw e;
    }
  }

  // Helper method: Build a map of recipe parameters
  private Map<String, Object> buildRecipeParams(Recipe recipe) {
    Map<String, Object> recipeParams = new HashMap<>();
    recipeParams.put(PostgresSqlStatement.COLUMN_RECIPE_CREATOR_ID, recipe.getCreatorId());
    recipeParams.put(PostgresSqlStatement.COLUMN_RECIPE_NAME, recipe.getName());
    recipeParams.put(PostgresSqlStatement.COLUMN_RECIPE_IMAGE, recipe.getImage());
    recipeParams.put(PostgresSqlStatement.COLUMN_RECIPE_DESCRIPTION, recipe.getDescription());
    recipeParams.put(PostgresSqlStatement.COLUMN_RECIPE_COOKING_TIME, recipe.getCookingTimeInSec());
    recipeParams.put(
        PostgresSqlStatement.COLUMN_RECIPE_DIFFICULTY_LEVEL, recipe.getDifficultyLevel());
    recipeParams.put(PostgresSqlStatement.COLUMN_RECIPE_RATING, recipe.getRating());
    // Converts ERecipeStatus to its corresponding integer value
    recipeParams.put(PostgresSqlStatement.COLUMN_RECIPE_STATUS, recipe.getStatus().code);
    recipeParams.put(PostgresSqlStatement.COLUMN_RECIPE_CUISINE, recipe.getCuisine());
    return recipeParams;
  }

  // Helper method: Map database record to Recipe object
  private Recipe mapToRecipe(Map<String, Object> row) {
    logger.debug("Mapping database record to Recipe object");
    logger.info("mapToRecipe: {}", row);
    Recipe recipe = new Recipe();

    recipe.setId(
        row.get(PostgresSqlStatement.COLUMN_RECIPE_ID) != null
            ? ((Number) row.get(PostgresSqlStatement.COLUMN_RECIPE_ID)).longValue()
            : null);
    recipe.setCreatorId(
        row.get(PostgresSqlStatement.COLUMN_RECIPE_CREATOR_ID) != null
            ? ((Number) row.get(PostgresSqlStatement.COLUMN_RECIPE_CREATOR_ID)).longValue()
            : null);
    recipe.setName(
        row.get(PostgresSqlStatement.COLUMN_RECIPE_NAME) != null
            ? (String) row.get(PostgresSqlStatement.COLUMN_RECIPE_NAME)
            : null);
    recipe.setImage(
        row.get(PostgresSqlStatement.COLUMN_RECIPE_IMAGE) != null
            ? (String) row.get(PostgresSqlStatement.COLUMN_RECIPE_IMAGE)
            : null);
    recipe.setDescription(
        row.get(PostgresSqlStatement.COLUMN_RECIPE_DESCRIPTION) != null
            ? (String) row.get(PostgresSqlStatement.COLUMN_RECIPE_DESCRIPTION)
            : null);
    recipe.setCookingTimeInSec(
        row.get(PostgresSqlStatement.COLUMN_RECIPE_COOKING_TIME) != null
            ? ((Number) row.get(PostgresSqlStatement.COLUMN_RECIPE_COOKING_TIME)).intValue()
            : null);
    recipe.setDifficultyLevel(
        row.get(PostgresSqlStatement.COLUMN_RECIPE_DIFFICULTY_LEVEL) != null
            ? ((Number) row.get(PostgresSqlStatement.COLUMN_RECIPE_DIFFICULTY_LEVEL)).intValue()
            : null);
    recipe.setRating(
        row.get(PostgresSqlStatement.COLUMN_RECIPE_RATING) != null
            ? ((Number) row.get(PostgresSqlStatement.COLUMN_RECIPE_RATING)).doubleValue()
            : null);
    // Convert integer values to ERecipeStatus
    recipe.setStatus(
        row.get(PostgresSqlStatement.COLUMN_RECIPE_STATUS) != null
            ? ERecipeStatus.valueOfCode(
                ((Number) row.get(PostgresSqlStatement.COLUMN_RECIPE_STATUS)).intValue())
            : null);
    recipe.setCuisine(
        row.get(PostgresSqlStatement.COLUMN_RECIPE_CUISINE) != null
            ? (String) row.get(PostgresSqlStatement.COLUMN_RECIPE_CUISINE)
            : null);
    recipe.setCreateDatetime(
        row.get("create_datetime") != null ? (Timestamp) row.get("create_datetime") : null);
    recipe.setUpdateDatetime(
        row.get("update_datetime") != null ? (Timestamp) row.get("update_datetime") : null);

    logger.debug("Recipe object mapping completed: ID={}", recipe.getId());
    return recipe;
  }

  // Helper method: Validate the recipe
  private void validateRecipe(Recipe recipe) {
    logger.debug("Validating recipe: {}", recipe.getName());
    if (recipe.getCreatorId() == null
        || recipe.getName() == null
        || recipe.getCookingTimeInSec() == null
        || recipe.getDifficultyLevel() == null
        || recipe.getRating() == null
        || recipe.getStatus() == null) {
      // TODO: if "cuisine" is not null, it should be added here.
      logger.error("Recipe validation failed, required fields are empty");
      throw new IllegalArgumentException("Required fields for recipe cannot be null");
    }
  }

  // Helper method: Validate the ingredient
  private void validateIngredient(Ingredient ingredient) {
    logger.debug("Validating ingredient: {}", ingredient.getName());
    if (ingredient.getName() == null
        || ingredient.getQuantity() == null
        || ingredient.getUom() == null) {
      logger.error("Ingredient validation failed, required fields are empty");
      throw new IllegalArgumentException("Required fields for ingredient cannot be null");
    }
  }

  // Helper method: Validate the cooking step
  private void validateCookingStep(CookingStep step) {
    logger.debug("Validating cooking step");
    if (step.getDescription() == null) {
      logger.error("Cooking step validation failed, description is empty");
      throw new IllegalArgumentException("Description for cooking step cannot be null");
    }
  }

  // Helper method: Insert ingredients associated with the recipe
  void insertIngredients(Recipe recipe) {
    logger.info("Starting to insert ingredients, Recipe ID={}", recipe.getId());

    // Check if ingredients are null
    if (recipe.getIngredients() == null) {
      logger.info("Ingredients are null, skipping insertion, Recipe ID={}", recipe.getId());
      return; // Return directly, do not perform insertion
    }

    for (Ingredient ingredient : recipe.getIngredients()) {
      // Validate the ingredient
      validateIngredient(ingredient);
      try {
        Map<String, Object> ingredientParams = new HashMap<>();
        ingredientParams.put(PostgresSqlStatement.INPUT_INGREDIENT_RECIPE_ID, recipe.getId());
        ingredientParams.put(PostgresSqlStatement.INPUT_INGREDIENT_NAME, ingredient.getName());
        ingredientParams.put(
            PostgresSqlStatement.INPUT_INGREDIENT_QUANTITY, ingredient.getQuantity());
        ingredientParams.put(PostgresSqlStatement.INPUT_INGREDIENT_UOM, ingredient.getUom());
        postgresDataAccess.upsertStatement(
            PostgresSqlStatement.SQL_INGREDIENT_ADD, ingredientParams);
        logger.debug("Inserted ingredient: {}", ingredient.getName());
      } catch (Exception e) {
        logger.error("Exception occurred while inserting ingredient: {}", e.getMessage(), e);
        throw e;
      }
    }
    logger.info("Ingredients insertion completed, Recipe ID={}", recipe.getId());
  }

  // Helper method: Delete ingredients associated with the recipe
  void deleteIngredients(Long recipeId) {
    logger.info("Starting to delete ingredients, Recipe ID={}", recipeId);
    try {
      Map<String, Object> deleteIngredientsParams = new HashMap<>();
      deleteIngredientsParams.put(PostgresSqlStatement.INPUT_INGREDIENT_RECIPE_ID, recipeId);
      int deletedRows =
          postgresDataAccess.upsertStatement(
              PostgresSqlStatement.SQL_INGREDIENT_DELETE_BY_RECIPE_ID, deleteIngredientsParams);
      logger.debug("Ingredients deleted successfully, number of deleted rows: {}", deletedRows);
    } catch (Exception e) {
      logger.error("Exception occurred while deleting ingredients: {}", e.getMessage(), e);
      throw e; // Rethrow the exception to trigger transaction rollback
    }
  }

  // Helper method: Insert cooking steps associated with the recipe
  void insertCookingSteps(Recipe recipe) {
    logger.info("Starting to insert cooking steps, Recipe ID={}", recipe.getId());

    // Check if cooking steps are null
    if (recipe.getCookingSteps() == null) {
      logger.info("Cooking steps are null, skipping insertion, Recipe ID={}", recipe.getId());
      return; // Return directly, do not perform insertion
    }

    for (CookingStep step : recipe.getCookingSteps()) {
      validateCookingStep(step); // Validate non-empty fields
      try {
        Map<String, Object> stepParams = new HashMap<>();
        stepParams.put(PostgresSqlStatement.INPUT_COOKING_STEP_RECIPE_ID, recipe.getId());
        stepParams.put(PostgresSqlStatement.INPUT_COOKING_STEP_DESCRIPTION, step.getDescription());
        stepParams.put(PostgresSqlStatement.INPUT_COOKING_STEP_IMAGE, step.getImage());
        postgresDataAccess.upsertStatement(PostgresSqlStatement.SQL_COOKING_STEP_ADD, stepParams);
        logger.debug("Inserted cooking step: {}", step.getDescription());
      } catch (Exception e) {
        logger.error("Exception occurred while inserting cooking step: {}", e.getMessage(), e);
        throw e; // Rethrow the exception to trigger transaction rollback
      }
    }
    logger.info("Cooking steps insertion completed, Recipe ID={}", recipe.getId());
  }

  // Helper method: Delete cooking steps associated with the recipe
  void deleteCookingSteps(Long recipeId) {
    logger.info("Starting to delete cooking steps, Recipe ID={}", recipeId);
    try {
      Map<String, Object> deleteStepsParams = new HashMap<>();
      deleteStepsParams.put(PostgresSqlStatement.INPUT_COOKING_STEP_RECIPE_ID, recipeId);
      int deletedRows =
          postgresDataAccess.upsertStatement(
              PostgresSqlStatement.SQL_COOKING_STEP_DELETE_RECIPE_ID, deleteStepsParams);
      logger.debug("Cooking steps deleted successfully, number of deleted rows: {}", deletedRows);
    } catch (Exception e) {
      logger.error("Exception occurred while deleting cooking steps: {}", e.getMessage(), e);
      throw e; // Rethrow the exception to trigger transaction rollback
    }
  }

  // Helper method: Get ingredients associated with the recipe
  private List<Ingredient> getIngredientsForRecipe(Long recipeId) {
    logger.info("Getting ingredients, Recipe ID={}", recipeId);

    // Construct query parameters
    Map<String, Object> params = new HashMap<>();
    params.put(PostgresSqlStatement.INPUT_INGREDIENT_RECIPE_ID, recipeId);

    // Execute the query
    List<Map<String, Object>> result =
        postgresDataAccess.queryStatement(
            PostgresSqlStatement.SQL_INGREDIENT_GET_BY_RECIPE_ID, params);

    // Construct the list of ingredients
    List<Ingredient> ingredients = new ArrayList<>();
    for (Map<String, Object> row : result) {
      Ingredient ingredient = new Ingredient();
      ingredient.setId(
          row.get(PostgresSqlStatement.COLUMN_INGREDIENT_ID) != null
              ? ((Number) row.get(PostgresSqlStatement.COLUMN_INGREDIENT_ID)).longValue()
              : null);
      ingredient.setRecipeId(
          row.get(PostgresSqlStatement.COLUMN_INGREDIENT_RECIPE_ID) != null
              ? ((Number) row.get(PostgresSqlStatement.COLUMN_INGREDIENT_RECIPE_ID)).longValue()
              : null);
      ingredient.setName(
          row.get(PostgresSqlStatement.COLUMN_INGREDIENT_NAME) != null
              ? (String) row.get(PostgresSqlStatement.COLUMN_INGREDIENT_NAME)
              : null);
      ingredient.setQuantity(
          row.get(PostgresSqlStatement.COLUMN_INGREDIENT_QUANTITY) != null
              ? ((Number) row.get(PostgresSqlStatement.COLUMN_INGREDIENT_QUANTITY)).doubleValue()
              : null);
      ingredient.setUom(
          row.get(PostgresSqlStatement.COLUMN_INGREDIENT_UOM) != null
              ? (String) row.get(PostgresSqlStatement.COLUMN_INGREDIENT_UOM)
              : null);
      ingredients.add(ingredient); // Add to the ingredient list
      logger.debug("Loaded ingredient: {}", ingredient.getName());
    }

    logger.info("Ingredients loading completed, count={}", ingredients.size());
    return ingredients;
  }

  // Helper method: Get cooking steps associated with the recipe
  private List<CookingStep> getCookingStepsForRecipe(Long recipeId) {
    logger.info("Getting cooking steps, Recipe ID={}", recipeId);

    // Construct query parameters
    Map<String, Object> params = new HashMap<>();
    params.put(PostgresSqlStatement.INPUT_COOKING_STEP_RECIPE_ID, recipeId);

    // Execute the query
    List<Map<String, Object>> result =
        postgresDataAccess.queryStatement(
            PostgresSqlStatement.SQL_COOKING_STEP_GET_BY_RECIPE_ID, params);

    // Construct the list of cooking steps
    List<CookingStep> steps = new ArrayList<>();
    for (Map<String, Object> row : result) {
      CookingStep step = new CookingStep();
      step.setId(
          row.get(PostgresSqlStatement.COLUMN_COOKING_STEP_ID) != null
              ? ((Number) row.get(PostgresSqlStatement.COLUMN_COOKING_STEP_ID)).longValue()
              : null);
      step.setRecipeId(
          row.get(PostgresSqlStatement.COLUMN_COOKING_STEP_RECIPE_ID) != null
              ? ((Number) row.get(PostgresSqlStatement.COLUMN_COOKING_STEP_RECIPE_ID)).longValue()
              : null);
      step.setDescription(
          row.get(PostgresSqlStatement.COLUMN_COOKING_STEP_DESCRIPTION) != null
              ? (String) row.get(PostgresSqlStatement.COLUMN_COOKING_STEP_DESCRIPTION)
              : null);
      step.setImage(
          row.get(PostgresSqlStatement.COLUMN_COOKING_STEP_IMAGE) != null
              ? (String) row.get(PostgresSqlStatement.COLUMN_COOKING_STEP_IMAGE)
              : null);
      steps.add(step); // Add to the cooking steps list
      logger.debug("Loaded cooking step: {}", step.getDescription());
    }

    logger.info("Cooking steps loading completed, count={}", steps.size());
    return steps;
  }
}
