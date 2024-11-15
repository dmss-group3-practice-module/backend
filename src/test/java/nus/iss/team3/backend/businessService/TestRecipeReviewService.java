package nus.iss.team3.backend.businessService;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import nus.iss.team3.backend.businessService.recipeReview.IRecipeReviewService;
import nus.iss.team3.backend.businessService.recipeReview.RecipeReviewService;
import nus.iss.team3.backend.domainService.recipe.IRecipeService;
import nus.iss.team3.backend.domainService.review.IReviewService;
import nus.iss.team3.backend.entity.Recipe;
import nus.iss.team3.backend.entity.RecipeReview;
import nus.iss.team3.backend.entity.RecipeWithReviews;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestRecipeReviewService {

  private IRecipeReviewService recipeReviewService; // Mocked review service

  private IRecipeService mockRecipeService; // Mocked recipe service
  private IReviewService mockReviewService;

  private Recipe sampleRecipe; // Sample recipe object for testing
  private RecipeWithReviews sampleRecipeWithReviews; // Sample RecipeWithReviews object for testing

  /**
   * Initializes the test environment by creating new mock objects and RecipeService instance before
   * each test.
   */
  @BeforeEach
  void setUp() {
    mockRecipeService = mock(IRecipeService.class);
    mockReviewService = mock(IReviewService.class);
    recipeReviewService = new RecipeReviewService(mockRecipeService, mockReviewService);

    // Initialize sample Recipe object
    sampleRecipe = new Recipe();
    sampleRecipe.setId(1L);
    sampleRecipe.setName("Sample Recipe");

    // Initialize sample RecipeWithReviews object
    RecipeReview review = new RecipeReview();
    review.setRecipeId(sampleRecipe.getId());
    review.setComments("Excellent recipe!");
    sampleRecipeWithReviews = new RecipeWithReviews(sampleRecipe, List.of(review));
  }

  /** Test successfully getting a recipe with reviews by ID. */
  @Test
  void getRecipeWithReviewsById_Success() {
    // Arrange
    Long recipeId = 1L;
    when(mockRecipeService.getRecipeById(recipeId)).thenReturn(sampleRecipe);
    when(mockReviewService.getReviewsByRecipeId(recipeId))
        .thenReturn(List.of(sampleRecipeWithReviews.getReviews().getFirst()));

    // Act
    RecipeWithReviews result = recipeReviewService.getRecipeWithReviewsById(recipeId);

    // Assert
    assertNotNull(result);
    assertEquals(sampleRecipe.getId(), result.getRecipe().getId());
    assertEquals(sampleRecipe.getName(), result.getRecipe().getName());
    assertEquals(1, result.getReviews().size());
    assertEquals("Excellent recipe!", result.getReviews().getFirst().getComments());

    verify(mockRecipeService, times(1)).getRecipeById(recipeId);
    verify(mockReviewService, times(1)).getReviewsByRecipeId(recipeId);
  }

  /** Test getting a recipe with reviews by ID when the recipe is not found. */
  @Test
  void getRecipeWithReviewsById_NotFound() {
    // Arrange
    Long recipeId = 1L;
    when(mockRecipeService.getRecipeById(recipeId)).thenReturn(null);

    // Act
    RecipeWithReviews result = recipeReviewService.getRecipeWithReviewsById(recipeId);

    // Assert
    assertNull(result);
    verify(mockRecipeService, times(1)).getRecipeById(recipeId);
    verify(mockReviewService, never()).getReviewsByRecipeId(recipeId);
  }

  /** Test successfully getting all recipes with reviews. */
  @Test
  void getAllRecipesWithReviews_Success() {
    // Arrange
    when(mockRecipeService.getAllRecipes()).thenReturn(List.of(sampleRecipe));
    when(mockReviewService.getReviewsByRecipeId(sampleRecipe.getId()))
        .thenReturn(List.of(sampleRecipeWithReviews.getReviews().getFirst()));

    // Act
    List<RecipeWithReviews> result = recipeReviewService.getAllRecipesWithReviews();

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(sampleRecipe.getId(), result.getFirst().getRecipe().getId());
    assertEquals(sampleRecipe.getName(), result.getFirst().getRecipe().getName());
    assertEquals(1, result.getFirst().getReviews().size());
    assertEquals("Excellent recipe!", result.getFirst().getReviews().getFirst().getComments());

    verify(mockRecipeService, times(1)).getAllRecipes();
    verify(mockReviewService, times(1)).getReviewsByRecipeId(sampleRecipe.getId());
  }

  /** Test getting all recipes with reviews when there are no recipes. */
  @Test
  void getAllRecipesWithReviews_EmptyList() {
    // Arrange
    when(mockRecipeService.getAllRecipes()).thenReturn(List.of());

    // Act
    List<RecipeWithReviews> result = recipeReviewService.getAllRecipesWithReviews();

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());

    verify(mockRecipeService, times(1)).getAllRecipes();
    verify(mockReviewService, never()).getReviewsByRecipeId(anyLong());
  }

  @Test
  void addReview_nullRecipe() {
    // Arrange
    RecipeReview inputReview = null;
    List<RecipeReview> inputRecipeReviewList = null;
    when(mockReviewService.getReviewsByRecipeId(anyLong())).thenReturn(inputRecipeReviewList);

    // Act
    recipeReviewService.addReview(inputReview);

    // Assert

    verify(mockReviewService, times(1)).addReview(any());
    verify(mockReviewService, never()).getReviewsByRecipeId(anyLong());
    verify(mockRecipeService, never()).updateRecipeRating(anyLong(), anyDouble());
  }

  @Test
  void addReview_nullRecipeId() {
    // Arrange
    RecipeReview inputReview = new RecipeReview();
    inputReview.setRecipeId(null);
    List<RecipeReview> inputRecipeReviewList = null;
    when(mockReviewService.getReviewsByRecipeId(anyLong())).thenReturn(inputRecipeReviewList);

    // Act
    recipeReviewService.addReview(inputReview);

    // Assert

    verify(mockReviewService, times(1)).addReview(any());
    verify(mockReviewService, never()).getReviewsByRecipeId(anyLong());
    verify(mockRecipeService, never()).updateRecipeRating(anyLong(), anyDouble());
  }

  @Test
  void addReview_validRecipe_nullReviews() {
    // Arrange
    RecipeReview inputReview = new RecipeReview();
    inputReview.setRecipeId(1L);
    List<RecipeReview> inputRecipeReviewList = null;
    when(mockReviewService.getReviewsByRecipeId(anyLong())).thenReturn(inputRecipeReviewList);

    // Act
    recipeReviewService.addReview(inputReview);

    // Assert

    verify(mockReviewService, times(1)).addReview(any());
    verify(mockReviewService, times(1)).getReviewsByRecipeId(anyLong());
    verify(mockRecipeService, never()).updateRecipeRating(anyLong(), anyDouble());
  }

  @Test
  void addReview_validRecipe_emptyReviews() {
    // Arrange
    RecipeReview inputReview = new RecipeReview();
    inputReview.setRecipeId(1L);
    List<RecipeReview> inputRecipeReviewList = new ArrayList<>();
    double averageRating = 4.0;
    when(mockReviewService.getReviewsByRecipeId(anyLong())).thenReturn(inputRecipeReviewList);

    // Act
    recipeReviewService.addReview(inputReview);

    // Assert

    verify(mockReviewService, times(1)).addReview(any());
    verify(mockReviewService, times(1)).getReviewsByRecipeId(anyLong());
    verify(mockRecipeService, never()).updateRecipeRating(anyLong(), eq(averageRating));
  }

  @Test
  void addReview_validRecipe_1Review_nullRating() {
    // Arrange
    RecipeReview inputReview = new RecipeReview();
    inputReview.setRecipeId(1L);
    List<RecipeReview> inputRecipeReviewList = new ArrayList<>();
    {
      RecipeReview review = new RecipeReview();
      review.setRating(null);
      inputRecipeReviewList.add(review);
    }
    double averageRating = 0.0;
    when(mockReviewService.getReviewsByRecipeId(anyLong())).thenReturn(inputRecipeReviewList);

    // Act
    recipeReviewService.addReview(inputReview);

    // Assert

    verify(mockReviewService, times(1)).addReview(any());
    verify(mockReviewService, times(1)).getReviewsByRecipeId(anyLong());
    verify(mockRecipeService, times(1)).updateRecipeRating(anyLong(), eq(averageRating));
  }

  @Test
  void addReview_validRecipe_1Review_validRating() {
    // Arrange
    RecipeReview inputReview = new RecipeReview();
    inputReview.setRecipeId(1L);
    List<RecipeReview> inputRecipeReviewList = new ArrayList<>();
    {
      RecipeReview review = new RecipeReview();
      review.setRating(4.0);
      inputRecipeReviewList.add(review);
    }
    double averageRating = 4.0;
    when(mockReviewService.getReviewsByRecipeId(anyLong())).thenReturn(inputRecipeReviewList);

    // Act
    recipeReviewService.addReview(inputReview);

    // Assert

    verify(mockReviewService, times(1)).addReview(any());
    verify(mockReviewService, times(1)).getReviewsByRecipeId(anyLong());
    verify(mockRecipeService, times(1)).updateRecipeRating(anyLong(), eq(averageRating));
  }

  @Test
  void addReview_validRecipe_1Review_validRating_with_compute() {
    // Arrange
    RecipeReview inputReview = new RecipeReview();
    inputReview.setRecipeId(1L);
    List<RecipeReview> inputRecipeReviewList = new ArrayList<>();
    {
      RecipeReview review = new RecipeReview();
      review.setRating(4.0);
      inputRecipeReviewList.add(review);
    }
    {
      RecipeReview review = new RecipeReview();
      review.setRating(2.0);
      inputRecipeReviewList.add(review);
    }
    {
      RecipeReview review = new RecipeReview();
      review.setRating(3.0);
      inputRecipeReviewList.add(review);
    }
    {
      RecipeReview review = new RecipeReview();
      review.setRating(4.0);
      inputRecipeReviewList.add(review);
    }
    double averageRating = 3.25;
    when(mockReviewService.getReviewsByRecipeId(anyLong())).thenReturn(inputRecipeReviewList);

    // Act
    recipeReviewService.addReview(inputReview);

    // Assert

    verify(mockReviewService, times(1)).addReview(any());
    verify(mockReviewService, times(1)).getReviewsByRecipeId(anyLong());
    verify(mockRecipeService, times(1)).updateRecipeRating(anyLong(), eq(averageRating));
  }

  @Test
  void addReview_validRecipe_1Review_validRating_with_compute_withNull() {
    // Arrange
    RecipeReview inputReview = new RecipeReview();
    inputReview.setRecipeId(1L);
    List<RecipeReview> inputRecipeReviewList = new ArrayList<>();
    {
      RecipeReview review = new RecipeReview();
      review.setRating(4.0);
      inputRecipeReviewList.add(review);
    }
    {
      RecipeReview review = new RecipeReview();
      review.setRating(2.0);
      inputRecipeReviewList.add(review);
    }
    {
      RecipeReview review = new RecipeReview();
      review.setRating(null);
      inputRecipeReviewList.add(review);
    }
    {
      RecipeReview review = new RecipeReview();
      review.setRating(4.0);
      inputRecipeReviewList.add(review);
    }
    double averageRating = 2.5;
    when(mockReviewService.getReviewsByRecipeId(anyLong())).thenReturn(inputRecipeReviewList);

    // Act
    recipeReviewService.addReview(inputReview);

    // Assert

    verify(mockReviewService, times(1)).addReview(any());
    verify(mockReviewService, times(1)).getReviewsByRecipeId(anyLong());
    verify(mockRecipeService, times(1)).updateRecipeRating(anyLong(), eq(averageRating));
  }
}
