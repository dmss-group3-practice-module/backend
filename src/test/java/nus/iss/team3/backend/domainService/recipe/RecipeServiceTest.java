package nus.iss.team3.backend.domainService.recipe;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import nus.iss.team3.backend.dataaccess.IRecipeDataAccess;
import nus.iss.team3.backend.domainService.recipe.status.IRecipeStateContext;
import nus.iss.team3.backend.entity.ERecipeStatus;
import nus.iss.team3.backend.entity.Recipe;
import nus.iss.team3.backend.entity.RecipeReview;
import nus.iss.team3.backend.entity.RecipeWithReviews;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test class: RecipeServiceTest is used to test the methods of the RecipeService class to
 * ensure its behavior meets expectations.
 *
 * @author Mao Weining
 */
class RecipeServiceTest {
  private IRecipeStateContext mockRecipeContext; // Mocked data access layer

  private IRecipeDataAccess mockDataAccess; // Mocked data access layer
  private RecipeService recipeService; // Service class under test

  private Recipe sampleRecipe; // Sample recipe object for testing
  private RecipeWithReviews sampleRecipeWithReviews; // Sample RecipeWithReviews object for testing

  /**
   * Initializes the test environment by creating new mock objects and RecipeService instance before
   * each test.
   */
  @BeforeEach
  void setUp() {
    mockDataAccess = mock(IRecipeDataAccess.class);
    mockRecipeContext = mock(IRecipeStateContext.class);
    recipeService = new RecipeService(mockDataAccess, mockRecipeContext);

    // Initialize sample Recipe object
    sampleRecipe = new Recipe();
    sampleRecipe.setId(1L);
    sampleRecipe.setName("Sample Recipe");

    // Initialize sample RecipeWithReviews object
    RecipeReview review = new RecipeReview();
    review.setRecipeId(sampleRecipe.getId());
    review.setComments("Excellent recipe!");
    sampleRecipeWithReviews = new RecipeWithReviews(sampleRecipe, List.of(review));

    recipeService.postConstruct();
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
    recipe.setCookingTimeInMin(300); // Set cooking time in seconds
    recipe.setDifficultyLevel(2); // Set difficulty level (e.g., 1 to 5)
    Recipe returnRecipe = new Recipe();
    returnRecipe.setId(1L);
    returnRecipe.setName("Test Recipe");
    returnRecipe.setCookingTimeInMin(300); // Set cooking time in seconds

    returnRecipe.setDifficultyLevel(2); // Set difficulty level (e.g., 1 to 5)

    // Mock the data access layer to return true
    when(mockRecipeContext.addRecipe(recipe)).thenReturn(returnRecipe);

    // Act: Call the service layer's addRecipe method
    boolean result = recipeService.addRecipe(recipe);

    // Assert: Verify the result is true and the data access layer was called once
    assertTrue(result);
    verify(mockRecipeContext, times(1)).addRecipe(recipe);
  }

  @Test
  void addRecipe_DifferentValue() {
    // Arrange: Create a valid recipe object
    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setName("");
    recipe.setCookingTimeInMin(300); // Set cooking time in seconds
    recipe.setDifficultyLevel(2); // Set difficulty level (e.g., 1 to 5)
    // Mock the data access layer to return true
    recipe.setName("");
    assertThrows(IllegalArgumentException.class, () -> recipeService.addRecipe(recipe));
    recipe.setName(null);
    assertThrows(IllegalArgumentException.class, () -> recipeService.addRecipe(recipe));
    recipe.setName("name");

    recipe.setCookingTimeInMin(null);
    assertThrows(IllegalArgumentException.class, () -> recipeService.addRecipe(recipe));
    recipe.setCookingTimeInMin(-1);
    assertThrows(IllegalArgumentException.class, () -> recipeService.addRecipe(recipe));
    recipe.setCookingTimeInMin(100);

    recipe.setDifficultyLevel(null);
    assertThrows(IllegalArgumentException.class, () -> recipeService.addRecipe(recipe));
    recipe.setDifficultyLevel(-1);
    assertThrows(IllegalArgumentException.class, () -> recipeService.addRecipe(recipe));
    recipe.setDifficultyLevel(6);
    assertThrows(IllegalArgumentException.class, () -> recipeService.addRecipe(recipe));
    recipe.setDifficultyLevel(2);

    recipe.setRating(1.1);

    // Mock the data access layer to return true
    when(mockRecipeContext.addRecipe(recipe)).thenReturn(recipe);

    // Act: Call the service layer's addRecipe method
    boolean result = recipeService.addRecipe(recipe);

    // Assert: Verify the result is true and the data access layer was called once
    assertTrue(result);
    verify(mockRecipeContext, times(1)).addRecipe(recipe);
  }

