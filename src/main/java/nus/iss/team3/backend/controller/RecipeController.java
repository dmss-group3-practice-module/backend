package nus.iss.team3.backend.controller;

import java.util.List;
import nus.iss.team3.backend.businessService.recipeReview.IRecipeReviewService;
import nus.iss.team3.backend.domainService.recipe.IRecipeService;
import nus.iss.team3.backend.entity.Recipe;
import nus.iss.team3.backend.entity.RecipeWithReviews;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class to handle web call for recipe related queries.
 *
 * @author Mao Weining
 */
@RestController
// TODO: set Config profile
// @Profile(ProfileConfig.PROFILE_RECIPE)
// Set the base URL path to /recipe, all mappings will be based on this path
@RequestMapping("/recipe")
public class RecipeController {

  private static final Logger logger = LogManager.getLogger(RecipeController.class);
  private final IRecipeService recipeService;
  private final IRecipeReviewService recipeReviewService;

  @Autowired
  public RecipeController(IRecipeService recipeService, IRecipeReviewService recipeReviewService) {
    this.recipeService = recipeService;
    this.recipeReviewService = recipeReviewService;
  }

  /**
   * Add a new recipe.
   *
   * @param recipe The recipe object obtained from the request body.
   * @return Response entity indicating the result of the operation.
   */
  @PostMapping
  public ResponseEntity<String> addRecipe(@RequestBody Recipe recipe) {
    /* Notices:
     * 1. PostMapping Note: Map the HTTP POST request to this method, and the path is /api/recipes.
     * 2. RequestBody Recipe: Bind and deserialize JSON data from the request body to the Recipe object.
     * */
    logger.info("Received request to add recipe: {}", recipe.getName());
    try {
      recipeService.addRecipe(recipe);
      logger.info("Recipe added successfully: {}", recipe.getName());
      return new ResponseEntity<>("Recipe added successfully", HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      logger.warn("Validation error occurred while adding recipe: {}", e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Failed to add recipe: {}", e.getMessage(), e);
      return new ResponseEntity<>("Failed to add recipe", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Update an existing recipe.
   *
   * @param id The recipe ID from the path variable.
   * @param recipe The recipe object obtained from the request body.
   * @return Response entity indicating the result of the operation.
   */
  @PutMapping("/{id}")
  public ResponseEntity<String> updateRecipe(@PathVariable Long id, @RequestBody Recipe recipe) {
    logger.info("Received request to update recipe: ID={}, Name={}", id, recipe.getName());
    try {
      recipe.setId(id);
      boolean updated = recipeService.updateRecipe(recipe);
      if (!updated) {
        return new ResponseEntity<>("Failed to update recipe", HttpStatus.INTERNAL_SERVER_ERROR);
      }
      logger.info("Recipe updated successfully: ID={}, Name={}", id, recipe.getName());
      return new ResponseEntity<>("Recipe updated successfully", HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      logger.warn("Validation error occurred while updating recipe: {}", e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Failed to update recipe: {}", e.getMessage(), e);
      return new ResponseEntity<>("Failed to update recipe", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Delete a recipe by ID.
   *
   * @param id The recipe ID from the path variable.
   * @return Response entity indicating the result of the operation.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteRecipe(@PathVariable Long id) {
    logger.info("Received request to delete recipe: ID={}", id);
    try {
      recipeService.deleteRecipeById(id);
      logger.info("Recipe deleted successfully: ID={}", id);
      return new ResponseEntity<>("Recipe deleted successfully", HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      logger.warn("Validation error occurred while deleting recipe: {}", e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Failed to delete recipe: {}", e.getMessage(), e);
      return new ResponseEntity<>("Failed to delete recipe", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Get a specific recipe by ID.
   *
   * @param id The recipe ID from the path variable.
   * @return Response entity containing the recipe.
   */
  @GetMapping("/{id}")
  public ResponseEntity<Recipe> getRecipe(@PathVariable Long id) {
    logger.info("Received request to get recipe: ID={}", id);
    try {
      Recipe recipe = recipeService.getRecipeById(id);
      if (recipe != null) {
        logger.info("Found recipe: ID={}, Name={}", recipe.getId(), recipe.getName());
        return new ResponseEntity<>(recipe, HttpStatus.OK);
      } else {
        logger.warn("Recipe with ID {} not found", id);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      logger.error("Failed to get recipe: {}", e.getMessage(), e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Get all recipes.
   *
   * @return Response entity containing the list of all recipes.
   */
  @GetMapping
  public ResponseEntity<List<Recipe>> getAllRecipes() {
    logger.info("Received request to get all recipes");
    try {
      List<Recipe> recipes = recipeService.getAllRecipes();
      logger.info("Found {} recipes", recipes.size());
      return new ResponseEntity<>(recipes, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Failed to get all recipes: {}", e.getMessage(), e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Get all published recipes.
   *
   * @return Response entity containing the list of all published recipes.
   */
  @GetMapping("/published")
  public ResponseEntity<List<Recipe>> getAllPublishedRecipes() {
    logger.info("Received request to get all published recipes");
    try {
      List<Recipe> recipes = recipeService.getAllPublishedRecipes();
      logger.info("{} published recipes were found", recipes.size());
      return new ResponseEntity<>(recipes, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Failed to get all published recipes: {}", e.getMessage(), e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Search for recipes by name.
   *
   * @param name The recipe name from the request parameter.
   * @return Response entity containing the matching recipes.
   */
  @GetMapping("/search")
  public ResponseEntity<List<Recipe>> searchRecipes(@RequestParam String name) {
    logger.info("Received request to search recipes: Name={}", name);
    try {
      List<Recipe> recipes = recipeService.getRecipesByName(name);
      logger.info("Found {} recipes containing '{}'", recipes.size(), name);
      return new ResponseEntity<>(recipes, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Failed to search recipes: {}", e.getMessage(), e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Get all recipes from the same creator by creatorId.
   *
   * @param creatorId The recipe ID from the path variable.
   * @return Response entity containing the list of all recipes from the same creator.
   */
  @GetMapping("/creator/{creatorId}")
  public ResponseEntity<List<Recipe>> searchRecipesByCreatorId(@PathVariable int creatorId) {
    logger.info("Received request to retrieve recipes by creator Id: creatorId={}", creatorId);
    try {
      List<Recipe> recipes = recipeService.getRecipesByCreatorId(creatorId);
      logger.info("Found {} recipes under '{}'", recipes != null ? recipes.size() : 0, creatorId);
      return new ResponseEntity<>(recipes, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Failed to search recipes: {}", e.getMessage(), e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Get a specific recipe by ID along with its reviews.
   *
   * @param id The recipe ID from the path variable.
   * @return Response entity containing the recipe with reviews.
   */
  @GetMapping("/{id}/with-reviews")
  public ResponseEntity<RecipeWithReviews> getRecipeWithReviews(@PathVariable Long id) {
    logger.info("Received request to get recipe with reviews: ID={}", id);
    try {
      RecipeWithReviews recipeWithReviews = recipeReviewService.getRecipeWithReviewsById(id);
      if (recipeWithReviews != null) {
        logger.info(
            "Found recipe with reviews: ID={}, Name={}",
            recipeWithReviews.getRecipe().getId(),
            recipeWithReviews.getRecipe().getName());
        return new ResponseEntity<>(recipeWithReviews, HttpStatus.OK);
      } else {
        logger.warn("Recipe with ID {} not found ", id);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      logger.error("Failed to get recipe with reviews: {}", e.getMessage(), e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Get all recipes along with their reviews.
   *
   * @return Response entity containing the list of all recipes with reviews.
   */
  @GetMapping("/with-reviews")
  public ResponseEntity<List<RecipeWithReviews>> getAllRecipesWithReviews() {
    logger.info("Received request to get all recipes with reviews");
    try {
      List<RecipeWithReviews> recipesWithReviews = recipeReviewService.getAllRecipesWithReviews();
      logger.info("Found {} recipes with reviews", recipesWithReviews.size());
      return new ResponseEntity<>(recipesWithReviews, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Failed to get all recipes with reviews: {}", e.getMessage(), e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
