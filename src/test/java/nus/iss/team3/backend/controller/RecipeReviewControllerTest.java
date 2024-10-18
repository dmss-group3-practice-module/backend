package nus.iss.team3.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import nus.iss.team3.backend.entity.RecipeReview;
import nus.iss.team3.backend.service.IRecipeReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RecipeReviewControllerTest {

  @Mock private IRecipeReviewService recipeReviewService;

  @InjectMocks private RecipeReviewController recipeReviewController;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testAddReview_Success() {
    RecipeReview review = new RecipeReview();
    review.setComments("Great recipe!");

    ResponseEntity<String> response = recipeReviewController.addReview(1L, review);

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

    ResponseEntity<String> response = recipeReviewController.addReview(1L, review);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid review", response.getBody());
  }

  @Test
  public void testUpdateReview_Success() {
    RecipeReview review = new RecipeReview();
    review.setComments("Updated review");

    ResponseEntity<String> response = recipeReviewController.updateReview(1L, 1L, review);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Review updated successfully", response.getBody());
    verify(recipeReviewService, times(1)).updateReview(1L, 1L, review);
  }

  @Test
  public void testDeleteReview_Success() {
    ResponseEntity<String> response = recipeReviewController.deleteReview(1L, 1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Review deleted successfully", response.getBody());
    verify(recipeReviewService, times(1)).deleteReview(1L, 1L);
  }

  @Test
  public void testDeleteReviewsByCreator_Success() {
    ResponseEntity<String> response = recipeReviewController.deleteReviewsByCreator(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("All reviews by the same creator ID deleted successfully", response.getBody());
    verify(recipeReviewService, times(1)).deleteReviewsByCreatorId(1L);
  }

  @Test
  public void testDeleteReviewsByCreator_Failure() {
    doThrow(new RuntimeException("Deletion failed"))
        .when(recipeReviewService)
        .deleteReviewsByCreatorId(1L);

    ResponseEntity<String> response = recipeReviewController.deleteReviewsByCreator(1L);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Failed to delete reviews", response.getBody());
  }

  @Test
  public void testGetReviewByRecipeAndCreator_Found() {
    RecipeReview review = new RecipeReview();
    review.setComments("Great recipe!");

    when(recipeReviewService.getReviewByRecipeAndCreator(1L, 1L)).thenReturn(review);

    ResponseEntity<RecipeReview> response =
        recipeReviewController.getReviewByRecipeAndCreator(1L, 1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(review, response.getBody());
  }

  @Test
  public void testGetReviewsByRecipeId() {
    RecipeReview review = new RecipeReview();
    review.setComments("Great recipe!");
    List<RecipeReview> reviews = Collections.singletonList(review);

    when(recipeReviewService.getReviewsByRecipeId(1L)).thenReturn(reviews);

    ResponseEntity<List<RecipeReview>> response = recipeReviewController.getReviewsByRecipeId(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(reviews, response.getBody());
  }

  @Test
  public void testGetReviewsByCreatorId() {
    RecipeReview review = new RecipeReview();
    review.setComments("Great recipe!");
    List<RecipeReview> reviews = Collections.singletonList(review);

    when(recipeReviewService.getReviewsByCreatorId(1L)).thenReturn(reviews);

    ResponseEntity<List<RecipeReview>> response = recipeReviewController.getReviewsByCreatorId(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(reviews, response.getBody());
  }
}
