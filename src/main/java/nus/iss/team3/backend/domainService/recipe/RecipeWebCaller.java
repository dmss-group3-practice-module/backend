package nus.iss.team3.backend.domainService.recipe;

import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import nus.iss.team3.backend.ProfileConfig;
import nus.iss.team3.backend.domainService.webservice.IWebserviceCaller;
import nus.iss.team3.backend.entity.Recipe;
import nus.iss.team3.backend.entity.RecipeWithReviews;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Implementation of IRecipeService that calls REST APIs using IWebserviceCallerGen.
 *
 * @author Mao Weining
 */
@Service
@Profile(ProfileConfig.PROFILE_NOT + ProfileConfig.PROFILE_RECIPE)
public class RecipeWebCaller implements IRecipeService {

  private static final Logger logger = LogManager.getLogger(RecipeWebCaller.class);
  private final IWebserviceCaller webServiceCaller;
  private final String serviceUrl;
  private final String servicePort;

  public RecipeWebCaller(
      @Value("${service.url.recipe.address}") String serviceUrl,
      @Value("${service.url.recipe.port}") String servicePort,
      IWebserviceCaller webServiceCaller) {
    this.serviceUrl = serviceUrl;
    this.servicePort = servicePort;
    this.webServiceCaller = webServiceCaller;
  }

  @PostConstruct
  public void postContruct() {
    logger.info("Recipe Service Web Caller initialized.");
  }

  private String getUrl(String path) {
    return "http://" + serviceUrl + ":" + servicePort + path;
  }

  @Override
  public boolean addRecipe(Recipe recipe) {
    String url = getUrl("/recipe");
    try {
      ResponseEntity<Recipe> response = webServiceCaller.postCall(url, recipe, Recipe.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        logger.info("Recipe added successfully.");
        return true;
      } else {
        logger.error("Failed to add recipe. Status code: {}", response.getStatusCode());
        return false;
      }
    } catch (Exception e) {
      logger.error("Error adding recipe: {}", e.getMessage());
      return false;
    }
  }

  @Override
  public boolean updateRecipe(Recipe recipe) {
    if (recipe.getId() == null) {
      logger.error("Recipe ID must not be null for update operation.");
      return false;
    }
    String url = getUrl("/recipe/" + recipe.getId());
    try {
      ResponseEntity<String> response = webServiceCaller.putCall(url, recipe, String.class);
      if (response.getStatusCode().is2xxSuccessful()) {
        logger.info("Recipe updated successfully.");
        return true;
      } else {
        logger.error("Failed to update recipe. Status code: {}", response.getStatusCode());
        return false;
      }
    } catch (Exception e) {
      logger.error("Error updating recipe with ID {}: {}", recipe.getId(), e.getMessage());
      return false;
    }
  }

  @Override
  public boolean deleteRecipeById(Long recipeId) {
    String url = getUrl("/recipe/" + recipeId);
    try {
      ResponseEntity<String> response = webServiceCaller.deleteCall(url, String.class);
      if (response.getStatusCode().is2xxSuccessful()) {
        logger.info("Recipe deleted successfully.");
        return true;
      } else {
        logger.error("Failed to delete recipe. Status code: {}", response.getStatusCode());
        return false;
      }
    } catch (Exception e) {
      logger.error("Error deleting recipe with ID {}: {}", recipeId, e.getMessage());
      return false;
    }
  }

  @Override
  public Recipe getRecipeById(Long recipeId) {
    String url = getUrl("/recipe/" + recipeId);
    try {
      ResponseEntity<Recipe> response = webServiceCaller.getCall(url, Recipe.class);
      if (response.getStatusCode().is2xxSuccessful()) {
        return response.getBody();
      } else {
        logger.error("Failed to retrieve recipe. Status code: {}", response.getStatusCode());
        return null;
      }
    } catch (Exception e) {
      logger.error("Error retrieving recipe by ID {}: {}", recipeId, e.getMessage());
      return null;
    }
  }

