package nus.iss.team3.backend.dataaccess;

import java.util.List;
import nus.iss.team3.backend.entity.RecipeReview;

/**
 * Interface class for RecipeReviewDataAccess, should contain all functionality needed for recipe.
 *
 * @author Mao Weining
 */
public interface IReviewDataAccess {

  void addReview(RecipeReview review);

  void updateReview(Long recipeId, Long creatorId, RecipeReview review);

  void deleteReview(Long recipeId, Long creatorId);

  void deleteReviewsByRecipeId(Long recipeId);

  void deleteReviewsByCreatorId(Long creatorId);

  RecipeReview getReviewByRecipeAndCreator(Long recipeId, Long creatorId);

  List<RecipeReview> getReviewsByRecipeId(Long recipeId);

  List<RecipeReview> getReviewsByCreatorId(Long creatorId);
}
