package nus.iss.team3.backend.controller;

import java.util.List;
import nus.iss.team3.backend.entity.RecipeReview;
import nus.iss.team3.backend.service.IRecipeReviewService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class to handle web call for recipe reviews related queries.
 *
 * @author Mao Weining
 */
@RestController
@RequestMapping("/recipe/{recipeId}/reviews")
public class RecipeReviewController {

  private static final Logger logger = LogManager.getLogger(RecipeReviewController.class);
  private final IRecipeReviewService recipeReviewService;

  public RecipeReviewController(IRecipeReviewService recipeReviewService) {
    this.recipeReviewService = recipeReviewService;
  }

  /**
   * Add a new review for a specific recipe.
   *
   * @param recipeId The ID of the recipe for which the review is being added.
   * @param review The review object obtained from the request body.
   * @return Response entity indicating the result of the operation.
   */
  @PostMapping
  public ResponseEntity<String> addReview(
      @PathVariable Long recipeId, @RequestBody RecipeReview review) {
    review.setRecipeId(recipeId);
    logger.info("Received request to add review for recipe ID: {}", recipeId);
    try {
      recipeReviewService.addReview(review);
      logger.info("Added review for recipe ID: {}", recipeId);
      return new ResponseEntity<>("Review added successfully", HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      logger.warn("Validation error occurred while adding review: {}", e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Failed to add review for recipe ID: {}", recipeId, e);
      return new ResponseEntity<>("Failed to add review", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Update an existing review for a specific recipe by a specific creator.
   *
   * @param recipeId The ID of the recipe for which the review is being updated.
   * @param creatorId The ID of the creator of the review.
   * @param review The updated review object obtained from the request body.
   * @return Response entity indicating the result of the operation.
   */
  @PutMapping("/{creatorId}")
  public ResponseEntity<String> updateReview(
      @PathVariable Long recipeId, @PathVariable Long creatorId, @RequestBody RecipeReview review) {
    review.setRecipeId(recipeId);
    review.setCreatorId(creatorId);
    logger.info(
        "Received request to update review for recipe ID: {} by creator ID: {}",
        recipeId,
        creatorId);
    try {
      recipeReviewService.updateReview(recipeId, creatorId, review);
      logger.info("Updated review for recipe ID: {} by creator ID: {}", recipeId, creatorId);
      return new ResponseEntity<>("Review updated successfully", HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      logger.warn("Validation error occurred while updating review: {}", e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      logger.error(
          "Failed to update review for recipe ID: {} by creator ID: {}", recipeId, creatorId, e);
      return new ResponseEntity<>("Failed to update review", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Delete a specific review for a recipe by a specific creator.
   *
   * @param recipeId The ID of the recipe from which the review is being deleted.
   * @param creatorId The ID of the creator of the review to be deleted.
   * @return Response entity indicating the result of the operation.
   */
  @DeleteMapping("/{creatorId}")
  public ResponseEntity<String> deleteReview(
      @PathVariable Long recipeId, @PathVariable Long creatorId) {
    logger.info(
        "Received request to delete review for recipe ID: {} by creator ID: {}",
        recipeId,
        creatorId);
    try {
      recipeReviewService.deleteReview(recipeId, creatorId);
      logger.info("Deleted review for recipe ID: {} by creator ID: {}", recipeId, creatorId);
      return new ResponseEntity<>("Review deleted successfully", HttpStatus.OK);
    } catch (Exception e) {
      logger.error(
          "Failed to delete review for recipe ID: {} by creator ID: {}", recipeId, creatorId, e);
      return new ResponseEntity<>("Failed to delete review", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Delete all reviews for a specific recipe.
   *
   * @param recipeId The ID of the recipe for which all reviews are being deleted.
   * @return Response entity indicating the result of the operation.
   */
  @DeleteMapping
  public ResponseEntity<String> deleteReviewsByRecipeId(@PathVariable Long recipeId) {
    logger.info("Received request to delete all reviews for recipe ID: {}", recipeId);
    try {
      recipeReviewService.deleteReviewsByRecipeId(recipeId);
      logger.info("Deleted all reviews for recipe ID: {}", recipeId);
      return new ResponseEntity<>("Review deleted successfully", HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Failed to delete all reviews for recipe ID: {}", recipeId, e);
      return new ResponseEntity<>(
          "Failed to delete all reviews for a specific recipe", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Delete all reviews for a specific creator.
   *
   * @param creatorId The ID of the creator whose reviews are to be deleted.
   * @return Response entity indicating the result of the operation.
   */
  @DeleteMapping("/creator/{creatorId}")
  public ResponseEntity<String> deleteReviewsByCreator(@PathVariable Long creatorId) {
    logger.info("Received request to delete all reviews by creator ID: {}", creatorId);
    try {
      recipeReviewService.deleteReviewsByCreatorId(creatorId);
      logger.info("Deleted all reviews by creator ID: {}", creatorId);
      return new ResponseEntity<>(
          "All reviews by the same creator ID deleted successfully", HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Failed to delete reviews by creator ID: {}", creatorId, e);
      return new ResponseEntity<>("Failed to delete reviews", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Retrieve a specific review for a recipe by a specific creator.
   *
   * @param recipeId The ID of the recipe for which the review is being retrieved.
   * @param creatorId The ID of the creator of the review.
   * @return Response entity containing the review if found, or a not found status.
   */
  @GetMapping("/{creatorId}")
  public ResponseEntity<RecipeReview> getReviewByRecipeAndCreator(
      @PathVariable Long recipeId, @PathVariable Long creatorId) {
    logger.info(
        "Received request to get review for recipe ID: {} by creator ID: {}", recipeId, creatorId);
    RecipeReview review = recipeReviewService.getReviewByRecipeAndCreator(recipeId, creatorId);
    if (review != null) {
      logger.info("Retrieved review for recipe ID: {} by creator ID: {}", recipeId, creatorId);
      return new ResponseEntity<>(review, HttpStatus.OK);
    } else {
      logger.warn("No review found for recipe ID: {} by creator ID: {}", recipeId, creatorId);
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Retrieve all reviews for a specific recipe.
   *
   * @param recipeId The ID of the recipe for which reviews are being retrieved.
   * @return Response entity containing the list of reviews for the recipe.
   */
  @GetMapping
  public ResponseEntity<List<RecipeReview>> getReviewsByRecipeId(@PathVariable Long recipeId) {
    logger.info("Received request to get reviews for recipe ID: {}", recipeId);
    List<RecipeReview> reviews = recipeReviewService.getReviewsByRecipeId(recipeId);
    logger.info(
        "Retrieved reviews for recipe ID: {}, count: {}",
        recipeId,
        reviews != null ? reviews.size() : 0);
    return new ResponseEntity<>(reviews, HttpStatus.OK);
  }

  /**
   * Retrieve all reviews created by a specific creator.
   *
   * @param creatorId The ID of the creator for whom reviews are being retrieved.
   * @return Response entity containing the list of reviews created by the specified creator.
   */
  @GetMapping("/creator/{creatorId}")
  public ResponseEntity<List<RecipeReview>> getReviewsByCreatorId(@PathVariable Long creatorId) {
    logger.info("Received request to get reviews by creator ID: {}", creatorId);
    List<RecipeReview> reviews = recipeReviewService.getReviewsByCreatorId(creatorId);
    logger.info(
        "Retrieved reviews by creator ID: {}, count: {}",
        creatorId,
        reviews != null ? reviews.size() : 0);
    return new ResponseEntity<>(reviews, HttpStatus.OK);
  }
}
