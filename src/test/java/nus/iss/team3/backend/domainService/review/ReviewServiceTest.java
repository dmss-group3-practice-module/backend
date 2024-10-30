package nus.iss.team3.backend.domainService.review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import nus.iss.team3.backend.dataaccess.IReviewDataAccess;
import nus.iss.team3.backend.entity.RecipeReview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit test class: RecipeReviewServiceTest is used to test the methods of the RecipeReviewService
 * class to ensure its behavior meets expectations.
 *
 * @author Mao Weining
 */
public class ReviewServiceTest {

  @Mock private IReviewDataAccess recipeReviewDataAccess;

  @InjectMocks private ReviewService reviewService;

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

    reviewService.addReview(review);

    verify(recipeReviewDataAccess, times(1)).addReview(review);
  }

  @Test
  public void testAddReview_NullReview() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> reviewService.addReview(null));
    assertEquals("Review cannot be null", exception.getMessage());
  }

  @Test
  public void testAddReview_NullRecipeId() {
    RecipeReview review = new RecipeReview();
    review.setCreatorId(1L);
    review.setRating(5.0);

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> reviewService.addReview(review));
    assertEquals("Recipe ID cannot be null", exception.getMessage());
  }

  @Test
  public void testAddReview_NullCreatorId() {
    RecipeReview review = new RecipeReview();
    review.setRecipeId(1L);
    review.setRating(5.0);

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> reviewService.addReview(review));
    assertEquals("Creator ID cannot be null", exception.getMessage());
  }

  @Test
  public void testAddReview_InvalidRating() {
    RecipeReview review = new RecipeReview();
    review.setRecipeId(1L);
    review.setCreatorId(1L);
    review.setRating(6.0); // Invalid rating

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> reviewService.addReview(review));
    assertEquals("Rating must be between 0 and 5", exception.getMessage());
  }

  @Test
  public void testUpdateReview_Success() {
    RecipeReview review = new RecipeReview();
    review.setRecipeId(1L);
    review.setCreatorId(1L);
    review.setRating(5.0);

    reviewService.updateReview(1L, 1L, review);

    verify(recipeReviewDataAccess, times(1)).updateReview(1L, 1L, review);
  }

  @Test
  public void testDeleteReview_Success() {
    reviewService.deleteReview(1L, 1L);

    verify(recipeReviewDataAccess, times(1)).deleteReview(1L, 1L);
  }

  @Test
  public void testDeleteReviewsByRecipeId_Success() {
    reviewService.deleteReviewsByRecipeId(1L);

    verify(recipeReviewDataAccess, times(1)).deleteReviewsByRecipeId(1L);
  }

  @Test
  public void testDeleteReviewsByCreatorId_Success() {
    reviewService.deleteReviewsByCreatorId(1L, 1L);

    verify(recipeReviewDataAccess, times(1)).deleteReviewsByCreatorId(1L);
  }

  @Test
  public void testGetReviewByRecipeAndCreator_Found() {
    RecipeReview review = new RecipeReview();
    review.setRecipeId(1L);
    review.setCreatorId(1L);
    review.setRating(5.0);

    when(recipeReviewDataAccess.getReviewByRecipeAndCreator(1L, 1L)).thenReturn(review);

    RecipeReview result = reviewService.getReviewByRecipeAndCreator(1L, 1L);

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

    List<RecipeReview> result = reviewService.getReviewsByRecipeId(1L);

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

    List<RecipeReview> result = reviewService.getReviewsByCreatorId(1L, 1L);

    assertEquals(reviews, result);
  }
}
