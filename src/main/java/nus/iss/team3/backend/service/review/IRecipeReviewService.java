package nus.iss.team3.backend.service.review;

import java.util.List;
import nus.iss.team3.backend.entity.RecipeReview;

/**
 * Interface for RecipeReviewService, contains logic involving recipe reviews.
 *
 * @author Mao Weining
 */
public interface IRecipeReviewService {

  void addReview(RecipeReview review);

  void updateReview(Long recipeId, Long creatorId, RecipeReview review);

  void deleteReview(Long recipeId, Long creatorId);

  void deleteReviewsByRecipeId(Long recipeId);

  void deleteReviewsByCreatorId(Long recipeId, Long creatorId);

  RecipeReview getReviewByRecipeAndCreator(Long recipeId, Long creatorId);

  List<RecipeReview> getReviewsByRecipeId(Long recipeId);

  List<RecipeReview> getReviewsByCreatorId(Long recipeId, Long creatorId);
}
