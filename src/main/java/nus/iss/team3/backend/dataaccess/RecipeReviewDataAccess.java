package nus.iss.team3.backend.dataaccess;

import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.COLUMN_REVIEW_COMMENTS;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.COLUMN_REVIEW_CREATE_TIME;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.COLUMN_REVIEW_CREATOR_ID;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.COLUMN_REVIEW_ID;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.COLUMN_REVIEW_RATING;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.COLUMN_REVIEW_RECIPE_ID;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.COLUMN_REVIEW_UPDATE_TIME;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.INPUT_REVIEW_COMMENTS;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.INPUT_REVIEW_CREATOR_ID;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.INPUT_REVIEW_RATING;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.INPUT_REVIEW_RECIPE_ID;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.SQL_REVIEW_ADD;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.SQL_REVIEW_DELETE;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.SQL_REVIEW_DELETE_BY_CREATOR_ID;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.SQL_REVIEW_DELETE_BY_RECIPE_ID;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.SQL_REVIEW_GET_BY_CREATOR_ID;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.SQL_REVIEW_GET_BY_RECIPE_AND_CREATOR;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.SQL_REVIEW_GET_BY_RECIPE_ID;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.SQL_REVIEW_UPDATE;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nus.iss.team3.backend.dataaccess.postgres.PostgresDataAccess;
import nus.iss.team3.backend.entity.RecipeReview;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class RecipeReviewDataAccess implements IRecipeReviewDataAccess {

  private static final Logger logger = LogManager.getLogger(RecipeReviewDataAccess.class);
  private final PostgresDataAccess postgresDataAccess;

  public RecipeReviewDataAccess(PostgresDataAccess postgresDataAccess) {
    this.postgresDataAccess = postgresDataAccess;
  }

  @Override
  @Transactional
  public void addReview(RecipeReview review) {
    logger.info("Starting to add review for recipe ID: {}", review.getRecipeId());
    try {
      validateReview(review);
      Map<String, Object> params = new HashMap<>();
      params.put(INPUT_REVIEW_RECIPE_ID, review.getRecipeId());
      params.put(INPUT_REVIEW_CREATOR_ID, review.getCreatorId());
      params.put(INPUT_REVIEW_RATING, review.getRating());
      params.put(INPUT_REVIEW_COMMENTS, review.getComments());

      int result = postgresDataAccess.upsertStatement(SQL_REVIEW_ADD, params);
      if (result > 0) {
        logger.info("Added review for recipe ID: {}", review.getRecipeId());
      } else {
        logger.error("Failed to add review for recipe ID: {}", review.getRecipeId());
      }
    } catch (Exception e) {
      logger.error("Exception occurred while adding review: {}", e.getMessage(), e);
      throw e; // Rethrow the exception to trigger transaction rollback
    }
  }

  @Override
  @Transactional
  public void updateReview(Long recipeId, Long creatorId, RecipeReview review) {
    logger.info(
        "Starting to update review for recipe ID: {} by creator ID: {}", recipeId, creatorId);
    try {
      validateReview(review);
      Map<String, Object> params = new HashMap<>();
      params.put(INPUT_REVIEW_RATING, review.getRating());
      params.put(INPUT_REVIEW_COMMENTS, review.getComments());
      params.put(INPUT_REVIEW_RECIPE_ID, recipeId);
      params.put(INPUT_REVIEW_CREATOR_ID, creatorId);

      int result = postgresDataAccess.upsertStatement(SQL_REVIEW_UPDATE, params);
      if (result > 0) {
        logger.info("Updated review for recipe ID: {} by creator ID: {}", recipeId, creatorId);
      } else {
        logger.error(
            "Failed to update review for recipe ID: {} by creator ID: {}", recipeId, creatorId);
      }

    } catch (Exception e) {
      logger.error("Exception occurred while updating review: {}", e.getMessage(), e);
      throw e; // Rethrow the exception to trigger transaction rollback
    }
  }

  @Override
  @Transactional
  public void deleteReview(Long recipeId, Long creatorId) {
    logger.info(
        "Starting to delete review for recipe ID: {} by creator ID: {}", recipeId, creatorId);
    try {
      Map<String, Object> params = new HashMap<>();
      params.put(INPUT_REVIEW_RECIPE_ID, recipeId);
      params.put(INPUT_REVIEW_CREATOR_ID, creatorId);

      int result = postgresDataAccess.upsertStatement(SQL_REVIEW_DELETE, params);
      if (result > 0) {
        logger.info("Deleted review for recipe ID: {} by creator ID: {}", recipeId, creatorId);
      } else {
        logger.error(
            "Failed to delete review for recipe ID: {} by creator ID: {}", recipeId, creatorId);
      }
    } catch (Exception e) {
      logger.error("Exception occurred while deleting review: {}", e.getMessage(), e);
      throw e; // Rethrow the exception to trigger transaction rollback
    }
  }

  @Override
  @Transactional
  public void deleteReviewsByRecipeId(Long recipeId) {
    logger.info("Starting to delete all reviews for recipe ID: {}", recipeId);
    try {
      Map<String, Object> params = new HashMap<>();
      params.put(INPUT_REVIEW_RECIPE_ID, recipeId);

      int result = postgresDataAccess.upsertStatement(SQL_REVIEW_DELETE_BY_RECIPE_ID, params);
      if (result > 0) {
        logger.info("Deleted all reviews for recipe ID: {}", recipeId);
      } else {
        logger.error("Failed to delete all reviews for recipe ID: {}", recipeId);
      }
    } catch (Exception e) {
      logger.error("Exception occurred while deleting reviews by recipe ID: {}", e.getMessage(), e);
      throw e; // Rethrow the exception to trigger transaction rollback
    }
  }

  @Override
  @Transactional
  public void deleteReviewsByCreatorId(Long creatorId) {
    logger.info("Starting to delete all reviews by creator ID: {}", creatorId);
    try {
      Map<String, Object> params = new HashMap<>();
      params.put(INPUT_REVIEW_CREATOR_ID, creatorId);

      int result = postgresDataAccess.upsertStatement(SQL_REVIEW_DELETE_BY_CREATOR_ID, params);
      if (result > 0) {
        logger.info("Deleted all reviews by creator ID: {}", creatorId);
      } else {
        logger.error("Failed to delete all reviews by creator ID: {}", creatorId);
      }
    } catch (Exception e) {
      logger.error(
          "Exception occurred while deleting reviews by creator ID: {}", e.getMessage(), e);
      throw e; // Rethrow the exception to trigger transaction rollback
    }
  }

  @Override
  public RecipeReview getReviewByRecipeAndCreator(Long recipeId, Long creatorId) {
    logger.info("Fetching review for recipe ID: {} by creator ID: {}", recipeId, creatorId);
    try {
      Map<String, Object> params = new HashMap<>();
      params.put(INPUT_REVIEW_RECIPE_ID, recipeId);
      params.put(INPUT_REVIEW_CREATOR_ID, creatorId);
      List<Map<String, Object>> results =
          postgresDataAccess.queryStatement(SQL_REVIEW_GET_BY_RECIPE_AND_CREATOR, params);
      if (results != null && !results.isEmpty()) {
        return mapRowToReview(results.getFirst());
      }
    } catch (Exception e) {
      logger.error("Exception occurred while fetching review: {}", e.getMessage(), e);
    }
    return null;
  }

  @Override
  public List<RecipeReview> getReviewsByRecipeId(Long recipeId) {
    logger.info("Fetching reviews for recipe ID: {}", recipeId);
    try {
      Map<String, Object> params = new HashMap<>();
      params.put(INPUT_REVIEW_RECIPE_ID, recipeId);
      List<Map<String, Object>> results =
          postgresDataAccess.queryStatement(SQL_REVIEW_GET_BY_RECIPE_ID, params);
      if (results != null) {
        return mapRowsToReviews(results);
      }
    } catch (Exception e) {
      logger.error("Exception occurred while fetching reviews by recipe ID: {}", e.getMessage(), e);
    }
    return null;
  }

  @Override
  public List<RecipeReview> getReviewsByCreatorId(Long creatorId) {
    logger.info("Fetching reviews by creator ID: {}", creatorId);

    try {
      Map<String, Object> params = new HashMap<>();
      params.put(INPUT_REVIEW_CREATOR_ID, creatorId);
      List<Map<String, Object>> results =
          postgresDataAccess.queryStatement(SQL_REVIEW_GET_BY_CREATOR_ID, params);
      if (results != null) {
        return mapRowsToReviews(results);
      }
    } catch (Exception e) {
      logger.error(
          "Exception occurred while fetching reviews by creator ID: {}", e.getMessage(), e);
    }
    return null;
  }

  private RecipeReview mapRowToReview(Map<String, Object> row) {
    logger.debug("Mapping database record to RecipeReview object");
    logger.info("mapRowToReview: {}", row);
    RecipeReview review = new RecipeReview();

    review.setId(
        row.get(COLUMN_REVIEW_ID) != null
            ? ((Number) row.get(COLUMN_REVIEW_ID)).longValue()
            : null);
    review.setRecipeId(
        row.get(COLUMN_REVIEW_RECIPE_ID) != null
            ? ((Number) row.get(COLUMN_REVIEW_RECIPE_ID)).longValue()
            : null);
    review.setCreatorId(
        row.get(COLUMN_REVIEW_CREATOR_ID) != null
            ? ((Number) row.get(COLUMN_REVIEW_CREATOR_ID)).longValue()
            : null);
    review.setRating(
        row.get(COLUMN_REVIEW_RATING) != null
            ? ((Number) row.get(COLUMN_REVIEW_RATING)).doubleValue()
            : null);
    review.setCreateDatetime(
        row.get(COLUMN_REVIEW_CREATE_TIME) != null
            ? (Timestamp) row.get(COLUMN_REVIEW_CREATE_TIME)
            : null);
    review.setUpdateDatetime(
        row.get(COLUMN_REVIEW_UPDATE_TIME) != null
            ? (Timestamp) row.get(COLUMN_REVIEW_UPDATE_TIME)
            : null);
    review.setComments(
        row.get(COLUMN_REVIEW_COMMENTS) != null ? (String) row.get(COLUMN_REVIEW_COMMENTS) : null);

    logger.debug("RecipeReview object mapping completed: ID={}", review.getId());
    return review;
  }

  private List<RecipeReview> mapRowsToReviews(List<Map<String, Object>> rows) {
    List<RecipeReview> reviews = new ArrayList<>();
    for (Map<String, Object> row : rows) {
      reviews.add(mapRowToReview(row));
    }
    return reviews;
  }

  private void validateReview(RecipeReview review) {
    logger.debug("Validating review for recipe ID: {}", review.getRecipeId());
    if (review.getRecipeId() == null
        || review.getCreatorId() == null
        || review.getRating() == null) {
      logger.error("Review validation failed, required fields are empty");
      throw new IllegalArgumentException("Required fields for review cannot be null");
    }
  }
}
