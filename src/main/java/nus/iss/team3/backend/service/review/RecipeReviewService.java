package nus.iss.team3.backend.service.review;

import java.util.List;
import java.util.Objects;
import nus.iss.team3.backend.dataaccess.IRecipeReviewDataAccess;
import nus.iss.team3.backend.entity.RecipeReview;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * Service class with logic for handling recipe review-related queries.
 *
 * @author Mao Weining
 */
@Service
// TODO: Activate Spring Profile
// @Profile("recipe")
// @Profile(ProfileConfig.PROFILE_RECIPE)
public class RecipeReviewService implements IRecipeReviewService {

  private static final Logger logger = LogManager.getLogger(RecipeReviewService.class);
  private final IRecipeReviewDataAccess recipeReviewDataAccess;

  public RecipeReviewService(IRecipeReviewDataAccess recipeReviewDataAccess) {
    this.recipeReviewDataAccess = recipeReviewDataAccess;
  }

  @Override
  public void addReview(RecipeReview review) {
    validateReview(review);
    recipeReviewDataAccess.addReview(review);
    logger.info("Added review for recipe ID: {}", review.getRecipeId());
  }

  @Override
  public void updateReview(Long recipeId, Long creatorId, RecipeReview review) {
    validateReview(review);
    recipeReviewDataAccess.updateReview(recipeId, creatorId, review);
    logger.info("Updated review for recipe ID: {} by creator ID: {}", recipeId, creatorId);
  }

  @Override
  public void deleteReview(Long recipeId, Long creatorId) {
    Objects.requireNonNull(recipeId, "Recipe ID cannot be null");
    Objects.requireNonNull(creatorId, "Creator ID cannot be null");
    recipeReviewDataAccess.deleteReview(recipeId, creatorId);
    logger.info("Deleted review for recipe ID: {} by creator ID: {}", recipeId, creatorId);
  }

  @Override
  public void deleteReviewsByRecipeId(Long recipeId) {
    Objects.requireNonNull(recipeId, "Recipe ID cannot be null");
    recipeReviewDataAccess.deleteReviewsByRecipeId(recipeId);
    logger.info("Deleted all reviews for recipe ID: {}", recipeId);
  }

  @Override
  public void deleteReviewsByCreatorId(Long recipeId, Long creatorId) {
    Objects.requireNonNull(recipeId, "Recipe ID cannot be null");
    Objects.requireNonNull(creatorId, "Creator ID cannot be null");
    recipeReviewDataAccess.deleteReviewsByCreatorId(creatorId);
    logger.info("Deleted all reviews by creator ID: {}", creatorId);
  }

  @Override
  public RecipeReview getReviewByRecipeAndCreator(Long recipeId, Long creatorId) {
    Objects.requireNonNull(recipeId, "Recipe ID cannot be null");
    Objects.requireNonNull(creatorId, "Creator ID cannot be null");
    RecipeReview review = recipeReviewDataAccess.getReviewByRecipeAndCreator(recipeId, creatorId);
    if (review != null) {
      logger.info("Retrieved review for recipe ID: {} by creator ID: {}", recipeId, creatorId);
    } else {
      logger.warn("No review found for recipe ID: {} by creator ID: {}", recipeId, creatorId);
    }
    return review;
  }

  @Override
  public List<RecipeReview> getReviewsByRecipeId(Long recipeId) {
    Objects.requireNonNull(recipeId, "Recipe ID cannot be null");
    List<RecipeReview> reviews = recipeReviewDataAccess.getReviewsByRecipeId(recipeId);
    logger.info(
        "Retrieved reviews for recipe ID: {}, count: {}",
        recipeId,
        reviews != null ? reviews.size() : 0);
    return reviews;
  }

  @Override
  public List<RecipeReview> getReviewsByCreatorId(Long recipeId, Long creatorId) {
    Objects.requireNonNull(recipeId, "Recipe ID cannot be null");
    Objects.requireNonNull(creatorId, "Creator ID cannot be null");
    List<RecipeReview> reviews = recipeReviewDataAccess.getReviewsByCreatorId(creatorId);
    logger.info(
        "Retrieved reviews by creator ID: {}, count: {}",
        creatorId,
        reviews != null ? reviews.size() : 0);
    return reviews;
  }

  private void validateReview(RecipeReview review) {
    if (review == null) {
      logger.error("Attempted to add or update a null review");
      throw new IllegalArgumentException("Review cannot be null");
    }
    if (review.getRecipeId() == null) {
      logger.error("Attempted to add or update a review without a recipe ID");
      throw new IllegalArgumentException("Recipe ID cannot be null");
    }
    if (review.getCreatorId() == null) {
      logger.error("Attempted to add or update a review without a creator ID");
      throw new IllegalArgumentException("Creator ID cannot be null");
    }
    if (review.getRating() == null || review.getRating() < 0 || review.getRating() > 5) {
      logger.error("Attempted to add or update a review with an invalid rating");
      throw new IllegalArgumentException("Rating must be between 0 and 5");
    }
  }
}
