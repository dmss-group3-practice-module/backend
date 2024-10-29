package nus.iss.team3.backend;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import nus.iss.team3.backend.domainService.webservice.IWebserviceCaller;
import nus.iss.team3.backend.entity.RecipeReview;
import nus.iss.team3.backend.service.review.RecipeReviewWebCaller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit test class: RecipeReviewWebCallerTest is used to test the methods of the
 * RecipeReviewWebCaller class to ensure its behavior meets expectations.
 *
 * @author Mao Weining
 */
public class RecipeReviewWebCallerTest {

  @InjectMocks private RecipeReviewWebCaller recipeReviewWebCaller;

  @Mock private IWebserviceCaller webServiceCaller;

  @Value("${service.url.recipe.address}")
  private String serviceUrl = "localhost";

  @Value("${service.url.recipe.port}")
  private String servicePort = "8080";

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    recipeReviewWebCaller = new RecipeReviewWebCaller(serviceUrl, servicePort, webServiceCaller);
  }

  /** Test addReview when the operation is successful. */
  @Test
  public void testAddReview_Success() {
    // Prepare test data
    RecipeReview review = new RecipeReview();
    review.setRecipeId(1L);
    String url = "http://localhost:8080/recipe/1/reviews";

    // Mock webServiceCaller behavior
    when(webServiceCaller.postCall(eq(url), eq(review), eq(String.class)))
        .thenReturn(new ResponseEntity<>(HttpStatus.OK));

    // Call the method under test
    recipeReviewWebCaller.addReview(review);

    // Verify method calls
    verify(webServiceCaller, times(1)).postCall(eq(url), eq(review), eq(String.class));
  }

  /** Test addReview when the operation fails with a non-2xx status code. */
  @Test
  public void testAddReview_Failure() {
    // Prepare test data
    RecipeReview review = new RecipeReview();
    review.setRecipeId(1L);
    String url = "http://localhost:8080/recipe/1/reviews";

    // Mock webServiceCaller behavior to return a failure status
    when(webServiceCaller.postCall(eq(url), eq(review), eq(String.class)))
        .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

    // Call the method under test
    recipeReviewWebCaller.addReview(review);

    // Verify method calls
    verify(webServiceCaller, times(1)).postCall(eq(url), eq(review), eq(String.class));
  }

  /** Test addReview when an exception is thrown. */
  @Test
  public void testAddReview_Exception() {
    // Prepare test data
    RecipeReview review = new RecipeReview();
    review.setRecipeId(1L);
    String url = "http://localhost:8080/recipe/1/reviews";

    // Mock webServiceCaller to throw an exception
    when(webServiceCaller.postCall(eq(url), eq(review), eq(String.class)))
        .thenThrow(new RuntimeException("Connection error"));

    // Call the method under test
    recipeReviewWebCaller.addReview(review);

    // Verify method calls
    verify(webServiceCaller, times(1)).postCall(eq(url), eq(review), eq(String.class));
  }

  /** Test updateReview when the operation is successful. */
  @Test
  public void testUpdateReview_Success() {
    // Prepare test data
    Long recipeId = 1L;
    Long creatorId = 2L;
    RecipeReview review = new RecipeReview();
    String url = "http://localhost:8080/recipe/1/reviews/2";

    // Mock webServiceCaller behavior
    when(webServiceCaller.putCall(eq(url), eq(review), eq(String.class)))
        .thenReturn(new ResponseEntity<>(HttpStatus.OK));

    // Call the method under test
    recipeReviewWebCaller.updateReview(recipeId, creatorId, review);

    // Verify method calls
    verify(webServiceCaller, times(1)).putCall(eq(url), eq(review), eq(String.class));
  }

  /** Test deleteReview when the operation is successful. */
  @Test
  public void testDeleteReview_Success() {
    // Prepare test data
    Long recipeId = 1L;
    Long creatorId = 2L;
    String url = "http://localhost:8080/recipe/1/reviews/2";

    // Mock webServiceCaller behavior
    when(webServiceCaller.deleteCall(eq(url), eq(String.class)))
        .thenReturn(new ResponseEntity<>(HttpStatus.OK));

    // Call the method under test
    recipeReviewWebCaller.deleteReview(recipeId, creatorId);

    // Verify method calls
    verify(webServiceCaller, times(1)).deleteCall(eq(url), eq(String.class));
  }

  /** Test deleteReviewsByRecipeId when the operation is successful. */
  @Test
  public void testDeleteReviewsByRecipeId_Success() {
    // Prepare test data
    Long recipeId = 1L;
    String url = "http://localhost:8080/recipe/1/reviews";

    // Mock webServiceCaller behavior
    when(webServiceCaller.deleteCall(eq(url), eq(String.class)))
        .thenReturn(new ResponseEntity<>(HttpStatus.OK));

    // Call the method under test
    recipeReviewWebCaller.deleteReviewsByRecipeId(recipeId);

    // Verify method calls
    verify(webServiceCaller, times(1)).deleteCall(eq(url), eq(String.class));
  }

  /** Test getReviewByRecipeAndCreator when the operation is successful. */
  @Test
  public void testGetReviewByRecipeAndCreator_Success() {
    // Prepare test data
    Long recipeId = 1L;
    Long creatorId = 2L;
    RecipeReview expectedReview = new RecipeReview();
    expectedReview.setRecipeId(recipeId);
    expectedReview.setCreatorId(creatorId);
    String url = "http://localhost:8080/recipe/1/reviews/2";
    ResponseEntity<RecipeReview> responseEntity =
        new ResponseEntity<>(expectedReview, HttpStatus.OK);

    // Mock webServiceCaller behavior
    when(webServiceCaller.getCall(eq(url), eq(RecipeReview.class))).thenReturn(responseEntity);

    // Call the method under test
    RecipeReview actualReview =
        recipeReviewWebCaller.getReviewByRecipeAndCreator(recipeId, creatorId);

    // Verify method calls
    verify(webServiceCaller, times(1)).getCall(eq(url), eq(RecipeReview.class));

    // Assert the result
    assertEquals(expectedReview, actualReview);
  }

  /** Test getReviewsByRecipeId when the operation is successful. */
  @Test
  public void testGetReviewsByRecipeId_Success() {
    // Prepare test data
    Long recipeId = 1L;
    List<RecipeReview> expectedReviews = Arrays.asList(new RecipeReview(), new RecipeReview());
    String url = "http://localhost:8080/recipe/1/reviews";
    ResponseEntity<List<RecipeReview>> responseEntity =
        new ResponseEntity<>(expectedReviews, HttpStatus.OK);

    // Mock webServiceCaller behavior
    when(webServiceCaller.getCall(
            eq(url), ArgumentMatchers.<ParameterizedTypeReference<List<RecipeReview>>>any()))
        .thenReturn(responseEntity);

    // Call the method under test
    List<RecipeReview> actualReviews = recipeReviewWebCaller.getReviewsByRecipeId(recipeId);

    // Verify method calls
    verify(webServiceCaller, times(1))
        .getCall(eq(url), ArgumentMatchers.<ParameterizedTypeReference<List<RecipeReview>>>any());

    // Assert the result
    assertEquals(expectedReviews, actualReviews);
  }

  /** Test getReviewsByCreatorId when the operation is successful. */
  @Test
  public void testGetReviewsByCreatorId_Success() {
    // Prepare test data
    Long recipeId = 1L;
    Long creatorId = 2L;
    List<RecipeReview> expectedReviews = Arrays.asList(new RecipeReview(), new RecipeReview());
    String url = "http://localhost:8080/recipe/1/reviews/creator/2";
    ResponseEntity<List<RecipeReview>> responseEntity =
        new ResponseEntity<>(expectedReviews, HttpStatus.OK);

    // Mock webServiceCaller behavior
    when(webServiceCaller.getCall(
            eq(url), ArgumentMatchers.<ParameterizedTypeReference<List<RecipeReview>>>any()))
        .thenReturn(responseEntity);

    // Call the method under test
    List<RecipeReview> actualReviews =
        recipeReviewWebCaller.getReviewsByCreatorId(recipeId, creatorId);

    // Verify method calls
    verify(webServiceCaller, times(1))
        .getCall(eq(url), ArgumentMatchers.<ParameterizedTypeReference<List<RecipeReview>>>any());

    // Assert the result
    assertEquals(expectedReviews, actualReviews);
  }

  /** Test getReviewByRecipeAndCreator when the operation fails. */
  @Test
  public void testGetReviewByRecipeAndCreator_Failure() {
    // Prepare test data
    Long recipeId = 1L;
    Long creatorId = 2L;
    String url = "http://localhost:8080/recipe/1/reviews/2";
    ResponseEntity<RecipeReview> responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);

    // Mock webServiceCaller behavior
    when(webServiceCaller.getCall(eq(url), eq(RecipeReview.class))).thenReturn(responseEntity);

    // Call the method under test
    RecipeReview actualReview =
        recipeReviewWebCaller.getReviewByRecipeAndCreator(recipeId, creatorId);

    // Verify method calls
    verify(webServiceCaller, times(1)).getCall(eq(url), eq(RecipeReview.class));

    // Assert the result
    assertNull(actualReview);
  }

  /** Test getReviewsByRecipeId when an exception is thrown. */
  @Test
  public void testGetReviewsByRecipeId_Exception() {
    // Prepare test data
    Long recipeId = 1L;
    String url = "http://localhost:8080/recipe/1/reviews";

    // Mock webServiceCaller to throw an exception
    when(webServiceCaller.getCall(
            eq(url), ArgumentMatchers.<ParameterizedTypeReference<List<RecipeReview>>>any()))
        .thenThrow(new RuntimeException("Connection timeout"));

    // Call the method under test
    List<RecipeReview> actualReviews = recipeReviewWebCaller.getReviewsByRecipeId(recipeId);

    // Verify method calls
    verify(webServiceCaller, times(1))
        .getCall(eq(url), ArgumentMatchers.<ParameterizedTypeReference<List<RecipeReview>>>any());

    // Assert the result
    assertEquals(Collections.emptyList(), actualReviews);
  }

  /** Test deleteReview when an exception is thrown. */
  @Test
  public void testDeleteReview_Exception() {
    // Prepare test data
    Long recipeId = 1L;
    Long creatorId = 2L;
    String url = "http://localhost:8080/recipe/1/reviews/2";

    // Mock webServiceCaller to throw an exception
    when(webServiceCaller.deleteCall(eq(url), eq(String.class)))
        .thenThrow(new RuntimeException("Internal server error"));

    // Call the method under test
    recipeReviewWebCaller.deleteReview(recipeId, creatorId);

    // Verify method calls
    verify(webServiceCaller, times(1)).deleteCall(eq(url), eq(String.class));
  }

  /** Helper method to assert equality. */
  private void assertEquals(Object expected, Object actual) {
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
  }

  /** Helper method to assert null. */
  private void assertNull(Object actual) {
    org.junit.jupiter.api.Assertions.assertNull(actual);
  }
}