  @Override
  public List<Recipe> getAllRecipes() {
    String url = getUrl("/recipe");
    try {
      ParameterizedTypeReference<List<Recipe>> typeRef = new ParameterizedTypeReference<>() {};
      ResponseEntity<List<Recipe>> response = webServiceCaller.getCall(url, typeRef);
      if (response.getStatusCode().is2xxSuccessful()) {
        return response.getBody();
      } else {
        logger.error("Failed to retrieve all recipes. Status code: {}", response.getStatusCode());
        return Collections.emptyList();
      }
    } catch (Exception e) {
      logger.error("Error retrieving all recipes: {}", e.getMessage());
      return Collections.emptyList();
    }
  }

  @Override
  public List<Recipe> getAllPublishedRecipes() {
    String url = getUrl("/recipe/published");
    try {
      ParameterizedTypeReference<List<Recipe>> typeRef = new ParameterizedTypeReference<>() {};
      ResponseEntity<List<Recipe>> response = webServiceCaller.getCall(url, typeRef);
      if (response.getStatusCode().is2xxSuccessful()) {
        return response.getBody();
      } else {
        logger.error(
            "Failed to retrieve all published recipes. Status code: {}", response.getStatusCode());
        return Collections.emptyList();
      }
    } catch (Exception e) {
      logger.error("Error retrieving all published recipes: {}", e.getMessage());
      return Collections.emptyList();
    }
  }

  @Override
  public List<Recipe> getRecipesByName(String name) {
    String url = getUrl("/recipe/search?name=" + name);
    try {
      ParameterizedTypeReference<List<Recipe>> typeRef = new ParameterizedTypeReference<>() {};
      ResponseEntity<List<Recipe>> response = webServiceCaller.getCall(url, typeRef);
      if (response.getStatusCode().is2xxSuccessful()) {
        return response.getBody();
      } else {
        logger.error(
            "Failed to retrieve recipes by name '{}'. Status code: {}",
            name,
            response.getStatusCode());
        return Collections.emptyList();
      }
    } catch (Exception e) {
      logger.error("Error retrieving recipes by name '{}': {}", name, e.getMessage());
      return Collections.emptyList();
    }
  }

  @Override
  public List<Recipe> getRecipesByCreatorId(int creatorId) {
    String url = getUrl("/recipe/creator/" + creatorId);
    try {
      ParameterizedTypeReference<List<Recipe>> typeRef = new ParameterizedTypeReference<>() {};
      ResponseEntity<List<Recipe>> response = webServiceCaller.getCall(url, typeRef);
      if (response.getStatusCode().is2xxSuccessful()) {
        return response.getBody();
      } else {
        logger.error(
            "Failed to retrieve recipes by creator ID {}. Status code: {}",
            creatorId,
            response.getStatusCode());
        return Collections.emptyList();
      }
    } catch (Exception e) {
      logger.error("Error retrieving recipes by creator ID {}: {}", creatorId, e.getMessage());
      return Collections.emptyList();
    }
  }

  @Override
  public RecipeWithReviews getRecipeWithReviewsById(Long recipeId) {
    String url = getUrl("/recipe/" + recipeId + "/with-reviews");
    try {
      ResponseEntity<RecipeWithReviews> response =
          webServiceCaller.getCall(url, RecipeWithReviews.class);
      if (response.getStatusCode().is2xxSuccessful()) {
        return response.getBody();
      } else {
        logger.error(
            "Failed to retrieve recipe with reviews. Status code: {}", response.getStatusCode());
        return null;
      }
    } catch (Exception e) {
      logger.error("Error retrieving recipe with reviews by ID {}: {}", recipeId, e.getMessage());
      return null;
    }
  }

  @Override
  public List<RecipeWithReviews> getAllRecipesWithReviews() {
    String url = getUrl("/recipe/with-reviews");
    try {
      ParameterizedTypeReference<List<RecipeWithReviews>> typeRef =
          new ParameterizedTypeReference<>() {};
      ResponseEntity<List<RecipeWithReviews>> response = webServiceCaller.getCall(url, typeRef);
      if (response.getStatusCode().is2xxSuccessful()) {
        return response.getBody();
      } else {
        logger.error(
            "Failed to retrieve all recipes with reviews. Status code: {}",
            response.getStatusCode());
        return Collections.emptyList();
      }
    } catch (Exception e) {
      logger.error("Error retrieving all recipes with reviews: {}", e.getMessage());
      return Collections.emptyList();
    }
  }
}
