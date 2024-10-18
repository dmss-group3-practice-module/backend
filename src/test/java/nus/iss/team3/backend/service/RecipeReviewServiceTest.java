package nus.iss.team3.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import nus.iss.team3.backend.dataaccess.IRecipeReviewDataAccess;
import nus.iss.team3.backend.entity.RecipeReview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RecipeReviewServiceTest {

  @Mock private IRecipeReviewDataAccess recipeReviewDataAccess;

  @InjectMocks private RecipeReviewService recipeReviewService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testAddReview_Success() {
    RecipeReview review = new RecipeReview();
    review.setRecipeId(1L);
    review.setCreatorId(1L);
    review.setRating(5.0);

    recipeReviewService.addReview(review);

    verify(recipeReviewDataAccess, times(1)).addReview(review);
  }

  @Test
  public void testAddReview_NullReview() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> recipeReviewService.addReview(null));
    assertEquals("Review cannot be null", exception.getMessage());
  }

  @Test
  public void testAddReview_NullRecipeId() {
    RecipeReview review = new RecipeReview();
    review.setCreatorId(1L);
    review.setRating(5.0);

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> recipeReviewService.addReview(review));
    assertEquals("Recipe ID cannot be null", exception.getMessage());
  }

  @Test
  public void testAddReview_NullCreatorId() {
    RecipeReview review = new RecipeReview();
    review.setRecipeId(1L);
    review.setRating(5.0);

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> recipeReviewService.addReview(review));
    assertEquals("Creator ID cannot be null", exception.getMessage());
  }

  @Test
  public void testAddReview_InvalidRating() {
    RecipeReview review = new RecipeReview();
    review.setRecipeId(1L);
    review.setCreatorId(1L);
    review.setRating(6.0); // Invalid rating

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> recipeReviewService.addReview(review));
    assertEquals("Rating must be between 0 and 5", exception.getMessage());
  }

  @Test
  public void testUpdateReview_Success() {
    RecipeReview review = new RecipeReview();
    review.setRecipeId(1L);
    review.setCreatorId(1L);
    review.setRating(5.0);

    recipeReviewService.updateReview(1L, 1L, review);

    verify(recipeReviewDataAccess, times(1)).updateReview(1L, 1L, review);
  }

  @Test
  public void testDeleteReview_Success() {
    recipeReviewService.deleteReview(1L, 1L);

    verify(recipeReviewDataAccess, times(1)).deleteReview(1L, 1L);
  }

  @Test
  public void testDeleteReviewsByRecipeId_Success() {
    recipeReviewService.deleteReviewsByRecipeId(1L);

    verify(recipeReviewDataAccess, times(1)).deleteReviewsByRecipeId(1L);
  }

  @Test
  public void testDeleteReviewsByCreatorId_Success() {
    recipeReviewService.deleteReviewsByCreatorId(1L);

    verify(recipeReviewDataAccess, times(1)).deleteReviewsByCreatorId(1L);
  }

  @Test
  public void testGetReviewByRecipeAndCreator_Found() {
    RecipeReview review = new RecipeReview();
    review.setRecipeId(1L);
    review.setCreatorId(1L);
    review.setRating(5.0);

    when(recipeReviewDataAccess.getReviewByRecipeAndCreator(1L, 1L)).thenReturn(review);

    RecipeReview result = recipeReviewService.getReviewByRecipeAndCreator(1L, 1L);

    assertEquals(review, result);
  }

  @Test
  public void testGetReviewsByRecipeId() {
    RecipeReview review = new RecipeReview();
    review.setRecipeId(1L);
    review.setCreatorId(1L);
    review.setRating(5.0);
    List<RecipeReview> reviews = Collections.singletonList(review);

    when(recipeReviewDataAccess.getReviewsByRecipeId(1L)).thenReturn(reviews);

    List<RecipeReview> result = recipeReviewService.getReviewsByRecipeId(1L);

    assertEquals(reviews, result);
  }

  @Test
  public void testGetReviewsByCreatorId() {
    RecipeReview review = new RecipeReview();
    review.setRecipeId(1L);
    review.setCreatorId(1L);
    review.setRating(5.0);
    List<RecipeReview> reviews = Collections.singletonList(review);

    when(recipeReviewDataAccess.getReviewsByCreatorId(1L)).thenReturn(reviews);

    List<RecipeReview> result = recipeReviewService.getReviewsByCreatorId(1L);

    assertEquals(reviews, result);
  }
}