  @Test
  void addRecipe_NullRecipe() {
    // Arrange: Create a valid recipe object
    Recipe recipe = new Recipe();
    recipe.setName("Test Recipe");
    recipe.setCookingTimeInMin(300); // Set cooking time in seconds
    recipe.setDifficultyLevel(2); // Set difficulty level (e.g., 1 to 5)
    Recipe returnRecipe = null;

    // Mock the data access layer to return true
    when(mockRecipeContext.addRecipe(recipe)).thenReturn(returnRecipe);

    // Act: Call the service layer's addRecipe method
    boolean result = recipeService.addRecipe(recipe);

    // Assert: Verify the result is true and the data access layer was called once
    assertFalse(result);
    verify(mockRecipeContext, times(1)).addRecipe(recipe);
  }

  @Test
  void addRecipe_EmptyRecipe() {
    // Arrange: Create a valid recipe object
    Recipe recipe = new Recipe();
    recipe.setName("Test Recipe");
    recipe.setCookingTimeInMin(300); // Set cooking time in seconds
    recipe.setDifficultyLevel(2); // Set difficulty level (e.g., 1 to 5)
    Recipe returnRecipe = new Recipe();
    returnRecipe.setId(null);
    returnRecipe.setName("Test Recipe");
    returnRecipe.setCookingTimeInMin(300); // Set cooking time in seconds

    returnRecipe.setDifficultyLevel(2); // Set difficulty level (e.g., 1 to 5)

    // Mock the data access layer to return true
    when(mockRecipeContext.addRecipe(recipe)).thenReturn(returnRecipe);

    // Act: Call the service layer's addRecipe method
    boolean result = recipeService.addRecipe(recipe);

    // Assert: Verify the result is true and the data access layer was called once
    assertFalse(result);
    verify(mockRecipeContext, times(1)).addRecipe(recipe);
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
    recipe.setCookingTimeInMin(300); // Set cooking time in seconds
    recipe.setDifficultyLevel(2); // Set difficulty level (e.g., 1 to 5)

    // Create an existing recipe object that is different from the new recipe
    Recipe existingRecipe = new Recipe();
    existingRecipe.setId(1L); // Same ID
    existingRecipe.setName("Old Recipe");
    existingRecipe.setCookingTimeInMin(600); // Different cooking time
    existingRecipe.setDifficultyLevel(3); // Different difficulty level

    // Mock the data access layer to return the existing recipe when fetched
    when(mockDataAccess.getRecipeById(recipe.getId())).thenReturn(existingRecipe);
    // Mock the data access layer to return true for the update
    when(mockRecipeContext.updateRecipe(recipe)).thenReturn(true);

    // Act: Call the service layer's updateRecipe method
    boolean result = recipeService.updateRecipe(recipe);

    // Assert: Verify the result is true and the data access layer was called once
    assertTrue(result);
    verify(mockDataAccess, times(1))
        .getRecipeById(recipe.getId()); // Verify fetching existing recipe
    verify(mockRecipeContext, times(1)).updateRecipe(recipe); // Verify update was called
  }

