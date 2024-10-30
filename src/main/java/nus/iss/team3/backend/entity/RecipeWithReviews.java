package nus.iss.team3.backend.entity;

import java.util.List;

/**
 * Recipe with Review Class
 *
 * @author Mao Weining
 */
// Entity
public class RecipeWithReviews {

  private Recipe recipe;
  private List<RecipeReview> reviews;

  public RecipeWithReviews() {}

  public RecipeWithReviews(Recipe recipe, List<RecipeReview> reviews) {
    this.recipe = recipe;
    this.reviews = reviews;
  }

  public Recipe getRecipe() {
    return recipe;
  }

  public void setRecipe(Recipe recipe) {
    this.recipe = recipe;
  }

  public List<RecipeReview> getReviews() {
    return reviews;
  }

  public void setReviews(List<RecipeReview> reviews) {
    this.reviews = reviews;
  }
}
