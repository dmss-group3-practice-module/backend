package nus.iss.team3.backend.businessService.recipeReview;

import java.util.ArrayList;
import java.util.List;
import nus.iss.team3.backend.domainService.recipe.IRecipeService;
import nus.iss.team3.backend.domainService.review.IReviewService;
import nus.iss.team3.backend.entity.Recipe;
import nus.iss.team3.backend.entity.RecipeReview;
import nus.iss.team3.backend.entity.RecipeWithReviews;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class RecipeReviewService implements IRecipeReviewService {
  private static final Logger logger = LogManager.getLogger(RecipeReviewService.class);

  private final IRecipeService recipeService;
  private final IReviewService reviewService;

  public RecipeReviewService(IRecipeService recipeService, IReviewService reviewService) {
    this.recipeService = recipeService;
    this.reviewService = reviewService;
  }

  @Override
  public RecipeWithReviews getRecipeWithReviewsById(Long recipeId) {
    logger.info("Fetching recipe with ID: {}", recipeId);

    // Get Recipe
    Recipe recipe = recipeService.getRecipeById(recipeId);
    if (recipe == null) {
      logger.warn("Recipe with ID: {} not found", recipeId);
      return null;
    }

    // Get relevant Reviews
    List<RecipeReview> reviews = reviewService.getReviewsByRecipeId(recipeId);
    logger.info("Fetched {} reviews for recipe ID: {}", reviews.size(), recipeId);

    // Returns a combination of Recipe and Reviews
    return new RecipeWithReviews(recipe, reviews);
  }

  @Override
  public List<RecipeWithReviews> getAllRecipesWithReviews() {
    logger.info("Fetching all recipes with reviews");

    // Get all Recipes
    List<Recipe> recipes = recipeService.getAllRecipes();
    List<RecipeWithReviews> recipesWithReviews = new ArrayList<>();

    for (Recipe recipe : recipes) {
      // Get Reviews for each Recipe
      List<RecipeReview> reviews = reviewService.getReviewsByRecipeId(recipe.getId());
      // Combining Recipes and Reviews
      recipesWithReviews.add(new RecipeWithReviews(recipe, reviews));
      logger.info("Fetched {} all reviews for one recipe ID: {}", reviews.size(), recipe.getId());
    }

    logger.info("Fetched {} recipes with reviews", recipesWithReviews.size());
    return recipesWithReviews;
  }

  @Override
  public void addReview(RecipeReview review) {

    reviewService.addReview(review);

    // update recipe rating: get all reviews for the recipe, calculate the average rating and update
    // the recipe

    // not implementing this as a domain event, cause not pub sub....
    if (review == null || review.getRecipeId() == null) {
      // unable to continue do rating.
      return;
    }
    List<RecipeReview> reviews = reviewService.getReviewsByRecipeId(review.getRecipeId());

    if (reviews == null || reviews.isEmpty()) {
      return;
    }
    double totalRating = 0;
    for (RecipeReview r : reviews) {
      if (r.getRating() == null) {
        continue;
      }
      totalRating += r.getRating();
    }
    double avgRating = totalRating / reviews.size();

    recipeService.updateRecipeRating(review.getRecipeId(), avgRating);
  }
}
