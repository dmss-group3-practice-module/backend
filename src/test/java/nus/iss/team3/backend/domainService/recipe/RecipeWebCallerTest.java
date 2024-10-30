package nus.iss.team3.backend.domainService.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import nus.iss.team3.backend.domainService.webservice.IWebserviceCaller;
import nus.iss.team3.backend.entity.Recipe;
import nus.iss.team3.backend.entity.RecipeReview;
import nus.iss.team3.backend.entity.RecipeWithReviews;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit test class: RecipeWebCallerTest is used to test the methods of the RecipeWebCaller class to
 * ensure its behavior meets expectations.
 *
 * @author Mao Weining
 */
@ExtendWith(MockitoExtension.class)
public class RecipeWebCallerTest {

  @InjectMocks private RecipeWebCaller recipeWebCaller;

  @Mock private IWebserviceCaller webServiceCaller;

  @Value("${service.url.recipe.address}")
  private String serviceUrl = "localhost";

  @Value("${service.url.recipe.port}")
  private String servicePort = "8080";

  // Helper
  private static List<RecipeWithReviews> getRecipeWithReviews() {
    Recipe recipe1 = new Recipe();
    recipe1.setId(1L);
    Recipe recipe2 = new Recipe();
    recipe2.setId(2L);

    List<RecipeReview> reviews1 = Arrays.asList(new RecipeReview(), new RecipeReview());
    List<RecipeReview> reviews2 = List.of(new RecipeReview());

    RecipeWithReviews recipeWithReviews1 = new RecipeWithReviews(recipe1, reviews1);
    RecipeWithReviews recipeWithReviews2 = new RecipeWithReviews(recipe2, reviews2);

    return Arrays.asList(recipeWithReviews1, recipeWithReviews2);
  }

  @BeforeEach
  public void setUp() {
    recipeWebCaller = new RecipeWebCaller(serviceUrl, servicePort, webServiceCaller);
  }

  private String getUrl(String path) {
    return "http://" + serviceUrl + ":" + servicePort + path;
  }

  /** Test addRecipe when the operation is successful. */
  @Test
  public void testAddRecipe_Success() {
    Recipe recipe = new Recipe();
    String url = getUrl("/recipe");

    when(webServiceCaller.postCall(eq(url), eq(recipe), eq(Recipe.class)))
        .thenReturn(new ResponseEntity<>(HttpStatus.OK));

    boolean result = recipeWebCaller.addRecipe(recipe);

    verify(webServiceCaller, times(1)).postCall(eq(url), eq(recipe), eq(Recipe.class));

    assertTrue(result);
  }

  /** Test addRecipe when the operation fails. */
  @Test
  public void testAddRecipe_Failure() {
    Recipe recipe = new Recipe();
    String url = getUrl("/recipe");

    when(webServiceCaller.postCall(eq(url), eq(recipe), eq(Recipe.class)))
        .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

    boolean result = recipeWebCaller.addRecipe(recipe);

    verify(webServiceCaller, times(1)).postCall(eq(url), eq(recipe), eq(Recipe.class));

    assertFalse(result);
  }

  /** Test updateRecipe when the operation is successful. */
  @Test
  public void testUpdateRecipe_Success() {
    Recipe recipe = new Recipe();
    recipe.setId(1L);
    String url = getUrl("/recipe/" + recipe.getId());

    when(webServiceCaller.putCall(eq(url), eq(recipe), eq(String.class)))
        .thenReturn(new ResponseEntity<>(HttpStatus.OK));

    boolean result = recipeWebCaller.updateRecipe(recipe);

    verify(webServiceCaller, times(1)).putCall(eq(url), eq(recipe), eq(String.class));

    assertTrue(result);
  }

  /** Test updateRecipe when the recipe ID is null. */
  @Test
  public void testUpdateRecipe_NullId() {
    Recipe recipe = new Recipe();

    boolean result = recipeWebCaller.updateRecipe(recipe);

    assertFalse(result);
  }

  /** Test deleteRecipeById when the operation is successful. */
  @Test
  public void testDeleteRecipeById_Success() {
    Long recipeId = 1L;
    String url = getUrl("/recipe/" + recipeId);

    when(webServiceCaller.deleteCall(eq(url), eq(String.class)))
        .thenReturn(new ResponseEntity<>(HttpStatus.OK));

    boolean result = recipeWebCaller.deleteRecipeById(recipeId);

    verify(webServiceCaller, times(1)).deleteCall(eq(url), eq(String.class));

    assertTrue(result);
  }

  /** Test deleteRecipeById when the operation fails. */
  @Test
  public void testDeleteRecipeById_Failure() {
    Long recipeId = 1L;
    String url = getUrl("/recipe/" + recipeId);

    when(webServiceCaller.deleteCall(eq(url), eq(String.class)))
        .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

    boolean result = recipeWebCaller.deleteRecipeById(recipeId);

    verify(webServiceCaller, times(1)).deleteCall(eq(url), eq(String.class));

    assertFalse(result);
  }

