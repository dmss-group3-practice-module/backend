package nus.iss.team3.backend.service.review;

import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import nus.iss.team3.backend.entity.RecipeReview;
import nus.iss.team3.backend.service.webservice.IWebserviceCaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Implementation of IRecipeReviewService that calls REST APIs using IWebserviceCallerGen.
 *
 * @author Mao Weining
 */
@Service
// TODO: Activate Spring Profile
// @Profile("!recipe")
// @Profile(ProfileConfig.PROFILE_NOT + ProfileConfig.PROFILE_RECIPE)
public class RecipeReviewWebCaller implements IRecipeReviewService {

  private static final Logger logger = LogManager.getLogger(RecipeReviewWebCaller.class);
  private final IWebserviceCaller webServiceCaller;
  private final String serviceUrl;
  private final String servicePort;

  public RecipeReviewWebCaller(
      @Value("${service.url.recipe.address}") String serviceUrl,
      @Value("${service.url.recipe.port}") String servicePort,
      IWebserviceCaller webServiceCaller) {
    this.serviceUrl = serviceUrl;
    this.servicePort = servicePort;
    this.webServiceCaller = webServiceCaller;
  }

  private String getUrl(String path) {
    return "http://" + serviceUrl + ":" + servicePort + path;
  }

  @PostConstruct
  public void postConstruct() {
    logger.info("Recipe Review Service Web Caller initialized.");
  }

  @Override
  public void addReview(RecipeReview review) {
    Long recipeId = review.getRecipeId();
    String url = getUrl("/recipe/" + recipeId + "/reviews");
    try {
      ResponseEntity<String> response = webServiceCaller.postCall(url, review, String.class);
      if (response.getStatusCode().is2xxSuccessful()) {
        logger.info("Review added successfully for recipe ID: {}", recipeId);
      } else {
        logger.error("Failed to add review. Status code: {}", response.getStatusCode());
      }
    } catch (Exception e) {
      logger.error("Error adding review for recipe ID {}: {}", recipeId, e.getMessage());
    }
  }

  @Override
  public void updateReview(Long recipeId, Long creatorId, RecipeReview review) {
    String url = getUrl("/recipe/" + recipeId + "/reviews/" + creatorId);
    try {
      ResponseEntity<String> response = webServiceCaller.putCall(url, review, String.class);
      if (response.getStatusCode().is2xxSuccessful()) {
        logger.info(
            "Review updated successfully for recipe ID: {} by creator ID: {}", recipeId, creatorId);
      } else {
        logger.error("Failed to update review. Status code: {}", response.getStatusCode());
      }
    } catch (Exception e) {
      logger.error(
          "Error updating review for recipe ID {} by creator ID {}: {}",
          recipeId,
          creatorId,
          e.getMessage());
    }
  }

  @Override
  public void deleteReview(Long recipeId, Long creatorId) {
    String url = getUrl("/recipe/" + recipeId + "/reviews/" + creatorId);
    try {
      ResponseEntity<String> response = webServiceCaller.deleteCall(url, String.class);
      if (response.getStatusCode().is2xxSuccessful()) {
        logger.info(
            "Review deleted successfully for recipe ID: {} by creator ID: {}", recipeId, creatorId);
      } else {
        logger.error("Failed to delete review. Status code: {}", response.getStatusCode());
      }
    } catch (Exception e) {
      logger.error(
          "Error deleting review for recipe ID {} by creator ID {}: {}",
          recipeId,
          creatorId,
          e.getMessage());
    }
  }

  @Override
  public void deleteReviewsByRecipeId(Long recipeId) {
    String url = getUrl("/recipe/" + recipeId + "/reviews");
    try {
      ResponseEntity<String> response = webServiceCaller.deleteCall(url, String.class);
      if (response.getStatusCode().is2xxSuccessful()) {
        logger.info("All reviews deleted successfully for recipe ID: {}", recipeId);
      } else {
        logger.error(
            "Failed to delete reviews by recipeId. Status code: {}", response.getStatusCode());
      }
    } catch (Exception e) {
      logger.error("Error deleting reviews for recipe ID {}: {}", recipeId, e.getMessage());
    }
  }

  @Override
  public void deleteReviewsByCreatorId(Long recipeId, Long creatorId) {
    String url = getUrl("/recipe/" + recipeId + "/reviews/creator/" + creatorId);
    try {
      ResponseEntity<String> response = webServiceCaller.deleteCall(url, String.class);
      if (response.getStatusCode().is2xxSuccessful()) {
        logger.info(
            "All reviews deleted successfully for recipe ID: {} by creator ID: {}",
            recipeId,
            creatorId);
      } else {
        logger.error(
            "Failed to delete reviews by creatorId. Status code: {}", response.getStatusCode());
      }
    } catch (Exception e) {
      logger.error(
          "Error deleting reviews for recipe ID {} by creator ID {}: {}",
          recipeId,
          creatorId,
          e.getMessage());
    }
  }

  @Override
  public RecipeReview getReviewByRecipeAndCreator(Long recipeId, Long creatorId) {
    String url = getUrl("/recipe/" + recipeId + "/reviews/" + creatorId);
    try {
      ResponseEntity<RecipeReview> response = webServiceCaller.getCall(url, RecipeReview.class);
      if (response.getStatusCode().is2xxSuccessful()) {
        logger.info("Retrieved review for recipe ID: {} by creator ID: {}", recipeId, creatorId);
        return response.getBody();
      } else {
        logger.error("Failed to retrieve review. Status code: {}", response.getStatusCode());
        return null;
      }
    } catch (Exception e) {
      logger.error(
          "Error retrieving review for recipe ID {} by creator ID {}: {}",
          recipeId,
          creatorId,
          e.getMessage());
      return null;
    }
  }

  @Override
  public List<RecipeReview> getReviewsByRecipeId(Long recipeId) {
    String url = getUrl("/recipe/" + recipeId + "/reviews");
    try {
      ParameterizedTypeReference<List<RecipeReview>> typeRef =
          new ParameterizedTypeReference<>() {};
      ResponseEntity<List<RecipeReview>> response = webServiceCaller.getCall(url, typeRef);
      if (response.getStatusCode().is2xxSuccessful()) {
        logger.info("Retrieved reviews for recipe ID: {}", recipeId);
        return response.getBody();
      } else {
        logger.error(
            "Failed to retrieve reviews by recipeId. Status code: {}", response.getStatusCode());
        return Collections.emptyList();
      }
    } catch (Exception e) {
      logger.error("Error retrieving reviews for recipe ID {}: {}", recipeId, e.getMessage());
      return Collections.emptyList();
    }
  }

  @Override
  public List<RecipeReview> getReviewsByCreatorId(Long recipeId, Long creatorId) {
    String url = getUrl("/recipe/" + recipeId + "/reviews/creator/" + creatorId);
    try {
      ParameterizedTypeReference<List<RecipeReview>> typeRef =
          new ParameterizedTypeReference<>() {};
      ResponseEntity<List<RecipeReview>> response = webServiceCaller.getCall(url, typeRef);
      if (response.getStatusCode().is2xxSuccessful()) {
        logger.info("Retrieved reviews for recipe ID: {} by creator ID: {}", recipeId, creatorId);
        return response.getBody();
      } else {
        logger.error(
            "Failed to retrieve reviews by creatorId. Status code: {}", response.getStatusCode());
        return Collections.emptyList();
      }
    } catch (Exception e) {
      logger.error(
          "Error retrieving reviews for recipe ID {} by creator ID {}: {}",
          recipeId,
          creatorId,
          e.getMessage());
      return Collections.emptyList();
    }
  }
}
