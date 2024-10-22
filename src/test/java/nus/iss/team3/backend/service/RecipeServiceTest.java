package nus.iss.team3.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import nus.iss.team3.backend.dataaccess.IRecipeDataAccess;
import nus.iss.team3.backend.entity.Recipe;
import nus.iss.team3.backend.service.recipe.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test class: RecipeServiceTest is used to test the methods of the RecipeService class to
 * ensure its behavior meets expectations.
 */
class RecipeServiceTest {

  private IRecipeDataAccess mockDataAccess; // Mocked data access layer
  private RecipeService recipeService; // Service class under test

  /**
   * Initializes the test environment by creating new mock objects and RecipeService instance before
   * each test.
   */
  @BeforeEach
  void setUp() {
    mockDataAccess = mock(IRecipeDataAccess.class);
    recipeService = new RecipeService(mockDataAccess);
  }

  /**
   * Test successfully adding a valid recipe Verify that the service layer calls the data access
   * layer's addRecipe method and returns the expected result
   */
  @Test
  void addRecipe_Success() {
    // Arrange: Create a valid recipe object
    Recipe recipe = new Recipe();
    recipe.setName("Test Recipe");
    recipe.setCookingTimeInSec(300); // Set cooking time in seconds
    recipe.setDifficultyLevel(2); // Set difficulty level (e.g., 1 to 5)

    // Mock the data access layer to return true
    when(mockDataAccess.addRecipe(recipe)).thenReturn(true);

    // Act: Call the service layer's addRecipe method
    boolean result = recipeService.addRecipe(recipe);

    // Assert: Verify the result is true and the data access layer was called once
    assertTrue(result);
    verify(mockDataAccess, times(1)).addRecipe(recipe);
  }

  /**
   * Test adding a recipe with a null object Expect the service layer to throw
   * IllegalArgumentException
   */
  @Test
  void addRecipe_NullRecipe_ThrowsException() {
    // Act & Assert: Call addRecipe method with null, should throw IllegalArgumentException
    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> recipeService.addRecipe(null));

    // Verify the exception message is correct
    assertEquals("Recipe cannot be null", exception.getMessage());

