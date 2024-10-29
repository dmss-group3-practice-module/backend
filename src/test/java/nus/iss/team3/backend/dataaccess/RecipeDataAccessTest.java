package nus.iss.team3.backend.dataaccess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import nus.iss.team3.backend.dataaccess.postgres.PostgresDataAccess;
import nus.iss.team3.backend.entity.CookingStep;
import nus.iss.team3.backend.entity.ERecipeStatus;
import nus.iss.team3.backend.entity.Recipe;
import nus.iss.team3.backend.entity.RecipeIngredient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecipeDataAccessTest {

  @Mock private PostgresDataAccess postgresDataAccess;
  @InjectMocks private RecipeDataAccess recipeDataAccess;

  /**
   * Test successful addition of a recipe. Verify that the insert operation is executed as expected
   * and check the related INSERT statements.
   */
  @Test
  void addRecipe_Success() {
    // Arrange: Create a sample recipe object
    Recipe recipe = createSampleRecipe();
    Map<String, Object> insertResult = Map.of("id", 1L);
    // Mock the insertion of the recipe to return the generated ID
    when(postgresDataAccess.queryStatement(anyString(), anyMap()))
        .thenReturn(Collections.singletonList(insertResult));

    // Act: Call the method to add the recipe
    boolean result = recipeDataAccess.addRecipe(recipe);

    // Assert: Verify that the addition was successful and that the recipe ID was set
    assertTrue(result);
    assertEquals(1L, recipe.getId());

    // Verify that the SQL statement to insert the recipe was called once
    verify(postgresDataAccess, times(3)).queryStatement(contains("INSERT INTO recipe"), anyMap());

    // Verify that the INSERT statement for each ingredient was called correctly
    for (RecipeIngredient ingredient : recipe.getIngredients()) {
      // argThat(params -> { ... }) is a parameter matcher used to verify that the parameters passed
      // to the upsertStatement method are correct.
      verify(postgresDataAccess, times(1))
          .queryStatement(
              contains("INSERT INTO recipe_ingredients"),
              argThat(
                  params ->
                      Objects.equals(params.get("recipe_id"), 1L)
                          && Objects.equals(params.get("name"), ingredient.getName())
                          && Objects.equals(params.get("quantity"), ingredient.getQuantity())
                          && Objects.equals(params.get("uom"), ingredient.getUom())));
    }

    // Verify that the INSERT statement for each cooking step was called correctly
    for (CookingStep step : recipe.getCookingSteps()) {
      verify(postgresDataAccess, times(1))
          .queryStatement(
              contains("INSERT INTO recipe_cooking_step"),
              argThat(
                  params ->
                      Objects.equals(params.get("recipe_id"), 1L)
                          && Objects.equals(params.get("description"), step.getDescription())
                          && Objects.equals(params.get("image"), step.getImage())));
    }

    // Confirm that no deletes operations were executed
    verify(postgresDataAccess, never()).upsertStatement(contains("DELETE FROM"), anyMap());
  }

  /**
   * Test the failure case of adding a recipe when no rows are returned from inserting the recipe.
   * Verify that the method should return false and that the related INSERT statements are not
   * executed.
   */
  @Test
  void addRecipe_Failure_InsertRecipe() {
    // Arrange: Create a sample recipe object
    Recipe recipe = createSampleRecipe();
    // Mock that inserting the recipe returns no results
    when(postgresDataAccess.queryStatement(anyString(), anyMap()))
        .thenReturn(Collections.emptyList());

    // Act: Call the method to add the recipe
    boolean result = recipeDataAccess.addRecipe(recipe);

    // Assert: Verify that the addition failed and that the recipe ID remains null
    assertFalse(result);
    assertNull(recipe.getId());

    // Verify that only the attempt to insert the recipe was made, without inserting ingredients or
    // cooking steps
    verify(postgresDataAccess, times(1)).queryStatement(contains("INSERT INTO recipe"), anyMap());
    verify(postgresDataAccess, never())
        .queryStatement(contains("INSERT INTO recipe_ingredients"), anyMap());
    verify(postgresDataAccess, never())
        .queryStatement(contains("INSERT INTO recipe_cooking_step"), anyMap());
  }

  /**
   * Test that an exception is thrown when adding an invalid recipe. Verify that the method should
   * throw IllegalArgumentException and that there is no interaction with PostgresDataAccess.
   */
  @Test
  void addRecipe_InvalidRecipe_ThrowsException() {
    // Arrange: Create an invalid recipe object missing required fields
    Recipe invalidRecipe = new Recipe(); // Missing required fields

    // Act & Assert: Calling the add method should throw an exception
    Exception exception =
        assertThrows(
            IllegalArgumentException.class, () -> recipeDataAccess.addRecipe(invalidRecipe));

    // Verify that the exception message is correct
    assertEquals("Required fields for recipe cannot be null", exception.getMessage());

    // Verify that there was no interaction with PostgresDataAccess
    verify(postgresDataAccess, never()).queryStatement(anyString(), anyMap());
    verify(postgresDataAccess, never()).upsertStatement(anyString(), anyMap());
  }

  /**
   * Test that an exception is thrown when the database throws an exception while adding a recipe.
   * Verify that the method should throw the corresponding exception and log the error.
   */
  @Test
  void addRecipe_DatabaseException_ThrowsException() {
    // Arrange: Create a sample recipe object
    Recipe recipe = createSampleRecipeWithMultipleIngredientsAndSteps();
    // Mock that an exception is thrown when inserting the recipe
    when(postgresDataAccess.queryStatement(anyString(), anyMap()))
        .thenThrow(new RuntimeException("Database insert error"));

    // Act & Assert: Calling the method to add the recipe should throw an exception
    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> recipeDataAccess.addRecipe(recipe));

    // Verify that the exception message is correct
    assertEquals("Database insert error", exception.getMessage());

    // Verify that the SQL statement to insert the recipe was called once
    verify(postgresDataAccess, times(1)).queryStatement(contains("INSERT INTO recipe"), anyMap());

    // Verify that no insertions for ingredients and steps were executed
    verify(postgresDataAccess, never())
        .upsertStatement(contains("INSERT INTO recipe_ingredients"), anyMap());
    verify(postgresDataAccess, never())
        .upsertStatement(contains("INSERT INTO recipe_cooking_step"), anyMap());
  }

  /**
   * Test the successful update of a recipe. Verify that the method returns true, and that the
   * related UPDATE, DELETE, and INSERT statements are executed as expected.
   */
  @Test
  void updateRecipe_Success() {
    // Arrange: Create a sample recipe object
    Recipe recipe = createSampleRecipe();
    recipe.setId(1L); // Set the ID for the recipe to be updated

    // Mock the behavior of the database access methods
    when(postgresDataAccess.upsertStatement(anyString(), anyMap())).thenReturn(1);

    // Act: Call the method to update the recipe
    boolean result = recipeDataAccess.updateRecipe(recipe);

    // Assert: Verify that the update was successful
    assertTrue(result);

    // Verify that the SQL statement to update the recipe was called once
    verify(postgresDataAccess, times(1))
        .upsertStatement(
            contains("UPDATE recipe"),
            argThat(
                params ->
                    Objects.equals(params.get(PostgresSqlStatementRecipe.COLUMN_RECIPE_ID), 1L)
                        && Objects.equals(
                            params.get(PostgresSqlStatementRecipe.COLUMN_RECIPE_NAME),
                            recipe.getName())));

    // Verify that the DELETE statement for ingredients was called once
    verify(postgresDataAccess, times(1))
        .upsertStatement(
            contains("DELETE FROM recipe_ingredients"),
            argThat(
                params ->
                    Objects.equals(
                        params.get(PostgresSqlStatementRecipe.INPUT_INGREDIENT_RECIPE_ID), 1L)));

    // Verify that the INSERT statement for each ingredient was called correctly
    for (RecipeIngredient ingredient : recipe.getIngredients()) {
      verify(postgresDataAccess, times(1))
          .queryStatement(
              contains("INSERT INTO recipe_ingredients"),
              argThat(
                  params ->
                      Objects.equals(params.get("recipe_id"), 1L)
                          && Objects.equals(params.get("name"), ingredient.getName())
                          && Objects.equals(params.get("quantity"), ingredient.getQuantity())
                          && Objects.equals(params.get("uom"), ingredient.getUom())));
    }

    // Verify that the DELETE statement for cooking steps was called once
    verify(postgresDataAccess, times(1))
        .upsertStatement(
            contains("DELETE FROM recipe_cooking_step"),
            argThat(
                params ->
                    Objects.equals(
                        params.get(PostgresSqlStatementRecipe.INPUT_COOKING_STEP_RECIPE_ID), 1L)));

    // Verify that the INSERT statement for each cooking step was called correctly
    for (CookingStep step : recipe.getCookingSteps()) {
      verify(postgresDataAccess, times(1))
          .queryStatement(
              contains("INSERT INTO recipe_cooking_step"),
              argThat(
                  params ->
                      Objects.equals(params.get("recipe_id"), 1L)
                          && Objects.equals(params.get("description"), step.getDescription())
                          && Objects.equals(params.get("image"), step.getImage())));
    }
  }

  /**
   * Test the failure case of updating a recipe when the recipe object is invalid (missing required
   * fields). Verify that the method throws an IllegalArgumentException.
   */
  @Test
  void updateRecipe_InvalidRecipe() {
    // Arrange: Create an invalid recipe object (missing required fields)
    Recipe recipe = new Recipe(); // No fields set

    // Act & Assert: Expect an IllegalArgumentException to be thrown
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              recipeDataAccess.updateRecipe(recipe);
            });

    assertEquals("Required fields for recipe cannot be null", exception.getMessage());
  }

  /**
   * Test the failure case of updating a recipe when no rows are updated in the database. Verify
   * that the method should return false and that the related statements are executed.
   */
  @Test
  void updateRecipe_NoRowsUpdated() {
    // Arrange: Create a sample recipe object
    Recipe recipe = createSampleRecipe();
    recipe.setId(1L); // Set the ID for the recipe to be updated

    // Mock the behavior of the database access methods to return 0 updated rows
    when(postgresDataAccess.upsertStatement(anyString(), anyMap())).thenReturn(0);

    // Act: Call the method to update the recipe
    boolean result = recipeDataAccess.updateRecipe(recipe);

    // Assert: Verify that the update was not successful
    assertFalse(result);
  }

  /**
   * Test the failure case of updating a recipe when a database error occurs during the operation.
   * Verify that the method should throw a RuntimeException with the appropriate message.
   */
  @Test
  void updateRecipe_DatabaseError() {
    // Arrange: Create a sample recipe object
    Recipe recipe = createSampleRecipe();
    recipe.setId(1L); // Set the ID for the recipe to be updated

    // Mock the behavior of the database access methods to throw an exception
    when(postgresDataAccess.upsertStatement(anyString(), anyMap()))
        .thenThrow(new RuntimeException("Database error"));

    // Act & Assert: Expect a RuntimeException to be thrown
    Exception exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              recipeDataAccess.updateRecipe(recipe);
            });

    assertEquals("Database error", exception.getMessage());
  }

  /**
   * Test successful deletion of a recipe by ID. Verify that the delete operation is executed as
   * expected and check the related DELETE statements.
   */
  @Test
  void deleteRecipeById_Success() {
    // Arrange: Set the recipe ID
    Long recipeId = 1L;
    // Mock that the delete operation returns 1 indicating success
    when(postgresDataAccess.upsertStatement(anyString(), anyMap())).thenReturn(1);

    // Act: Call the method to delete the recipe
    boolean result = recipeDataAccess.deleteRecipeById(recipeId);

    // Assert: Verify that the deletion was successful
    assertTrue(result);

    // Verify that the DELETE statement for ingredients was called once
    verify(postgresDataAccess, times(1))
        .upsertStatement(
            contains("DELETE FROM recipe_ingredients"),
            argThat(params -> Objects.equals(params.get("recipe_id"), recipeId)));
    // Verify that the DELETE statement for cooking steps was called once
    verify(postgresDataAccess, times(1))
        .upsertStatement(
            contains("DELETE FROM recipe_cooking_step"),
            argThat(params -> Objects.equals(params.get("recipe_id"), recipeId)));
    // Verify that the DELETE statement for the recipe was called once
    verify(postgresDataAccess, times(1))
        .upsertStatement(
            contains("DELETE FROM recipe"),
            argThat(params -> Objects.equals(params.get("id"), recipeId)));
  }

  /**
   * Test the failure case of deleting a recipe by ID when the DELETE statement does not affect any
   * rows. Verify that the method should return false and that all related DELETE statements are
   * executed.
   */
  @Test
  void deleteRecipeById_Failure_NoRowsDeleted() {
    // Arrange: Set the recipe ID
    Long recipeId = 1L;
    // Mock that deleting the recipe returns 0 indicating no rows were deleted
    when(postgresDataAccess.upsertStatement(contains("DELETE FROM recipe"), anyMap()))
        .thenReturn(0);

    // Act: Call the method to delete the recipe
    boolean result = recipeDataAccess.deleteRecipeById(recipeId);

    // Assert: Verify that the deletion failed
    assertFalse(result);

    // Verify that all related DELETE statements were called once
    verify(postgresDataAccess, times(1))
        .upsertStatement(
            contains("DELETE FROM recipe_ingredients"),
            argThat(params -> Objects.equals(params.get("recipe_id"), recipeId)));
    verify(postgresDataAccess, times(1))
        .upsertStatement(
            contains("DELETE FROM recipe_cooking_step"),
            argThat(params -> Objects.equals(params.get("recipe_id"), recipeId)));
    verify(postgresDataAccess, times(1))
        .upsertStatement(
            contains("DELETE FROM recipe"),
            argThat(params -> Objects.equals(params.get("id"), recipeId)));
  }

  /**
   * Test that an exception is thrown when deleting a recipe and an exception occurs while deleting
   * the recipe steps. Verify that the method should throw the corresponding exception and log the
   * error.
   */
  @Test
  void deleteRecipeById_DeleteRecipe_DatabaseException_ThrowsException() {
    // Arrange: Set the recipe ID
    Long recipeId = 1L;

    // Mock that deleting ingredients executes normally
    when(postgresDataAccess.upsertStatement(
            eq("DELETE FROM recipe_ingredients WHERE recipe_id = :recipe_id"),
            argThat(params -> Objects.equals(params.get("recipe_id"), recipeId))))
        .thenReturn(1);

    // Mock that deleting cooking steps executes normally
    when(postgresDataAccess.upsertStatement(
            eq("DELETE FROM recipe_cooking_step WHERE recipe_id = :recipe_id"),
            argThat(params -> Objects.equals(params.get("recipe_id"), recipeId))))
        .thenReturn(1);

    // Mock that an exception is thrown when deleting the recipe
    when(postgresDataAccess.upsertStatement(
            eq("DELETE FROM recipe WHERE id = :id"),
            argThat(params -> Objects.equals(params.get("id"), recipeId))))
        .thenThrow(new RuntimeException("Database error on deleting recipe"));

    // Act & Assert: Calling the method to delete the recipe should throw an exception
    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> recipeDataAccess.deleteRecipeById(recipeId));

    // Verify that the exception message is correct
    assertEquals("Database error on deleting recipe", exception.getMessage());

    // Verify that the SQL statement to delete ingredients was called once
    verify(postgresDataAccess, times(1))
        .upsertStatement(
            eq("DELETE FROM recipe_ingredients WHERE recipe_id = :recipe_id"),
            argThat(params -> Objects.equals(params.get("recipe_id"), recipeId)));
    // Verify that the SQL statement to delete cooking steps was called once
    verify(postgresDataAccess, times(1))
        .upsertStatement(
            eq("DELETE FROM recipe_cooking_step WHERE recipe_id = :recipe_id"),
            argThat(params -> Objects.equals(params.get("recipe_id"), recipeId)));

    // Verify that the SQL statement to delete the recipe was called once
    verify(postgresDataAccess, times(1))
        .upsertStatement(
            eq("DELETE FROM recipe WHERE id = :id"),
            argThat(params -> Objects.equals(params.get("id"), recipeId)));
  }

  /**
   * Test getting a recipe by ID when the recipe exists. Verify that the query operation is executed
   * as expected and that the recipe object is correctly assembled.
   */
  @Test
  void getRecipeById_Found() {
    // Arrange: Set the recipe ID and mock the returned recipe data
    Long recipeId = 1L;
    Map<String, Object> recipeRow = createSampleRecipeMap();
    List<Map<String, Object>> recipeResult = Collections.singletonList(recipeRow);
    when(postgresDataAccess.queryStatement(contains("SELECT * FROM recipe WHERE id"), anyMap()))
        .thenReturn(recipeResult);
    // Mock the query for ingredients and cooking steps data
    when(postgresDataAccess.queryStatement(contains("SELECT * FROM recipe_ingredients"), anyMap()))
        .thenReturn(Collections.singletonList(createSampleIngredientMap()));
    when(postgresDataAccess.queryStatement(contains("SELECT * FROM recipe_cooking_step"), anyMap()))
        .thenReturn(Collections.singletonList(createSampleCookingStepMap()));

    // Act: Call the method to get the recipe
    Recipe recipe = recipeDataAccess.getRecipeById(recipeId);

    // Assert: Verify that the recipe object is correctly assembled
    assertNotNull(recipe);
    assertEquals(1L, recipe.getId());
    assertEquals("Sample Recipe", recipe.getName());
    assertEquals(1, recipe.getIngredients().size());
    assertEquals(1, recipe.getCookingSteps().size());

    // Verify that the SELECT query was called correctly
    verify(postgresDataAccess, times(1))
        .queryStatement(
            contains("SELECT * FROM recipe WHERE id"),
            argThat(params -> Objects.equals(params.get("id"), recipeId)));
    verify(postgresDataAccess, times(1))
        .queryStatement(
            contains("SELECT * FROM recipe_ingredients"),
            argThat(params -> Objects.equals(params.get("recipe_id"), recipeId)));
    verify(postgresDataAccess, times(1))
        .queryStatement(
            contains("SELECT * FROM recipe_cooking_step"),
            argThat(params -> Objects.equals(params.get("recipe_id"), recipeId)));
  }

  /**
   * Test getting a recipe by ID when the recipe does not exist. Verify that the method should
   * return null and that there are no queries for ingredients and cooking steps.
   */
  @Test
  void getRecipeById_NotFound() {
    // Arrange: Set the recipe ID and mock that the query returns no results
    Long recipeId = 1L;
    when(postgresDataAccess.queryStatement(contains("SELECT * FROM recipe WHERE id"), anyMap()))
        .thenReturn(Collections.emptyList());

    // Act: Call the method to get the recipe
    Recipe recipe = recipeDataAccess.getRecipeById(recipeId);

    // Assert: Verify that the method returns null
    assertNull(recipe);

    // Verify that only the SELECT query for the recipe was executed, with no queries for
    // ingredients and cooking steps
    verify(postgresDataAccess, times(1))
        .queryStatement(
            contains("SELECT * FROM recipe WHERE id"),
            argThat(params -> Objects.equals(params.get("id"), recipeId)));
    verify(postgresDataAccess, never())
        .queryStatement(contains("SELECT * FROM recipe_ingredients"), anyMap());
    verify(postgresDataAccess, never())
        .queryStatement(contains("SELECT * FROM recipe_cooking_step"), anyMap());
  }

  /**
   * Test that an exception is thrown when getting recipes by name and the database throws an
   * exception. Verify that the method should throw the corresponding exception and log the error.
   */
  @Test
  void getRecipesByName_DatabaseException_ThrowsException() {
    // Arrange: Set the search name
    String name = "Sample";
    // Mock that an exception is thrown when querying recipes
    when(postgresDataAccess.queryStatement(
            contains("SELECT * FROM recipe WHERE name ILIKE"), anyMap()))
        .thenThrow(new RuntimeException("Database query error"));

    // Act & Assert: Calling the method to get recipes by name should throw an exception
    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> recipeDataAccess.getRecipesByName(name));

    // Verify that the exception message is correct
    assertEquals("Database query error", exception.getMessage());

    // Verify that the SELECT statement for querying recipes was called
    verify(postgresDataAccess, times(1))
        .queryStatement(
            contains("SELECT * FROM recipe WHERE name ILIKE"),
            argThat(params -> Objects.equals(params.get("name"), "%" + name + "%")));
    // Verify that there are no queries for ingredients and cooking steps
    verify(postgresDataAccess, never())
        .queryStatement(contains("SELECT * FROM recipe_ingredients"), anyMap());
    verify(postgresDataAccess, never())
        .queryStatement(contains("SELECT * FROM recipe_cooking_step"), anyMap());
  }

  /**
   * Test getting all recipes. Verify that the query operation is executed as expected and that the
   * recipe list is correctly assembled.
   */
  @Test
  void getAllRecipes() {
    // Arrange: Mock the returned data for all recipes
    Map<String, Object> recipeRow = createSampleRecipeMap();
    List<Map<String, Object>> recipeResult = Collections.singletonList(recipeRow);
    when(postgresDataAccess.queryStatement(contains("SELECT * FROM recipe"), isNull()))
        .thenReturn(recipeResult);
    // Mock the query for ingredients and cooking steps data
    when(postgresDataAccess.queryStatement(contains("SELECT * FROM recipe_ingredients"), anyMap()))
        .thenReturn(Collections.singletonList(createSampleIngredientMap()));
    when(postgresDataAccess.queryStatement(contains("SELECT * FROM recipe_cooking_step"), anyMap()))
        .thenReturn(Collections.singletonList(createSampleCookingStepMap()));

    // Act: Call the method to get all recipes
    List<Recipe> recipes = recipeDataAccess.getAllRecipes();

    // Assert: Verify that the recipe list is correctly assembled
    assertNotNull(recipes);
    assertEquals(1, recipes.size());
    Recipe recipe = recipes.getFirst();
    assertEquals(1L, recipe.getId());
    assertEquals("Sample Recipe", recipe.getName());
    assertEquals(1, recipe.getIngredients().size());
    assertEquals(1, recipe.getCookingSteps().size());

    // Verify that the SELECT query was called correctly
    verify(postgresDataAccess, times(1)).queryStatement(contains("SELECT * FROM recipe"), isNull());
    verify(postgresDataAccess, times(1))
        .queryStatement(
            contains("SELECT * FROM recipe_ingredients"),
            argThat(params -> Objects.equals(params.get("recipe_id"), 1L)));
    verify(postgresDataAccess, times(1))
        .queryStatement(
            contains("SELECT * FROM recipe_cooking_step"),
            argThat(params -> Objects.equals(params.get("recipe_id"), 1L)));
  }

  /**
   * Test getting recipes by name when the recipe exists. Verify that the query operation is
   * executed as expected and that the recipe list is correctly assembled.
   */
  @Test
  void getRecipesByName_Found() {
    // Arrange: Set the search name and mock the returned recipe data
    String name = "Sample";
    Map<String, Object> recipeRow = createSampleRecipeMap();
    List<Map<String, Object>> recipeResult = Collections.singletonList(recipeRow);
    when(postgresDataAccess.queryStatement(
            contains("SELECT * FROM recipe WHERE name ILIKE"), anyMap()))
        .thenReturn(recipeResult);
    // Mock the query for ingredients and cooking steps data
    when(postgresDataAccess.queryStatement(contains("SELECT * FROM recipe_ingredients"), anyMap()))
        .thenReturn(Collections.singletonList(createSampleIngredientMap()));
    when(postgresDataAccess.queryStatement(contains("SELECT * FROM recipe_cooking_step"), anyMap()))
        .thenReturn(Collections.singletonList(createSampleCookingStepMap()));

    // Act: Call the method to get recipes by name
    List<Recipe> recipes = recipeDataAccess.getRecipesByName(name);

    // Assert: Verify that the recipe list is correctly assembled
    assertNotNull(recipes);
    assertEquals(1, recipes.size());
    Recipe recipe = recipes.getFirst();
    assertEquals("Sample Recipe", recipe.getName());
    assertEquals(1, recipe.getIngredients().size());
    assertEquals(1, recipe.getCookingSteps().size());

    // Verify that the SELECT query was called correctly
    verify(postgresDataAccess, times(1))
        .queryStatement(
            contains("SELECT * FROM recipe WHERE name ILIKE"),
            argThat(params -> Objects.equals(params.get("name"), "%" + name + "%")));
    verify(postgresDataAccess, times(1))
        .queryStatement(
            contains("SELECT * FROM recipe_ingredients"),
            argThat(params -> Objects.equals(params.get("recipe_id"), 1L)));
    verify(postgresDataAccess, times(1))
        .queryStatement(
            contains("SELECT * FROM recipe_cooking_step"),
            argThat(params -> Objects.equals(params.get("recipe_id"), 1L)));
  }

  /**
   * Test getting recipes by name when the recipe does not exist. Verify that the method should
   * return an empty list and that there are no queries for ingredients and cooking steps.
   */
  @Test
  void getRecipesByName_NotFound() {
    // Arrange: Set the search name and mock that the query returns no results
    String name = "Nonexistent";
    when(postgresDataAccess.queryStatement(
            contains("SELECT * FROM recipe WHERE name ILIKE"), anyMap()))
        .thenReturn(Collections.emptyList());

    // Act: Call the method to get recipes by name
    List<Recipe> recipes = recipeDataAccess.getRecipesByName(name);

    // Assert: Verify that the method returns an empty list
    assertNotNull(recipes);
    assertTrue(recipes.isEmpty());

    // Verify that only the SELECT query for recipes was executed, with no queries for ingredients
    // and cooking steps
    verify(postgresDataAccess, times(1))
        .queryStatement(
            contains("SELECT * FROM recipe WHERE name ILIKE"),
            argThat(params -> Objects.equals(params.get("name"), "%" + name + "%")));
    verify(postgresDataAccess, never())
        .queryStatement(contains("SELECT * FROM recipe_ingredients"), anyMap());
    verify(postgresDataAccess, never())
        .queryStatement(contains("SELECT * FROM recipe_cooking_step"), anyMap());
  }

  /** Helper method: Create a sample recipe object for testing */
  private Recipe createSampleRecipe() {
    Recipe recipe = new Recipe();
    recipe.setCreatorId(1L);
    recipe.setName("Sample Recipe");
    recipe.setImage("sample.jpg");
    recipe.setDescription("A sample recipe description.");
    recipe.setCookingTimeInSec(3600);
    recipe.setDifficultyLevel(2);
    recipe.setRating(4.5);
    recipe.setStatus(ERecipeStatus.DRAFT);
    recipe.setCreateDatetime(Timestamp.valueOf(java.time.LocalDateTime.now()));
    recipe.setUpdateDatetime(Timestamp.valueOf(java.time.LocalDateTime.now()));
    recipe.setCuisine("Chinese");

    RecipeIngredient ingredient = createIngredient("Sugar", 100.0, "grams");
    recipe.setIngredients(Collections.singletonList(ingredient));

    CookingStep step = createCookingStep("Mix ingredients.", "step1.jpg");
    recipe.setCookingSteps(Collections.singletonList(step));

    return recipe;
  }

  /** Helper method: Create a sample ingredient object for testing */
  private RecipeIngredient createIngredient(String name, double quantity, String uom) {
    RecipeIngredient ingredient = new RecipeIngredient();
    ingredient.setName(name);
    ingredient.setQuantity(quantity);
    ingredient.setUom(uom);
    return ingredient;
  }

  /** Helper method: Create a sample cooking step object for testing */
  private CookingStep createCookingStep(String description, String image) {
    CookingStep step = new CookingStep();
    step.setDescription(description);
    step.setImage(image);
    return step;
  }

  /**
   * Helper method: Create a sample recipe map representation for simulating database query results
   */
  private Map<String, Object> createSampleRecipeMap() {
    Map<String, Object> map = new HashMap<>();
    map.put(PostgresSqlStatementRecipe.COLUMN_RECIPE_ID, 1L);
    map.put(PostgresSqlStatementRecipe.COLUMN_RECIPE_CREATOR_ID, 1L);
    map.put(PostgresSqlStatementRecipe.COLUMN_RECIPE_NAME, "Sample Recipe");
    map.put(PostgresSqlStatementRecipe.COLUMN_RECIPE_IMAGE, "sample.jpg");
    map.put(PostgresSqlStatementRecipe.COLUMN_RECIPE_DESCRIPTION, "A sample recipe description.");
    map.put(PostgresSqlStatementRecipe.COLUMN_RECIPE_COOKING_TIME, 3600);
    map.put(PostgresSqlStatementRecipe.COLUMN_RECIPE_DIFFICULTY_LEVEL, 2);
    map.put(PostgresSqlStatementRecipe.COLUMN_RECIPE_RATING, 4.5);
    map.put(PostgresSqlStatementRecipe.COLUMN_RECIPE_STATUS, 1);
    map.put(
        PostgresSqlStatementRecipe.COLUMN_RECIPE_CREATE_TIME,
        new Timestamp(System.currentTimeMillis()));
    map.put(
        PostgresSqlStatementRecipe.COLUMN_RECIPE_UPDATE_TIME,
        new Timestamp(System.currentTimeMillis()));
    return map;
  }

  /**
   * Helper method: Create a sample ingredient map representation for simulating database query
   * results
   */
  private Map<String, Object> createSampleIngredientMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("id", 1L);
    map.put("recipe_id", 1L);
    map.put("name", "Sugar");
    map.put("quantity", 100.0);
    map.put("uom", "grams");
    return map;
  }

  /**
   * Helper method: Create a sample cooking step map representation for simulating database query
   * results
   */
  private Map<String, Object> createSampleCookingStepMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("id", 1);
    map.put("recipe_id", 1L);
    map.put("description", "Mix ingredients.");
    map.put("image", "step1.jpg");
    return map;
  }

  /**
   * Helper method: Create a sample recipe object with multiple ingredients and steps for testing
   */
  private Recipe createSampleRecipeWithMultipleIngredientsAndSteps() {
    Recipe recipe = new Recipe();
    recipe.setCreatorId(1L);
    recipe.setName("Sample Recipe");
    recipe.setImage("sample.jpg");
    recipe.setDescription("A sample recipe description.");
    recipe.setCookingTimeInSec(3600);
    recipe.setDifficultyLevel(2);
    recipe.setRating(4.5);
    recipe.setStatus(ERecipeStatus.DRAFT);
    recipe.setCuisine("Chinese");
    recipe.setCreateDatetime(Timestamp.valueOf(java.time.LocalDateTime.now()));
    recipe.setUpdateDatetime(Timestamp.valueOf(java.time.LocalDateTime.now()));

    List<RecipeIngredient> ingredients =
        Arrays.asList(
            createIngredient("Sugar", 100.0, "grams"), createIngredient("Flour", 200.0, "grams"));
    recipe.setIngredients(ingredients);

    List<CookingStep> steps =
        Arrays.asList(
            createCookingStep("Mix sugar and flour.", "step1.jpg"),
            createCookingStep("Bake the mixture.", "step2.jpg"));
    recipe.setCookingSteps(steps);

    return recipe;
  }
}