  /** Test getRecipeById when the operation is successful. */
  @Test
  public void testGetRecipeById_Success() {
    Long recipeId = 1L;
    Recipe expectedRecipe = new Recipe();
    expectedRecipe.setId(recipeId);
    String url = getUrl("/recipe/" + recipeId);

    ResponseEntity<Recipe> responseEntity = new ResponseEntity<>(expectedRecipe, HttpStatus.OK);

    when(webServiceCaller.getCall(eq(url), eq(Recipe.class))).thenReturn(responseEntity);

    Recipe actualRecipe = recipeWebCaller.getRecipeById(recipeId);

    verify(webServiceCaller, times(1)).getCall(eq(url), eq(Recipe.class));

    assertEquals(expectedRecipe, actualRecipe);
  }

  /** Test getRecipeById when the operation fails. */
  @Test
  public void testGetRecipeById_Failure() {
    Long recipeId = 1L;
    String url = getUrl("/recipe/" + recipeId);

    ResponseEntity<Recipe> responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);

    when(webServiceCaller.getCall(eq(url), eq(Recipe.class))).thenReturn(responseEntity);

    Recipe actualRecipe = recipeWebCaller.getRecipeById(recipeId);

    verify(webServiceCaller, times(1)).getCall(eq(url), eq(Recipe.class));

    assertNull(actualRecipe);
  }

  /** Test getAllRecipes when the operation is successful. */
  @Test
  @SuppressWarnings("unchecked")
  public void testGetAllRecipes_Success() {
    List<Recipe> expectedRecipes = Arrays.asList(new Recipe(), new Recipe());
    String url = getUrl("/recipe");

    ResponseEntity<List<Recipe>> responseEntity =
        new ResponseEntity<>(expectedRecipes, HttpStatus.OK);

    when(webServiceCaller.getCall(eq(url), any(ParameterizedTypeReference.class)))
        .thenReturn(responseEntity);

    List<Recipe> actualRecipes = recipeWebCaller.getAllRecipes();

    verify(webServiceCaller, times(1)).getCall(eq(url), any(ParameterizedTypeReference.class));

    assertEquals(expectedRecipes, actualRecipes);
  }

  /** Test getAllRecipes when the operation fails. */
  @Test
  @SuppressWarnings("unchecked")
  public void testGetAllRecipes_Failure() {
    String url = getUrl("/recipe");

    ResponseEntity<List<Recipe>> responseEntity =
        new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

    when(webServiceCaller.getCall(eq(url), any(ParameterizedTypeReference.class)))
        .thenReturn(responseEntity);

    List<Recipe> actualRecipes = recipeWebCaller.getAllRecipes();

    verify(webServiceCaller, times(1)).getCall(eq(url), any(ParameterizedTypeReference.class));

    assertEquals(Collections.emptyList(), actualRecipes);
  }

  /** Test getAllPublishedRecipes when the operation is successful. */
  @Test
  @SuppressWarnings("unchecked")
  public void testGetAllPublishedRecipes_Success() {
    List<Recipe> expectedRecipes = Arrays.asList(new Recipe(), new Recipe());
    String url = getUrl("/recipe/published");

    ResponseEntity<List<Recipe>> responseEntity =
        new ResponseEntity<>(expectedRecipes, HttpStatus.OK);

    when(webServiceCaller.getCall(eq(url), any(ParameterizedTypeReference.class)))
        .thenReturn(responseEntity);

    List<Recipe> actualRecipes = recipeWebCaller.getAllPublishedRecipes();

    verify(webServiceCaller, times(1)).getCall(eq(url), any(ParameterizedTypeReference.class));

    assertEquals(expectedRecipes, actualRecipes);
  }

  /** Test getRecipesByName when the operation is successful. */
  @Test
  @SuppressWarnings("unchecked")
  public void testGetRecipesByName_Success() {
    String name = "Test Recipe";
    List<Recipe> expectedRecipes = Arrays.asList(new Recipe(), new Recipe());
    String url = getUrl("/recipe/search?name=" + name);

    ResponseEntity<List<Recipe>> responseEntity =
        new ResponseEntity<>(expectedRecipes, HttpStatus.OK);

    when(webServiceCaller.getCall(eq(url), any(ParameterizedTypeReference.class)))
        .thenReturn(responseEntity);

    List<Recipe> actualRecipes = recipeWebCaller.getRecipesByName(name);

    verify(webServiceCaller, times(1)).getCall(eq(url), any(ParameterizedTypeReference.class));

    assertEquals(expectedRecipes, actualRecipes);
  }

  /** Test getRecipesByCreatorId when the operation is successful. */
  @Test
  @SuppressWarnings("unchecked")
  public void testGetRecipesByCreatorId_Success() {
    int creatorId = 123;
    List<Recipe> expectedRecipes = Arrays.asList(new Recipe(), new Recipe());
    String url = getUrl("/recipe/creator/" + creatorId);

    ResponseEntity<List<Recipe>> responseEntity =
        new ResponseEntity<>(expectedRecipes, HttpStatus.OK);

    when(webServiceCaller.getCall(eq(url), any(ParameterizedTypeReference.class)))
        .thenReturn(responseEntity);

    List<Recipe> actualRecipes = recipeWebCaller.getRecipesByCreatorId(creatorId);

    verify(webServiceCaller, times(1)).getCall(eq(url), any(ParameterizedTypeReference.class));

    assertEquals(expectedRecipes, actualRecipes);
  }
}
