package nus.iss.team3.backend.dataaccess;

import static nus.iss.team3.backend.service.util.SqlUtilities.getLongValue;
import static nus.iss.team3.backend.service.util.SqlUtilities.getStringValue;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import nus.iss.team3.backend.dataaccess.postgres.PostgresDataAccess;
import nus.iss.team3.backend.entity.CookingStep;
import nus.iss.team3.backend.entity.ERecipeStatus;
import nus.iss.team3.backend.entity.Recipe;
import nus.iss.team3.backend.entity.RecipeIngredient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository class to connect to postgres for recipe data.
 *
 * @author Mao Weining
 */
@Repository
public class RecipeDataAccess implements IRecipeDataAccess {

  private static final Logger logger = LogManager.getLogger(RecipeDataAccess.class);
  private final PostgresDataAccess postgresDataAccess;

  public RecipeDataAccess(PostgresDataAccess postgresDataAccess) {
    this.postgresDataAccess = postgresDataAccess;
  }

  public static class RecipeIngredientMapper {
    public RecipeIngredient map(Map<String, Object> row) {
      return RecipeIngredient.builder()
          .id(getLongValue(row, PostgresSqlStatementRecipe.COLUMN_INGREDIENT_ID))
          .recipeId(getLongValue(row, PostgresSqlStatementRecipe.COLUMN_INGREDIENT_RECIPE_ID))
          .name(getStringValue(row, PostgresSqlStatementRecipe.COLUMN_INGREDIENT_NAME))
          .quantity(getDoubleValue(row, PostgresSqlStatementRecipe.COLUMN_INGREDIENT_QUANTITY))
          .uom(getStringValue(row, PostgresSqlStatementRecipe.COLUMN_INGREDIENT_UOM))
          .build();
    }

    private Long getLongValue(Map<String, Object> row, String column) {
      return row.get(column) != null ? ((Number) row.get(column)).longValue() : null;
    }

    private String getStringValue(Map<String, Object> row, String column) {
      return row.get(column) != null ? (String) row.get(column) : null;
    }

    private Double getDoubleValue(Map<String, Object> row, String column) {
      return row.get(column) != null ? ((Number) row.get(column)).doubleValue() : null;
    }
  }

  @Override
  @Transactional
  public Recipe addRecipe(Recipe recipe) {
    logger.debug("Starting to add recipe: {}", recipe.getName());
    try {
      validateRecipe(recipe);
      logger.debug("Adding recipe: Recipe validation passed");

      Map<String, Object> recipeParams = buildRecipeParams(recipe);

      List<Map<String, Object>> result =
          postgresDataAccess.queryStatement(
              PostgresSqlStatementRecipe.SQL_RECIPE_ADD, recipeParams);

      if (result == null || result.isEmpty()) {
        logger.warn("Failed to insert recipe, no generated ID returned");
        throw new IllegalArgumentException("Failed to insert recipe");
      }

      // Get the generated recipe ID and set it to the recipe object
      Long recipeId =
          ((Number) result.getFirst().get(PostgresSqlStatementRecipe.COLUMN_RECIPE_ID)).longValue();
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

      logger.debug("Recipe addition completed: ID={}", recipeId);

      recipe.setId(recipeId);
      return recipe;
    } catch (Exception e) {
      logger.error("Exception occurred while adding recipe: {}", e.getMessage(), e);
      throw e; // Rethrow the exception to trigger transaction rollback
    }
  }