  @Test
  void updateRecipe_Failure() {
    // Arrange: Create a valid recipe object and set the ID
    Recipe recipe = new Recipe();
    recipe.setId(1L); // Set the ID
    recipe.setName("Updated Recipe");
    recipe.setCookingTimeInMin(300); // Set cooking time in seconds
    recipe.setDifficultyLevel(2); // Set difficulty level (e.g., 1 to 5)

    // Create an existing recipe object that is different from the new recipe
    Recipe existingRecipe = new Recipe();
    existingRecipe.setId(1L); // Same ID
    existingRecipe.setName("Old Recipe");
    existingRecipe.setCookingTimeInMin(600); // Different cooking time
    existingRecipe.setDifficultyLevel(3); // Different difficulty level

    // Mock the data access layer to return the existing recipe when fetched
    when(mockDataAccess.getRecipeById(recipe.getId())).thenReturn(existingRecipe);
    // Mock the data access layer to return true for the update
    when(mockRecipeContext.updateRecipe(recipe)).thenReturn(false);

    // Act: Call the service layer's updateRecipe method
    boolean result = recipeService.updateRecipe(recipe);

    // Assert: Verify the result is true and the data access layer was called once
    assertFalse(result);
    verify(mockDataAccess, times(1))
        .getRecipeById(recipe.getId()); // Verify fetching existing recipe
    verify(mockRecipeContext, times(1)).updateRecipe(recipe); // Verify update was called
  }

  @Test
  void updateRecipe_sameRecipes() {
    // Arrange: Create a valid recipe object and set the ID
    Recipe recipe = new Recipe();
    recipe.setId(1L); // Set the ID
    recipe.setName("Same Recipe");
    recipe.setCookingTimeInMin(300); // Set cooking time in seconds
    recipe.setDifficultyLevel(2); // Set difficulty level (e.g., 1 to 5)

    // Create an existing recipe object that is different from the new recipe
    Recipe existingRecipe = new Recipe();
    existingRecipe.setId(1L); // Same ID
    existingRecipe.setName("Same Recipe");
    existingRecipe.setCookingTimeInMin(300); // Different cooking time
    existingRecipe.setDifficultyLevel(2); // Different difficulty level

    // Mock the data access layer to return the existing recipe when fetched
    when(mockDataAccess.getRecipeById(recipe.getId())).thenReturn(recipe);
    // Mock the data access layer to return true for the update

    // Act: Call the service layer's updateRecipe method
    boolean result = recipeService.updateRecipe(recipe);

    // Assert: Verify the result is true and the data access layer was called once
    assertFalse(result);
    verify(mockDataAccess, times(1))
        .getRecipeById(recipe.getId()); // Verify fetching existing recipe
  }