    // Verify the data access layer's addRecipe method was not called
    verify(mockDataAccess, never()).addRecipe(any());
  }

  /**
   * Test adding a recipe object without a name Expect the service layer to throw
   * IllegalArgumentException
   */
  @Test
  void addRecipe_EmptyName_ThrowsException() {
    // Arrange: Create a recipe object but do not set the name
    Recipe recipe = new Recipe();

    // Act & Assert: Call addRecipe method, should throw IllegalArgumentException
    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> recipeService.addRecipe(recipe));

    // Verify the exception message is correct
    assertEquals("Recipe name cannot be empty", exception.getMessage());

    // Verify the data access layer's addRecipe method was not called
    verify(mockDataAccess, never()).addRecipe(any());
  }

  /**
   * Test successfully updating a valid recipe Verify that the service layer calls the data access
   * layer's updateRecipe method and returns the expected result
   */
  @Test
  void updateRecipe_Success() {
    // Arrange: Create a valid recipe object and set the ID
    Recipe recipe = new Recipe();
    recipe.setId(1L); // Set the ID
    recipe.setName("Updated Recipe");
    recipe.setCookingTimeInSec(300); // Set cooking time in seconds
    recipe.setDifficultyLevel(2); // Set difficulty level (e.g., 1 to 5)

    // Create an existing recipe object that is different from the new recipe
    Recipe existingRecipe = new Recipe();
    existingRecipe.setId(1L); // Same ID
    existingRecipe.setName("Old Recipe");
    existingRecipe.setCookingTimeInSec(600); // Different cooking time
    existingRecipe.setDifficultyLevel(3); // Different difficulty level

    // Mock the data access layer to return the existing recipe when fetched
    when(mockDataAccess.getRecipeById(recipe.getId())).thenReturn(existingRecipe);
    // Mock the data access layer to return true for the update
    when(mockDataAccess.updateRecipe(recipe)).thenReturn(true);

    // Act: Call the service layer's updateRecipe method
    boolean result = recipeService.updateRecipe(recipe);

    // Assert: Verify the result is true and the data access layer was called once
    assertTrue(result);
    verify(mockDataAccess, times(1))
        .getRecipeById(recipe.getId()); // Verify fetching existing recipe
    verify(mockDataAccess, times(1)).updateRecipe(recipe); // Verify update was called
  }

  /**
   * Test updating a recipe with a null object Expect the service layer to throw
   * IllegalArgumentException
   */
  @Test
  void updateRecipe_NullRecipe_ThrowsException() {
    // Act & Assert: Call updateRecipe method with null, should throw IllegalArgumentException
    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> recipeService.updateRecipe(null));

    // Verify the exception message is correct
    assertEquals("Recipe cannot be null", exception.getMessage());

    // Verify the data access layer's updateRecipe method was not called
    verify(mockDataAccess, never()).updateRecipe(any());
  }

  /**
   * Test updating a recipe object without an ID Expect the service layer to throw
   * IllegalArgumentException
   */
  @Test
  void updateRecipe_NullId_ThrowsException() {
    // Arrange: Create a recipe object but do not set the ID
    Recipe recipe = new Recipe();
    recipe.setName("Recipe without ID");

    // Act & Assert: Call updateRecipe method, should throw IllegalArgumentException
    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> recipeService.updateRecipe(recipe));

    // Verify the exception message is correct
    assertEquals("Recipe ID cannot be null", exception.getMessage());

    // Verify the data access layer's updateRecipe method was not called
    verify(mockDataAccess, never()).updateRecipe(any());
  }

  /**
   * Test updating a recipe object without a name Expect the service layer to throw
   * IllegalArgumentException
   */
  @Test
  void updateRecipe_EmptyName_ThrowsException() {
    // Arrange: Create a recipe object, set ID but do not set name
    Recipe recipe = new Recipe();
    recipe.setId(1L);

    // Act & Assert: Call updateRecipe method, should throw IllegalArgumentException
    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> recipeService.updateRecipe(recipe));

    // Verify the exception message is correct
    assertEquals("Recipe name cannot be empty", exception.getMessage());

    // Verify the data access layer's updateRecipe method was not called
    verify(mockDataAccess, never()).updateRecipe(any());
  }

  /**
   * Test successfully deleting a recipe by ID Verify that the service layer calls the data access
   * layer's deleteRecipeById method and returns the expected result
   */
  @Test
  void deleteRecipeById_Success() {
    // Arrange: Set the recipe ID
    Long recipeId = 1L;

    // Create an existing recipe object that is different from the new recipe
    Recipe existingRecipe = new Recipe();
    existingRecipe.setId(1L); // Same ID
    existingRecipe.setName("Old Recipe");
    existingRecipe.setCookingTimeInSec(600); // Different cooking time
    existingRecipe.setDifficultyLevel(3); // Different difficulty level

    // Mock the data access layer to return the existing recipe when fetched
    when(mockDataAccess.getRecipeById(anyLong())).thenReturn(existingRecipe);
    // Mock the data access layer to return true
    when(mockDataAccess.deleteRecipeById(recipeId)).thenReturn(true);

    // Act: Call the service layer's deleteRecipeById method
    boolean result = recipeService.deleteRecipeById(recipeId);

    // Assert: Verify the result is true and the data access layer was called once
    assertTrue(result);
    verify(mockDataAccess, times(1)).deleteRecipeById(recipeId);
  }

  /**
   * Test deleting a recipe with a null ID Expect the service layer to throw NullPointerException
   */
  @Test
  void deleteRecipeById_NullId_ThrowsException() {
    // Act & Assert: Call deleteRecipeById method with null, should throw NullPointerException
    Exception exception =
        assertThrows(NullPointerException.class, () -> recipeService.deleteRecipeById(null));

    // Verify the exception message is correct
    assertEquals("Recipe ID cannot be null", exception.getMessage());

    // Verify the data access layer's deleteRecipeById method was not called
    verify(mockDataAccess, never()).deleteRecipeById(any());
  }

  /**
   * Test successfully retrieving a recipe by ID Verify that the service layer calls the data access
   * layer's getRecipeById method and returns the expected result
   */
  @Test
  void getRecipeById_Found() {
    // Arrange: Set the recipe ID and mock the returned recipe object
    Long recipeId = 1L;
    Recipe recipe = new Recipe();
    recipe.setId(recipeId);
    recipe.setName("Existing Recipe");

    when(mockDataAccess.getRecipeById(recipeId)).thenReturn(recipe);

    // Act: Call the service layer's getRecipeById method
    Recipe result = recipeService.getRecipeById(recipeId);

    // Assert: Verify the returned recipe object matches the expected one and the data access layer
    // was called once
    assertNotNull(result);
    assertEquals(recipeId, result.getId());
    assertEquals("Existing Recipe", result.getName());
    verify(mockDataAccess, times(1)).getRecipeById(recipeId);
  }

  /**
   * Test getting a recipe by ID with a null ID Expect the service layer to throw
   * NullPointerException
   */
  @Test
  void getRecipeById_NullId_ThrowsException() {
    // Act & Assert: Call getRecipeById method with null, should throw NullPointerException
    Exception exception =
        assertThrows(NullPointerException.class, () -> recipeService.getRecipeById(null));

    // Verify the exception message is correct
    assertEquals("Recipe ID cannot be null", exception.getMessage());

    // Verify the data access layer's getRecipeById method was not called
    verify(mockDataAccess, never()).getRecipeById(any());
  }

  /**
   * Test getting a recipe by ID when the recipe does not exist Verify that the service layer
   * returns null
   */
  @Test
  void getRecipeById_NotFound() {
    // Arrange: Set the recipe ID and mock the data access layer to return null
    Long recipeId = 1L;
    when(mockDataAccess.getRecipeById(recipeId)).thenReturn(null);

    // Act: Call the service layer's getRecipeById method
    Recipe result = recipeService.getRecipeById(recipeId);

    // Assert: Verify the result is null and the data access layer was called once
    assertNull(result);
    verify(mockDataAccess, times(1)).getRecipeById(recipeId);
  }

  /**
   * Test successfully retrieving all recipes Verify that the service layer calls the data access
   * layer's getAllRecipes method and returns the expected result
   */
  @Test
  void getAllRecipes_Success() {
    // Arrange: Create a list of recipes and mock the data access layer to return it
    Recipe recipe1 = new Recipe();
    recipe1.setId(1L);
    recipe1.setName("Recipe One");

    Recipe recipe2 = new Recipe();
    recipe2.setId(2L);
    recipe2.setName("Recipe Two");

    List<Recipe> recipes = List.of(recipe1, recipe2);
    when(mockDataAccess.getAllRecipes()).thenReturn(recipes);

    // Act: Call the service layer's getAllRecipes method
    List<Recipe> result = recipeService.getAllRecipes();

    // Assert: Verify the returned list matches the expected one and the data access layer was
    // called once
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(mockDataAccess, times(1)).getAllRecipes();
  }

  /**
   * Test when the data access layer returns an empty list while retrieving all recipes Verify that
   * the service layer returns an empty list
   */
  @Test
  void getAllRecipes_EmptyList() {
    // Arrange: Mock the data access layer to return an empty list
    when(mockDataAccess.getAllRecipes()).thenReturn(Collections.emptyList());

    // Act: Call the service layer's getAllRecipes method
    List<Recipe> result = recipeService.getAllRecipes();

    // Assert: Verify the returned list is empty and the data access layer was called once
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(mockDataAccess, times(1)).getAllRecipes();
  }

  /**
   * Test getting recipes by name when the name is non-empty and there are matching results Verify
   * that the service layer calls the data access layer's getRecipesByName method and returns the
   * expected result
   */
  @Test
  void getRecipesByName_Found() {
    // Arrange: Set the search name and mock the data access layer to return a list of recipes
    String name = "Chicken";
    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setName("Chicken Curry");

    when(mockDataAccess.getRecipesByName(name)).thenReturn(List.of(recipe));

    // Act: Call the service layer's getRecipesByName method
    List<Recipe> result = recipeService.getRecipesByName(name);

    // Assert: Verify the returned list contains the expected recipe and the data access layer was
    // called once
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Chicken Curry", result.getFirst().getName());
    verify(mockDataAccess, times(1)).getRecipesByName(name);
  }

  /**
   * Test getting recipes by name when the name is non-empty but there are no matching results
   * Verify that the service layer returns an empty list
   */
  @Test
  void getRecipesByName_NotFound() {
    // Arrange: Set the search name and mock the data access layer to return an empty list
    String name = "Beef";
    when(mockDataAccess.getRecipesByName(name)).thenReturn(Collections.emptyList());

    // Act: Call the service layer's getRecipesByName method
    List<Recipe> result = recipeService.getRecipesByName(name);

    // Assert: Verify the returned list is empty and the data access layer was called once
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(mockDataAccess, times(1)).getRecipesByName(name);
  }

  /**
   * Test getting recipes by name when an empty string is passed Verify that the service layer
   * returns an empty list and the data access layer is not called
   */
  @Test
  void getRecipesByName_EmptyName_ReturnsEmptyList() {
    // Arrange: Set an empty string as the search name
    String name = "   ";

    // Act: Call the service layer's getRecipesByName method
    List<Recipe> result = recipeService.getRecipesByName(name);

    // Assert: Verify the returned list is empty and the data access layer's getRecipesByName method
    // was not called
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(mockDataAccess, never()).getRecipesByName(anyString());
  }

  /**
   * Test getting recipes by name when null is passed Verify that the service layer returns an empty
   * list and the data access layer is not called
   */
  @Test
  void getRecipesByName_NullName_ReturnsEmptyList() {
    // Act: Call the service layer's getRecipesByName method with null
    List<Recipe> result = recipeService.getRecipesByName(null);

    // Assert: Verify the returned list is empty and the data access layer's getRecipesByName method
    // was not called
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(mockDataAccess, never()).getRecipesByName(anyString());
  }
}