  @Override
  @Transactional
  public boolean updateRecipe(Recipe recipe) {
    logger.debug("Starting to update recipe: ID={}", recipe.getId());
    try {
      validateRecipe(recipe);
      logger.debug("Updating recipe: Recipe validation passed");

      // Build the update SQL statement and parameters
      Map<String, Object> recipeParams = buildRecipeParams(recipe);
      recipeParams.put(PostgresSqlStatementRecipe.COLUMN_RECIPE_ID, recipe.getId());

      // Execute the update statement
      int updatedRows =
          postgresDataAccess.upsertStatement(
              PostgresSqlStatementRecipe.SQL_RECIPE_UPDATE, recipeParams);

      if (updatedRows == 0) {
        logger.warn("No recipe found with ID={} for update", recipe.getId());
        return false; // Return false if no rows were updated
      }
      logger.debug("Recipe updated successfully, number of updated rows: {}", updatedRows);

      // Update ingredients: first delete existing ingredients, then insert new ones
      deleteIngredients(recipe.getId());
      if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
        insertIngredients(recipe);
        logger.debug("Ingredients updated successfully");
      } else {
        logger.debug("No ingredients to update for the recipe");
      }
      logger.debug("Ingredients updated successfully");

      // Update cooking steps: first delete existing steps, then insert new ones
      deleteCookingSteps(recipe.getId());
      if (recipe.getCookingSteps() != null && !recipe.getCookingSteps().isEmpty()) {
        insertCookingSteps(recipe);
        logger.debug("Cooking steps updated successfully");
      } else {
        logger.debug("No cooking steps to update for the recipe");
      }
      logger.debug("Cooking steps updated successfully");

      logger.debug("Recipe update completed: ID={}", recipe.getId());
      return true;
    } catch (Exception e) {
      logger.error("Exception occurred while updating recipe: {}", e.getMessage(), e);
      throw e; // Rethrow the exception to trigger transaction rollback
    }
  }

  @Override
  @Transactional
  public boolean deleteRecipeById(Long recipeId) {
    logger.debug("Starting to delete recipe: ID={}", recipeId);
    try {
      deleteIngredients(recipeId);
      logger.debug("Ingredients deleted successfully");

      deleteCookingSteps(recipeId);
      logger.debug("Cooking steps deleted successfully");

      // Execute the delete statement
      int deletedRows =
          postgresDataAccess.upsertStatement(
              PostgresSqlStatementRecipe.SQL_RECIPE_DELETE_BY_ID,
              Collections.singletonMap(PostgresSqlStatementRecipe.INPUT_RECIPE_ID, recipeId));

      if (deletedRows > 0) {
        logger.debug("Recipe deleted successfully: ID={}", recipeId);
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
    logger.debug("Querying recipe: ID={}", recipeId);
    try {
      // Execute the query
      List<Map<String, Object>> result =
          postgresDataAccess.queryStatement(
              PostgresSqlStatementRecipe.SQL_RECIPE_GET_BY_ID,
              Collections.singletonMap(PostgresSqlStatementRecipe.INPUT_RECIPE_ID, recipeId));

      if (result == null || result.isEmpty()) {
        logger.warn("No recipe found with ID={}", recipeId);
        return null;
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

      logger.debug("Recipe query completed: ID={}", recipeId);

      return recipe;
    } catch (Exception e) {
      logger.error("Exception occurred while querying recipe: {}", e.getMessage(), e);
      throw e;
    }
  }

  /**
   * @param draftRecipeId
   * @return
   */
  @Override
  public Recipe getRecipeByDraftId(Long draftRecipeId) {
    logger.debug("Querying by draft recipeId: ID={}", draftRecipeId);
    try {
      // Execute the query
      List<Map<String, Object>> result =
          postgresDataAccess.queryStatement(
              PostgresSqlStatementRecipe.SQL_RECIPE_GET_BY_DRAFT_ID,
              Collections.singletonMap(
                  PostgresSqlStatementRecipe.INPUT_RECIPE_DRAFT_ID, draftRecipeId));

      if (result == null || result.isEmpty()) {
        // normal to no have draft recipe record, ok not to log anything.
        return null;
      }

      // Map the query result to a Recipe object
      Recipe recipe = mapToRecipe(result.getFirst());
      logger.debug(
          "Recipe mapping successful: draft ID={}, main Id = {}", draftRecipeId, recipe.getId());

      // Query and set the recipe's ingredients
      recipe.setIngredients(getIngredientsForRecipe(recipe.getId()));
      logger.debug("Ingredients loaded successfully: Recipe ID={}", recipe.getId());

      // Query and set the recipe's cooking steps
      recipe.setCookingSteps(getCookingStepsForRecipe(recipe.getId()));
      logger.debug("Cooking steps loaded successfully: Recipe ID={}", recipe.getId());

      logger.debug("Recipe query completed: ID={}", recipe.getId());

      return recipe;
    } catch (Exception e) {
      logger.error("Exception occurred while querying recipe: {}", e.getMessage(), e);
      throw e;
    }
  }

  @Override
  public List<Recipe> getAllRecipes() {
    logger.debug("Querying all recipes");
    try {
      // Execute the query
      List<Map<String, Object>> result =
          postgresDataAccess.queryStatement(PostgresSqlStatementRecipe.SQL_RECIPE_GET_ALL, null);

      List<Recipe> recipes = new ArrayList<>();
      for (Map<String, Object> row : result) {
        Recipe recipe = mapToRecipe(row);
        // Query and set the recipe's ingredients
        recipe.setIngredients(getIngredientsForRecipe(recipe.getId()));
        // Query and set the recipe's cooking steps
        recipe.setCookingSteps(getCookingStepsForRecipe(recipe.getId()));
        recipes.add(recipe); // Add to the recipe list
      }

      logger.debug("Querying all recipes completed, count: {}", recipes.size());
      return recipes;
    } catch (Exception e) {
      logger.error("Exception occurred while querying all recipes: {}", e.getMessage(), e);
      throw e;
    }
  }

  @Override
  public List<Recipe> getAllPublishedRecipes() {
    logger.debug("Querying all published recipes");
    try {
      // Execute the query
      List<Map<String, Object>> result =
          postgresDataAccess.queryStatement(
              PostgresSqlStatementRecipe.SQL_RECIPE_GET_ALL_PUBLISHED, null);

      if (result == null) {
        return null;
      }
      List<Recipe> recipes = new ArrayList<>();
      for (Map<String, Object> row : result) {
        Recipe recipe = mapToRecipe(row);
        // Query and set the recipe's ingredients
        recipe.setIngredients(getIngredientsForRecipe(recipe.getId()));
        // Query and set the recipe's cooking steps
        recipe.setCookingSteps(getCookingStepsForRecipe(recipe.getId()));
        recipes.add(recipe); // Add to the recipe list
      }

      logger.debug("Querying all published recipes completed, count: {} ", recipes.size());
      return recipes;
    } catch (Exception e) {
      logger.error("Exception occurred while querying all recipes: {}", e.getMessage(), e);
      throw e;
    }
  }

  @Override
  public List<Recipe> getRecipesByName(String name) {
    logger.debug("Querying recipes by name: Name contains '{}'", name);
    try {
      // Execute the query: use wildcard for fuzzy query
      List<Map<String, Object>> result =
          postgresDataAccess.queryStatement(
              PostgresSqlStatementRecipe.SQL_RECIPE_GET_BY_NAME,
              Collections.singletonMap(
                  PostgresSqlStatementRecipe.INPUT_RECIPE_NAME, "%" + name + "%"));

      List<Recipe> recipes = new ArrayList<>();
      for (Map<String, Object> row : result) {
        Recipe recipe = mapToRecipe(row);
        // Query and set the recipe's ingredients
        recipe.setIngredients(getIngredientsForRecipe(recipe.getId()));
        // Query and set the recipe's cooking steps
        recipe.setCookingSteps(getCookingStepsForRecipe(recipe.getId()));
        recipes.add(recipe);
      }

      logger.debug("Querying recipes by name completed, found {} records", recipes.size());
      return recipes;
    } catch (Exception e) {
      logger.error("Exception occurred while querying recipes by name: {}", e.getMessage(), e);
      throw e;
    }
  }

  @Override
  public List<Recipe> getRecipeByCreatorId(int creatorId) {
    logger.debug("Querying recipe: creator Id={}", creatorId);
    try {
      // Execute the query
      List<Map<String, Object>> result =
          postgresDataAccess.queryStatement(
              PostgresSqlStatementRecipe.SQL_RECIPE_GET_BY_CREATOR_ID,
              Collections.singletonMap(
                  PostgresSqlStatementRecipe.INPUT_RECIPE_CREATOR_ID, creatorId));
      if (result == null || result.isEmpty()) {
        logger.warn("No recipe found with creatorID={} ", creatorId);
        return null;
      }
      // TODO: fixed set ingredients and cooking steps, and add try-catch structure
      List<Recipe> recipes = new ArrayList<>();
      for (Map<String, Object> row : result) {
        Recipe recipe = mapToRecipe(row);
        // Query and set the recipe's ingredients
        recipe.setIngredients(getIngredientsForRecipe(recipe.getId()));
        // Query and set the recipe's cooking steps
        recipe.setCookingSteps(getCookingStepsForRecipe(recipe.getId()));
        recipes.add(recipe); // Add to the recipe list
      }

      logger.debug("Querying recipes with creatorId completed, count: {}", recipes.size());
      return recipes;
    } catch (Exception e) {
      logger.error(
          "Exception occurred while querying recipes with creatorId: {}", e.getMessage(), e);
      throw e;
    }
  }

  /**
   * @param recipeId
   * @param rating
   * @return
   */
  @Override
  public boolean updateRecipeRating(Long recipeId, double rating) {
    logger.info("Starting to update recipe rating: ID={}, Rating={}", recipeId, rating);
    try {
      Map<String, Object> params = new HashMap<>();
      params.put(PostgresSqlStatementRecipe.INPUT_RECIPE_ID, recipeId);
      params.put(PostgresSqlStatementRecipe.INPUT_RECIPE_RATING, rating);

      int updatedRows =
          postgresDataAccess.upsertStatement(
              PostgresSqlStatementRecipe.SQL_RECIPE_UPDATE_RATING, params);

      if (updatedRows == 0) {
        logger.warn("No recipe found with ID={} for rating update", recipeId);
        return false;
      }
      logger.info("Recipe rating updated successfully: ID={}, Rating={}", recipeId, rating);
      return true;
    } catch (Exception e) {
      logger.error("Exception occurred while updating recipe rating: {}", e.getMessage(), e);
      throw e;
    }
  }

  // Helper method: Build a map of recipe parameters
  private Map<String, Object> buildRecipeParams(Recipe recipe) {
    Map<String, Object> recipeParams = new HashMap<>();
    recipeParams.put(PostgresSqlStatementRecipe.COLUMN_RECIPE_CREATOR_ID, recipe.getCreatorId());
    recipeParams.put(PostgresSqlStatementRecipe.COLUMN_RECIPE_NAME, recipe.getName());
    recipeParams.put(PostgresSqlStatementRecipe.COLUMN_RECIPE_IMAGE, recipe.getImage());
    recipeParams.put(PostgresSqlStatementRecipe.COLUMN_RECIPE_DESCRIPTION, recipe.getDescription());
    recipeParams.put(
        PostgresSqlStatementRecipe.COLUMN_RECIPE_COOKING_TIME, recipe.getCookingTimeInSec());
    recipeParams.put(
        PostgresSqlStatementRecipe.COLUMN_RECIPE_DIFFICULTY_LEVEL, recipe.getDifficultyLevel());
    recipeParams.put(PostgresSqlStatementRecipe.COLUMN_RECIPE_RATING, recipe.getRating());
    // Converts ERecipeStatus to its corresponding integer value
    recipeParams.put(PostgresSqlStatementRecipe.COLUMN_RECIPE_STATUS, recipe.getStatus().code);
    recipeParams.put(PostgresSqlStatementRecipe.COLUMN_RECIPE_CUISINE, recipe.getCuisine());
    recipeParams.put(
        PostgresSqlStatementRecipe.COLUMN_RECIPE_DRAFT_ID,
        recipe.getDraftRecipe() != null ? recipe.getDraftRecipe().getId() : null);
    return recipeParams;
  }

  // Helper method: Map database record to Recipe object
  private Recipe mapToRecipe(Map<String, Object> row) {
    logger.debug("Mapping database record to Recipe object");
    logger.debug("mapToRecipe: {}", row);
    Recipe recipe = new Recipe();

    recipe.setId(
        row.get(PostgresSqlStatementRecipe.COLUMN_RECIPE_ID) != null
            ? ((Number) row.get(PostgresSqlStatementRecipe.COLUMN_RECIPE_ID)).longValue()
            : null);
    recipe.setCreatorId(
        row.get(PostgresSqlStatementRecipe.COLUMN_RECIPE_CREATOR_ID) != null
            ? ((Number) row.get(PostgresSqlStatementRecipe.COLUMN_RECIPE_CREATOR_ID)).longValue()
            : null);
    recipe.setName(
        row.get(PostgresSqlStatementRecipe.COLUMN_RECIPE_NAME) != null
            ? (String) row.get(PostgresSqlStatementRecipe.COLUMN_RECIPE_NAME)
            : null);
    recipe.setImage(
        row.get(PostgresSqlStatementRecipe.COLUMN_RECIPE_IMAGE) != null
            ? (String) row.get(PostgresSqlStatementRecipe.COLUMN_RECIPE_IMAGE)
            : null);
    recipe.setDescription(
        row.get(PostgresSqlStatementRecipe.COLUMN_RECIPE_DESCRIPTION) != null
            ? (String) row.get(PostgresSqlStatementRecipe.COLUMN_RECIPE_DESCRIPTION)
            : null);
    recipe.setCookingTimeInSec(
        row.get(PostgresSqlStatementRecipe.COLUMN_RECIPE_COOKING_TIME) != null
            ? ((Number) row.get(PostgresSqlStatementRecipe.COLUMN_RECIPE_COOKING_TIME)).intValue()
            : null);
    recipe.setDifficultyLevel(
        row.get(PostgresSqlStatementRecipe.COLUMN_RECIPE_DIFFICULTY_LEVEL) != null
            ? ((Number) row.get(PostgresSqlStatementRecipe.COLUMN_RECIPE_DIFFICULTY_LEVEL))
                .intValue()
            : null);
    recipe.setRating(
        row.get(PostgresSqlStatementRecipe.COLUMN_RECIPE_RATING) != null
            ? ((Number) row.get(PostgresSqlStatementRecipe.COLUMN_RECIPE_RATING)).doubleValue()
            : null);
    // Convert integer values to ERecipeStatus
    recipe.setStatus(
        row.get(PostgresSqlStatementRecipe.COLUMN_RECIPE_STATUS) != null
            ? ERecipeStatus.valueOfCode(
                ((Number) row.get(PostgresSqlStatementRecipe.COLUMN_RECIPE_STATUS)).intValue())
            : null);
    recipe.setCuisine(
        row.get(PostgresSqlStatementRecipe.COLUMN_RECIPE_CUISINE) != null
            ? (String) row.get(PostgresSqlStatementRecipe.COLUMN_RECIPE_CUISINE)
            : null);
    recipe.setCreateDatetime(
        row.get("create_datetime") != null ? (Timestamp) row.get("create_datetime") : null);
    recipe.setUpdateDatetime(
        row.get("update_datetime") != null ? (Timestamp) row.get("update_datetime") : null);

    if (row.get(PostgresSqlStatementRecipe.COLUMN_RECIPE_DRAFT_ID) != null) {
      Recipe temp = new Recipe();
      temp.setId(((Number) row.get(PostgresSqlStatementRecipe.COLUMN_RECIPE_DRAFT_ID)).longValue());
      recipe.setDraftRecipe(temp);
    }

    logger.debug("Recipe object mapping completed: ID={}", recipe.getId());
    return recipe;
  }

  private void validateRecipe(Recipe recipe) {
    logger.debug("Validating recipe: {}", recipe.getName());
    if (recipe.getCreatorId() == null
        || recipe.getName() == null
        || recipe.getCookingTimeInSec() == null
        || recipe.getDifficultyLevel() == null
        || recipe.getRating() == null
        || recipe.getStatus() == null
        || recipe.getCuisine() == null) {
      logger.error("Recipe validation failed, required fields are empty");
      throw new IllegalArgumentException("Required fields for recipe cannot be null");
    }
  }

  private void validateIngredient(RecipeIngredient ingredient) {
    logger.debug("Validating ingredient: {}", ingredient.getName());
    if (ingredient.getName() == null
        || ingredient.getQuantity() == null
        || ingredient.getUom() == null) {
      logger.error("Ingredient validation failed, required fields are empty");
      throw new IllegalArgumentException("Required fields for ingredient cannot be null");
    }
  }

  private void validateCookingStep(CookingStep step) {
    logger.debug("Validating cooking step");
    if (step.getDescription() == null) {
      logger.error("Cooking step validation failed, description is empty");
      throw new IllegalArgumentException("Description for cooking step cannot be null");
    }
  }

  // Helper method: Insert ingredients associated with the recipe
  void insertIngredients(Recipe recipe) {
    logger.debug("Starting to insert ingredients, Recipe ID={}", recipe.getId());

    if (recipe.getIngredients() == null) {
      logger.debug("Ingredients are null, skipping insertion, Recipe ID={}", recipe.getId());
      return; // Return directly, do not perform insertion
    }

    for (RecipeIngredient ingredient : recipe.getIngredients()) {
      validateIngredient(ingredient);
      try {
        Map<String, Object> ingredientParams = new HashMap<>();
        ingredientParams.put(PostgresSqlStatementRecipe.INPUT_INGREDIENT_RECIPE_ID, recipe.getId());
        ingredientParams.put(
            PostgresSqlStatementRecipe.INPUT_INGREDIENT_NAME, ingredient.getName());
        ingredientParams.put(
            PostgresSqlStatementRecipe.INPUT_INGREDIENT_QUANTITY, ingredient.getQuantity());
        ingredientParams.put(PostgresSqlStatementRecipe.INPUT_INGREDIENT_UOM, ingredient.getUom());
        postgresDataAccess.queryStatement(
            PostgresSqlStatementRecipe.SQL_INGREDIENT_ADD, ingredientParams);
        logger.debug("Inserted ingredient: {}", ingredient.getName());
      } catch (Exception e) {
        logger.error("Exception occurred while inserting ingredient: {}", e.getMessage(), e);
        throw e;
      }
    }
    logger.debug("Ingredients insertion completed, Recipe ID={}", recipe.getId());
  }

  // Helper method: Delete ingredients associated with the recipe
  void deleteIngredients(Long recipeId) {
    logger.debug("Starting to delete ingredients, Recipe ID={}", recipeId);
    try {
      Map<String, Object> deleteIngredientsParams = new HashMap<>();
      deleteIngredientsParams.put(PostgresSqlStatementRecipe.INPUT_INGREDIENT_RECIPE_ID, recipeId);
      int deletedRows =
          postgresDataAccess.upsertStatement(
              PostgresSqlStatementRecipe.SQL_INGREDIENT_DELETE_BY_RECIPE_ID,
              deleteIngredientsParams);
      logger.debug("Ingredients deleted successfully, number of deleted rows: {}", deletedRows);
    } catch (Exception e) {
      logger.error("Exception occurred while deleting ingredients: {}", e.getMessage(), e);
      throw e; // Rethrow the exception to trigger transaction rollback
    }
  }

  // Helper method: Insert cooking steps associated with the recipe
  void insertCookingSteps(Recipe recipe) {
    logger.debug("Starting to insert cooking steps, Recipe ID={}", recipe.getId());

    if (recipe.getCookingSteps() == null) {

      logger.debug("Cooking steps are null, skipping insertion, Recipe ID={}", recipe.getId());

      return; // Return directly, do not perform insertion
    }

    for (CookingStep step : recipe.getCookingSteps()) {
      validateCookingStep(step); // Validate non-empty fields
      try {
        Map<String, Object> stepParams = new HashMap<>();
        stepParams.put(PostgresSqlStatementRecipe.INPUT_COOKING_STEP_RECIPE_ID, recipe.getId());
        stepParams.put(
            PostgresSqlStatementRecipe.INPUT_COOKING_STEP_DESCRIPTION, step.getDescription());
        stepParams.put(PostgresSqlStatementRecipe.INPUT_COOKING_STEP_IMAGE, step.getImage());
        postgresDataAccess.queryStatement(
            PostgresSqlStatementRecipe.SQL_COOKING_STEP_ADD, stepParams);
        logger.debug("Inserted cooking step: {}", step.getDescription());
      } catch (Exception e) {
        logger.error("Exception occurred while inserting cooking step: {}", e.getMessage(), e);
        throw e; // Rethrow the exception to trigger transaction rollback
      }
    }
    logger.debug("Cooking steps insertion completed, Recipe ID={}", recipe.getId());
  }

  // Helper method: Delete cooking steps associated with the recipe
  void deleteCookingSteps(Long recipeId) {
    logger.debug("Starting to delete cooking steps, Recipe ID={}", recipeId);
    try {
      Map<String, Object> deleteStepsParams = new HashMap<>();
      deleteStepsParams.put(PostgresSqlStatementRecipe.INPUT_COOKING_STEP_RECIPE_ID, recipeId);
      int deletedRows =
          postgresDataAccess.upsertStatement(
              PostgresSqlStatementRecipe.SQL_COOKING_STEP_DELETE_RECIPE_ID, deleteStepsParams);
      logger.debug("Cooking steps deleted successfully, number of deleted rows: {}", deletedRows);
    } catch (Exception e) {
      logger.error("Exception occurred while deleting cooking steps: {}", e.getMessage(), e);
      throw e; // Rethrow the exception to trigger transaction rollback
    }
  }

  private List<RecipeIngredient> getIngredientsForRecipe(Long recipeId) {
    logger.debug("Getting ingredients, Recipe ID={}", recipeId);

    Map<String, Object> params =
        Map.of(PostgresSqlStatementRecipe.INPUT_INGREDIENT_RECIPE_ID, recipeId);
    List<Map<String, Object>> result =
        postgresDataAccess.queryStatement(
            PostgresSqlStatementRecipe.SQL_INGREDIENT_GET_BY_RECIPE_ID, params);

    RecipeIngredientMapper mapper = new RecipeIngredientMapper();
    List<RecipeIngredient> ingredients =
        result.stream().map(mapper::map).collect(Collectors.toList());

    logger.debug("Ingredients loading completed, count={}", ingredients.size());
    return ingredients;
  }

  // Helper method: Get cooking steps associated with the recipe
  private List<CookingStep> getCookingStepsForRecipe(Long recipeId) {
    logger.debug("Getting cooking steps, Recipe ID={}", recipeId);

    Map<String, Object> params =
        Map.of(PostgresSqlStatementRecipe.INPUT_COOKING_STEP_RECIPE_ID, recipeId);
    List<Map<String, Object>> result =
        postgresDataAccess.queryStatement(
            PostgresSqlStatementRecipe.SQL_COOKING_STEP_GET_BY_RECIPE_ID, params);

    List<CookingStep> steps =
        result.stream()
            .map(
                row ->
                    CookingStep.builder()
                        .id(getLongValue(row, PostgresSqlStatementRecipe.COLUMN_COOKING_STEP_ID))
                        .recipeId(
                            getLongValue(
                                row, PostgresSqlStatementRecipe.COLUMN_COOKING_STEP_RECIPE_ID))
                        .description(
                            getStringValue(
                                row, PostgresSqlStatementRecipe.COLUMN_COOKING_STEP_DESCRIPTION))
                        .image(
                            getStringValue(
                                row, PostgresSqlStatementRecipe.COLUMN_COOKING_STEP_IMAGE))
                        .build())
            .collect(Collectors.toList());

    logger.debug("Cooking steps loading completed, count={}", steps.size());
    return steps;
  }
}
