package nus.iss.team3.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import nus.iss.team3.backend.businessService.recipeReview.RecipeReviewService;
import nus.iss.team3.backend.domainService.review.IReviewService;
import nus.iss.team3.backend.entity.RecipeReview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Unit test class: RecipeReviewControllerTest is used to test various endpoints of the
 * RecipeReviewController class to ensure its behavior meets expectations. This class testes the
 * logic in controller but not the MVC calls...
 *
 * @author Mao Weining
 */
@ExtendWith(SpringExtension.class)
public class ReviewControllerTest {

  @Mock private IReviewService reviewService;
  @Mock private RecipeReviewService recipeReviewService;

  @InjectMocks private ReviewController reviewController;

  @BeforeEach
  public void setUp() {}

  @Test
  public void testAddReview_Success() {
    RecipeReview review = new RecipeReview();
    review.setComments("Great recipe!");

    ResponseEntity<String> response = reviewController.addReview(1L, review);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("Review added successfully", response.getBody());
    verify(recipeReviewService, times(1)).addReview(any(RecipeReview.class));
  }

  @Test
  public void testAddReview_ValidationError() {
    RecipeReview review = new RecipeReview(); // Assume this causes validation error

    doThrow(new IllegalArgumentException("Invalid review"))
        .when(recipeReviewService)
        .addReview(any());

    ResponseEntity<String> response = reviewController.addReview(1L, review);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid review", response.getBody());
  }

  @Test
  public void testUpdateReview_Success() {
    RecipeReview review = new RecipeReview();
    review.setComments("Updated review");

    ResponseEntity<String> response = reviewController.updateReview(1L, 1L, review);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Review updated successfully", response.getBody());
    verify(reviewService, times(1)).updateReview(1L, 1L, review);
  }

  @Test
  public void testDeleteReview_Success() {
    ResponseEntity<String> response = reviewController.deleteReview(1L, 1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Review deleted successfully", response.getBody());
    verify(reviewService, times(1)).deleteReview(1L, 1L);
  }

  @Test
  public void testDeleteReviewsByCreator_Success() {
    ResponseEntity<String> response = reviewController.deleteReviewsByCreator(1L, 1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("All reviews by the same creator ID deleted successfully", response.getBody());
    verify(reviewService, times(1)).deleteReviewsByCreatorId(1L, 1L);
  }

  @Test
  public void testDeleteReviewsByCreator_Failure() {
    doThrow(new RuntimeException("Deletion failed"))
        .when(reviewService)
        .deleteReviewsByCreatorId(1L, 1L);

    ResponseEntity<String> response = reviewController.deleteReviewsByCreator(1L, 1L);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Failed to delete reviews", response.getBody());
  }

  @Test
  public void testDeleteReviewsByRecipeId() {

    ResponseEntity<String> response = reviewController.deleteReviewsByRecipeId(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Review deleted successfully", response.getBody());
    verify(reviewService, times(1)).deleteReviewsByRecipeId(1L);
  }

  @Test
  public void testGetReviewByRecipeAndCreator_Found() {
    RecipeReview review = new RecipeReview();
    review.setComments("Great recipe!");

    when(reviewService.getReviewByRecipeAndCreator(1L, 1L)).thenReturn(review);

    ResponseEntity<RecipeReview> response = reviewController.getReviewByRecipeAndCreator(1L, 1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(review, response.getBody());
  }

  @Test
  public void testGetReviewByRecipeAndCreator_NotFound() {
    RecipeReview review = null;

    when(reviewService.getReviewByRecipeAndCreator(1L, 1L)).thenReturn(review);

    ResponseEntity<RecipeReview> response = reviewController.getReviewByRecipeAndCreator(1L, 1L);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void testGetReviewsByRecipeId() {
    RecipeReview review = new RecipeReview();
    review.setComments("Great recipe!");
    List<RecipeReview> reviews = Collections.singletonList(review);

    when(reviewService.getReviewsByRecipeId(1L)).thenReturn(reviews);

    ResponseEntity<List<RecipeReview>> response = reviewController.getReviewsByRecipeId(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(reviews, response.getBody());
  }

  @Test
  public void testGetReviewsByRecipeId_ReviewNotFound() {
    List<RecipeReview> reviews = null;

    when(reviewService.getReviewsByRecipeId(1L)).thenReturn(reviews);

    ResponseEntity<List<RecipeReview>> response = reviewController.getReviewsByRecipeId(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(reviews, response.getBody());
  }

  @Test
  public void testGetReviewsByCreatorId() {
    RecipeReview review = new RecipeReview();
    review.setComments("Great recipe!");
    List<RecipeReview> reviews = Collections.singletonList(review);

    when(reviewService.getReviewsByCreatorId(1L, 1L)).thenReturn(reviews);

    ResponseEntity<List<RecipeReview>> response = reviewController.getReviewsByCreatorId(1L, 1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(reviews, response.getBody());
  }

  @Test
  public void testGetReviewsByCreatorId_NotFound() {
    List<RecipeReview> reviews = null;

    when(reviewService.getReviewsByCreatorId(1L, 1L)).thenReturn(reviews);

    ResponseEntity<List<RecipeReview>> response = reviewController.getReviewsByCreatorId(1L, 1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(reviews, response.getBody());
  }
}
