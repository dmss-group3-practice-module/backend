package nus.iss.team3.backend.businessService.recipeReview;

import java.util.List;
import nus.iss.team3.backend.entity.RecipeWithReviews;

public interface IRecipeReviewService {

  // Add new method to get recipe with its reviews.
  RecipeWithReviews getRecipeWithReviewsById(Long recipeId);

  List<RecipeWithReviews> getAllRecipesWithReviews();
}