  @Test
  void updateRecipe_nullExistingRecipe() {
    // Arrange: Create a valid recipe object and set the ID
    Recipe recipe = new Recipe();
    recipe.setId(1L); // Set the ID
    recipe.setName("Updated Recipe");
    recipe.setCookingTimeInMin(300); // Set cooking time in seconds
    recipe.setDifficultyLevel(2); // Set difficulty level (e.g., 1 to 5)

    // Create an existing recipe object that is different from the new recipe
    Recipe existingRecipe = null;

    // Mock the data access layer to return the existing recipe when fetched
    assertThrows(IllegalArgumentException.class, () -> recipeService.updateRecipe(recipe));
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
    existingRecipe.setCookingTimeInMin(600); // Different cooking time
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

  @Test
  void deleteRecipeById_Failure() {
    // Arrange: Set the recipe ID
    Long recipeId = 1L;

    // Create an existing recipe object that is different from the new recipe
    Recipe existingRecipe = new Recipe();
    existingRecipe.setId(1L); // Same ID
    existingRecipe.setName("Old Recipe");
    existingRecipe.setCookingTimeInMin(600); // Different cooking time
    existingRecipe.setDifficultyLevel(3); // Different difficulty level

    // Mock the data access layer to return the existing recipe when fetched
    when(mockDataAccess.getRecipeById(anyLong())).thenReturn(existingRecipe);
    // Mock the data access layer to return true
    when(mockDataAccess.deleteRecipeById(recipeId)).thenReturn(false);

    // Act: Call the service layer's deleteRecipeById method
    boolean result = recipeService.deleteRecipeById(recipeId);

    // Assert: Verify the result is true and the data access layer was called once
    assertFalse(result);
    verify(mockDataAccess, times(1)).deleteRecipeById(recipeId);
  }

  @Test
  void deleteRecipeById_noExistingRecipe() {
    // Arrange: Set the recipe ID
    Long recipeId = 1L;

    // Create an existing recipe object that is different from the new recipe
    Recipe existingRecipe = new Recipe();
    existingRecipe.setId(1L); // Same ID
    existingRecipe.setName("Old Recipe");
    existingRecipe.setCookingTimeInMin(600); // Different cooking time
    existingRecipe.setDifficultyLevel(3); // Different difficulty level

    when(mockDataAccess.getRecipeById(anyLong())).thenReturn(null);
    // Mock the data access layer to return the existing recipe when fetched
    assertThrows(IllegalArgumentException.class, () -> recipeService.deleteRecipeById(anyLong()));
  }

  @Test
  void deleteRecipeById_GotDraft() {
    // Arrange: Set the recipe ID
    Long recipeId = 1L;

    // Create an existing recipe object that is different from the new recipe
    Recipe existingRecipe = new Recipe();
    existingRecipe.setId(1L); // Same ID
    existingRecipe.setName("Old Recipe");
    existingRecipe.setCookingTimeInMin(600); // Different cooking time
    existingRecipe.setDifficultyLevel(3); // Different difficulty level
    existingRecipe.setDraftRecipe(new Recipe());
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

  @Test
  void deleteRecipeById_GotDraft_GotId() {
    // Arrange: Set the recipe ID
    Long recipeId = 1L;
    Long draftRecipeId = 2L;

    // Create an existing recipe object that is different from the new recipe
    Recipe existingRecipe = new Recipe();
    existingRecipe.setId(recipeId); // Same ID
    existingRecipe.setName("Old Recipe");
    existingRecipe.setCookingTimeInMin(600); // Different cooking time
    existingRecipe.setDifficultyLevel(3); // Different difficulty level

    Recipe draftRecipe = new Recipe();
    draftRecipe.setId(draftRecipeId);
    existingRecipe.setDraftRecipe(draftRecipe);
    // Mock the data access layer to return the existing recipe when fetched
    when(mockDataAccess.getRecipeById(anyLong())).thenReturn(existingRecipe);
    // Mock the data access layer to return true
    when(mockDataAccess.deleteRecipeById(recipeId)).thenReturn(true);
    when(mockDataAccess.deleteRecipeById(draftRecipeId)).thenReturn(true);

    // Act: Call the service layer's deleteRecipeById method
    boolean result = recipeService.deleteRecipeById(recipeId);

    // Assert: Verify the result is true and the data access layer was called once
    assertTrue(result);
    verify(mockDataAccess, times(1)).deleteRecipeById(recipeId);
    verify(mockDataAccess, times(1)).deleteRecipeById(draftRecipeId);
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

  @Test
  void getRecipeById_Found_emptyDraft() {
    // Arrange: Set the recipe ID and mock the returned recipe object
    Long recipeId = 1L;
    Recipe recipe = new Recipe();
    recipe.setId(recipeId);
    recipe.setName("Existing Recipe");
    recipe.setDraftRecipe(new Recipe());

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

  @Test
  void getRecipeById_Found_populatedDraft() {
    // Arrange: Set the recipe ID and mock the returned recipe object
    Long recipeId = 1L;
    Recipe recipe = new Recipe();
    recipe.setId(recipeId);
    recipe.setName("Existing Recipe");

    Recipe draftRecipe = new Recipe();
    draftRecipe.setId(2L);
    recipe.setDraftRecipe(draftRecipe);

    when(mockDataAccess.getRecipeById(recipeId)).thenReturn(recipe);
    when(mockDataAccess.getRecipeById(draftRecipe.getId())).thenReturn(draftRecipe);

    // Act: Call the service layer's getRecipeById method
    Recipe result = recipeService.getRecipeById(recipeId);

    // Assert: Verify the returned recipe object matches the expected one and the data access layer
    // was called once
    assertNotNull(result);
    assertEquals(recipeId, result.getId());
    assertEquals("Existing Recipe", result.getName());
    assertEquals(2L, result.getDraftRecipe().getId());
    verify(mockDataAccess, times(1)).getRecipeById(recipeId);
    verify(mockDataAccess, times(1)).getRecipeById(draftRecipe.getId());
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

  @Test
  void getAllRecipes_MultipleRecipe() {
    // Arrange: Create a list of recipes and mock the data access layer to return it
    List<Recipe> recipes = new ArrayList<>();
    {
      Recipe recipe = new Recipe();
      recipe.setId(1L);
      recipe.setName("Recipe One");
      Recipe draftRecipe = new Recipe();
      draftRecipe.setId(3L);
      recipe.setDraftRecipe(draftRecipe);
      recipes.add(recipe);
    }
    {
      Recipe recipe = new Recipe();
      recipe.setId(2L);
      recipe.setName("Recipe Two");
      recipes.add(recipe);
      recipe.setDraftRecipe(new Recipe());
    }
    {
      Recipe recipe = new Recipe();
      recipe.setId(3L);
      recipe.setName("Recipe 3");
      recipe.setStatus(ERecipeStatus.DRAFT);
      recipes.add(recipe);
    }
    {
      Recipe recipe = new Recipe();
      recipe.setId(4L);
      recipe.setName("Recipe 4");
      recipe.setStatus(ERecipeStatus.ARCHIVED);
      recipe.setDraftRecipe(null);
      recipes.add(recipe);
    }
    {
      Recipe recipe = new Recipe();
      recipe.setId(5L);
      recipe.setName("Recipe 5");
      recipe.setStatus(ERecipeStatus.DRAFT);
      recipes.add(recipe);
    }

    when(mockDataAccess.getAllRecipes()).thenReturn(recipes);

    // Act: Call the service layer's getAllRecipes method
    List<Recipe> result = recipeService.getAllRecipes();

    // Assert: Verify the returned list matches the expected one and the data access layer was
    // called once
    assertNotNull(result);
    assertEquals(4, result.size());
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

  @Test
  void getAllPublishedRecipes_Success() {
    // Arrange: Create a list of recipes and mock the data access layer to return it
    Recipe recipe1 = new Recipe();
    recipe1.setId(1L);
    recipe1.setName("Recipe One");

    Recipe recipe2 = new Recipe();
    recipe2.setId(2L);
    recipe2.setName("Recipe Two");

    List<Recipe> recipes = List.of(recipe1, recipe2);
    when(mockDataAccess.getAllPublishedRecipes()).thenReturn(recipes);

    // Act: Call the service layer's getAllRecipes method
    List<Recipe> result = recipeService.getAllPublishedRecipes();

    // Assert: Verify the returned list matches the expected one and the data access layer was
    // called once
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(mockDataAccess, times(1)).getAllPublishedRecipes();
  }

  /**
   * Test when the data access layer returns an empty list while retrieving all recipes Verify that
   * the service layer returns an empty list
   */
  @Test
  void getAllPublishedRecipes_EmptyList() {
    // Arrange: Mock the data access layer to return an empty list
    when(mockDataAccess.getAllPublishedRecipes()).thenReturn(Collections.emptyList());

    // Act: Call the service layer's getAllRecipes method
    List<Recipe> result = recipeService.getAllPublishedRecipes();

    // Assert: Verify the returned list is empty and the data access layer was called once
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(mockDataAccess, times(1)).getAllPublishedRecipes();
  }

  @Test
  void getRecipesByCreatorId_nullList() {
    int creatorId = 1;

    // Arrange: Mock the data access layer to return an empty list
    when(mockDataAccess.getRecipeByCreatorId(creatorId)).thenReturn(null);

    // Act: Call the service layer's getAllRecipes method
    List<Recipe> result = recipeService.getRecipesByCreatorId(creatorId);

    // Assert: Verify the returned list is empty and the data access layer was called once
    assertNull(result);
    verify(mockDataAccess, times(1)).getRecipeByCreatorId(creatorId);
  }

  @Test
  void getRecipesByCreatorId_populatedList() {
    int creatorId = 1;

    List<Recipe> recipes = List.of(new Recipe(), new Recipe());
    // Arrange: Mock the data access layer to return an empty list
    when(mockDataAccess.getRecipeByCreatorId(creatorId)).thenReturn(recipes);

    // Act: Call the service layer's getAllRecipes method
    List<Recipe> result = recipeService.getRecipesByCreatorId(creatorId);

    // Assert: Verify the returned list is empty and the data access layer was called once
    assertNotNull(result);
    verify(mockDataAccess, times(1)).getRecipeByCreatorId(creatorId);
  }

  @Test
  void getRecipesByDifficulty() {
    boolean isDesc = false;

    List<Recipe> recipes = List.of(new Recipe(), new Recipe());
    // Arrange: Mock the data access layer to return an empty list
    when(mockDataAccess.getAllPublishedRecipesByDifficulty(isDesc)).thenReturn(recipes);

    // Act: Call the service layer's getAllRecipes method
    List<Recipe> result = recipeService.getRecipesByDifficulty(isDesc);

    // Assert: Verify the returned list is empty and the data access layer was called once
    assertNotNull(result);
    verify(mockDataAccess, times(1)).getAllPublishedRecipesByDifficulty(isDesc);
  }

  @Test
  void getRecipesByRating() {
    boolean isDesc = false;

    List<Recipe> recipes = List.of(new Recipe(), new Recipe());
    // Arrange: Mock the data access layer to return an empty list
    when(mockDataAccess.getAllPublishedRecipesByRating(isDesc)).thenReturn(recipes);

    // Act: Call the service layer's getAllRecipes method
    List<Recipe> result = recipeService.getRecipesByRating(isDesc);

    // Assert: Verify the returned list is empty and the data access layer was called once
    assertNotNull(result);
    verify(mockDataAccess, times(1)).getAllPublishedRecipesByRating(isDesc);
  }

  @Test
  void getRecipesByUserReview_nullRecipeList() {
    int userId = 1;
    boolean isDesc = false;

    List<Recipe> recipes = null;
    // Arrange: Mock the data access layer to return an empty list
    when(mockDataAccess.getRecipeByCreatorId(userId)).thenReturn(recipes);
    when(mockDataAccess.getAllPublishedRecipesByDifficulty(isDesc)).thenReturn(recipes);

    // Act: Call the service layer's getAllRecipes method
    List<Recipe> result = recipeService.getRecipesByUserReview(userId, isDesc);

    // Assert: Verify the returned list is empty and the data access layer was called once
    assertNull(result);
    verify(mockDataAccess, times(1)).getRecipeByCreatorId(userId);
    verify(mockDataAccess, times(1)).getAllPublishedRecipesByDifficulty(isDesc);
  }

  @Test
  void getRecipesByUserReview_emptyRecipeList() {
    int userId = 1;
    boolean isDesc = false;

    List<Recipe> recipes = new ArrayList<>();
    // Arrange: Mock the data access layer to return an empty list
    when(mockDataAccess.getRecipeByCreatorId(userId)).thenReturn(recipes);
    when(mockDataAccess.getAllPublishedRecipesByDifficulty(isDesc)).thenReturn(recipes);

    // Act: Call the service layer's getAllRecipes method
    List<Recipe> result = recipeService.getRecipesByUserReview(userId, isDesc);

    // Assert: Verify the returned list is empty and the data access layer was called once
    assertNotNull(result);
    assertEquals(0, result.size());
    verify(mockDataAccess, times(1)).getRecipeByCreatorId(userId);
    verify(mockDataAccess, times(1)).getAllPublishedRecipesByDifficulty(isDesc);
  }

  @Test
  void getRecipesByUserReview_populatedRecipeList() {
    int userId = 1;
    boolean isDesc = false;

    List<Recipe> recipes = new ArrayList<>();
    {
      Recipe recipe = new Recipe();
      recipe.setId(1L);
      recipe.setName("Recipe One");
      recipe.setRating(1.1);
      recipes.add(recipe);
    }
    {
      Recipe recipe = new Recipe();
      recipe.setId(2L);
      recipe.setName("Recipe Two");
      recipe.setRating(2.2);
      recipes.add(recipe);
    }
    {
      Recipe recipe = new Recipe();
      recipe.setId(3L);
      recipe.setName("Recipe Three");
      recipe.setRating(3.3);
      recipes.add(recipe);
    }
    // Arrange: Mock the data access layer to return an empty list
    when(mockDataAccess.getRecipeByCreatorId(userId)).thenReturn(recipes);
    when(mockDataAccess.getAllPublishedRecipesByDifficulty(isDesc)).thenReturn(recipes);

    // Act: Call the service layer's getAllRecipes method
    List<Recipe> result = recipeService.getRecipesByUserReview(userId, isDesc);

    // Assert: Verify the returned list is empty and the data access layer was called once
    assertNotNull(result);
    assertEquals(3, result.size());
    verify(mockDataAccess, times(1)).getRecipeByCreatorId(userId);
    verify(mockDataAccess, times(1)).getAllPublishedRecipesByDifficulty(isDesc);
  }

  @Test
  void getRecipesByUserReview_populatedRecipeList_recipeTempListNullId() {
    int userId = 1;
    boolean isDesc = false;

    List<Recipe> recipes = new ArrayList<>();
    {
      Recipe recipe = new Recipe();
      recipe.setId(1L);
      recipe.setName("Recipe One");
      recipe.setRating(1.1);
      recipes.add(recipe);
    }
    {
      Recipe recipe = new Recipe();
      recipe.setId(2L);
      recipe.setName("Recipe Two");
      recipe.setRating(2.2);
      recipes.add(recipe);
    }
    {
      Recipe recipe = new Recipe();
      recipe.setId(3L);
      recipe.setName("Recipe Three");
      recipe.setRating(3.3);
      recipes.add(recipe);
    }

    List<Recipe> tmpRecipes = new ArrayList<>();
    {
      Recipe recipe = new Recipe();
      recipe.setName("Recipe One");
      recipe.setRating(1.1);
      tmpRecipes.add(recipe);
    }
    {
      Recipe recipe = new Recipe();
      recipe.setId(100L);
      recipe.setName("Recipe One");
      recipe.setRating(1.1);
      tmpRecipes.add(recipe);
    }
    // Arrange: Mock the data access layer to return an empty list
    when(mockDataAccess.getRecipeByCreatorId(userId)).thenReturn(recipes);
    when(mockDataAccess.getAllPublishedRecipesByDifficulty(isDesc)).thenReturn(tmpRecipes);

    // Act: Call the service layer's getAllRecipes method
    List<Recipe> result = recipeService.getRecipesByUserReview(userId, isDesc);

    // Assert: Verify the returned list is empty and the data access layer was called once
    assertNotNull(result);
    assertEquals(4, result.size());
    verify(mockDataAccess, times(1)).getRecipeByCreatorId(userId);
    verify(mockDataAccess, times(1)).getAllPublishedRecipesByDifficulty(isDesc);
  }

  @Test
  void updateRecipeRating_Success() {
    long recipeId = 1L;
    double rating = 1.1;

    boolean updateBoolean = true;
    // Arrange: Mock the data access layer to return an empty list
    when(mockDataAccess.updateRecipeRating(recipeId, rating)).thenReturn(updateBoolean);

    // Act: Call the service layer's getAllRecipes method
    boolean result = recipeService.updateRecipeRating(recipeId, rating);

    // Assert: Verify the returned list is empty and the data access layer was called once
    assertTrue(result);
    verify(mockDataAccess, times(1)).updateRecipeRating(recipeId, rating);
  }

  @Test
  void updateRecipeRating_Failure() {
    long recipeId = 1L;
    double rating = 1.1;

    boolean updateBoolean = false;
    // Arrange: Mock the data access layer to return an empty list
    when(mockDataAccess.updateRecipeRating(recipeId, rating)).thenReturn(updateBoolean);

    // Act: Call the service layer's getAllRecipes method
    boolean result = recipeService.updateRecipeRating(recipeId, rating);

    // Assert: Verify the returned list is empty and the data access layer was called once
    assertFalse(result);
    verify(mockDataAccess, times(1)).updateRecipeRating(recipeId, rating);
  }
}
