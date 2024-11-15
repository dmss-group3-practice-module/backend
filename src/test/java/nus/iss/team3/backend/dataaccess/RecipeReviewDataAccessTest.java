package nus.iss.team3.backend.dataaccess;

import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.COLUMN_REVIEW_COMMENTS;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.COLUMN_REVIEW_CREATE_TIME;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.COLUMN_REVIEW_CREATOR_ID;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.COLUMN_REVIEW_ID;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.COLUMN_REVIEW_RATING;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.COLUMN_REVIEW_RECIPE_ID;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.COLUMN_REVIEW_UPDATE_TIME;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.SQL_REVIEW_ADD;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.SQL_REVIEW_DELETE;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.SQL_REVIEW_DELETE_BY_CREATOR_ID;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.SQL_REVIEW_DELETE_BY_RECIPE_ID;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.SQL_REVIEW_GET_BY_CREATOR_ID;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.SQL_REVIEW_GET_BY_RECIPE_AND_CREATOR;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.SQL_REVIEW_GET_BY_RECIPE_ID;
import static nus.iss.team3.backend.dataaccess.PostgresSqlStatementRecipe.SQL_REVIEW_UPDATE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.*;
import nus.iss.team3.backend.dataaccess.postgres.PostgresDataAccess;
import nus.iss.team3.backend.entity.RecipeReview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit test class: RecipeReviewDataAccessTest is used to test various endpoints of the
 * RecipeReviewDataAccess class to ensure its behavior meets expectations.
 *
 * @author Mao Weining
 */
public class RecipeReviewDataAccessTest {

  @Mock private PostgresDataAccess postgresDataAccess;

  @InjectMocks private ReviewDataAccess recipeReviewDataAccess;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testAddReview_Success() {
    RecipeReview review = new RecipeReview();
    review.setRecipeId(1L);
    review.setCreatorId(1L);
    review.setRating(5.0);
    review.setComments("Great recipe!");

    when(postgresDataAccess.upsertStatement(eq(SQL_REVIEW_ADD), any())).thenReturn(1);

    recipeReviewDataAccess.addReview(review);

    verify(postgresDataAccess, times(1)).upsertStatement(eq(SQL_REVIEW_ADD), any());
  }

  @Test
  public void testAddReview_Failure() {
    RecipeReview review = new RecipeReview();
    review.setRecipeId(1L);
    review.setCreatorId(1L);
    review.setRating(5.0);
    review.setComments("Great recipe!");

    when(postgresDataAccess.upsertStatement(eq(SQL_REVIEW_ADD), any())).thenReturn(0);

    recipeReviewDataAccess.addReview(review);

    verify(postgresDataAccess, times(1)).upsertStatement(eq(SQL_REVIEW_ADD), any());
  }

  @Test
  public void testAddReview_Validation() {
    RecipeReview review = new RecipeReview();

    when(postgresDataAccess.upsertStatement(eq(SQL_REVIEW_ADD), any())).thenReturn(1);

    RuntimeException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              recipeReviewDataAccess.addReview(review);
            });
    assertEquals(exception.getMessage(), "Required fields for review cannot be null");

    review.setRecipeId(1L);
    exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              recipeReviewDataAccess.addReview(review);
            });
    assertEquals(exception.getMessage(), "Required fields for review cannot be null");

    review.setCreatorId(1L);
    exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              recipeReviewDataAccess.addReview(review);
            });
    assertEquals(exception.getMessage(), "Required fields for review cannot be null");

    review.setRating(3.4);
    recipeReviewDataAccess.addReview(review);
    verify(postgresDataAccess, times(1)).upsertStatement(eq(SQL_REVIEW_ADD), any());
  }

  @Test
  public void testUpdateReview_Success() {
    RecipeReview review = new RecipeReview();
    review.setRecipeId(1L);
    review.setCreatorId(1L);
    review.setRating(4.0);
    review.setComments("Updated comment");

    when(postgresDataAccess.upsertStatement(eq(SQL_REVIEW_UPDATE), any())).thenReturn(1);

    recipeReviewDataAccess.updateReview(1L, 1L, review);

    verify(postgresDataAccess, times(1)).upsertStatement(eq(SQL_REVIEW_UPDATE), any());
  }

  @Test
  public void testUpdateReview_Failure() {
    RecipeReview review = new RecipeReview();
    review.setRecipeId(1L);
    review.setCreatorId(1L);
    review.setRating(4.0);
    review.setComments("Updated comment");

    when(postgresDataAccess.upsertStatement(eq(SQL_REVIEW_UPDATE), any())).thenReturn(0);

    recipeReviewDataAccess.updateReview(1L, 1L, review);

    verify(postgresDataAccess, times(1)).upsertStatement(eq(SQL_REVIEW_UPDATE), any());
  }

  @Test
  public void testDeleteReview_Success() {
    when(postgresDataAccess.upsertStatement(eq(SQL_REVIEW_DELETE), any())).thenReturn(1);

    recipeReviewDataAccess.deleteReview(1L, 1L);

    verify(postgresDataAccess, times(1)).upsertStatement(eq(SQL_REVIEW_DELETE), any());
  }

  @Test
  public void testDeleteReview_Failure() {
    when(postgresDataAccess.upsertStatement(eq(SQL_REVIEW_DELETE), any())).thenReturn(0);

    recipeReviewDataAccess.deleteReview(1L, 1L);

    verify(postgresDataAccess, times(1)).upsertStatement(eq(SQL_REVIEW_DELETE), any());
  }

  @Test
  public void testDeleteReviewsByRecipeId_Success() {
    when(postgresDataAccess.upsertStatement(eq(SQL_REVIEW_DELETE_BY_RECIPE_ID), any()))
        .thenReturn(1);

    recipeReviewDataAccess.deleteReviewsByRecipeId(1L);

    verify(postgresDataAccess, times(1)).upsertStatement(eq(SQL_REVIEW_DELETE_BY_RECIPE_ID), any());
  }

  @Test
  public void testDeleteReviewsByRecipeId_Failure() {
    when(postgresDataAccess.upsertStatement(eq(SQL_REVIEW_DELETE_BY_RECIPE_ID), any()))
        .thenReturn(0);

    recipeReviewDataAccess.deleteReviewsByRecipeId(1L);

    verify(postgresDataAccess, times(1)).upsertStatement(eq(SQL_REVIEW_DELETE_BY_RECIPE_ID), any());
  }

  @Test
  public void testDeleteReviewsByCreatorId_Success() {
    when(postgresDataAccess.upsertStatement(eq(SQL_REVIEW_DELETE_BY_CREATOR_ID), any()))
        .thenReturn(1);

    recipeReviewDataAccess.deleteReviewsByCreatorId(1L);

    verify(postgresDataAccess, times(1))
        .upsertStatement(eq(SQL_REVIEW_DELETE_BY_CREATOR_ID), any());
  }

  @Test
  public void testDeleteReviewsByCreatorId_Failure() {
    when(postgresDataAccess.upsertStatement(eq(SQL_REVIEW_DELETE_BY_CREATOR_ID), any()))
        .thenReturn(0);

    recipeReviewDataAccess.deleteReviewsByCreatorId(1L);

    verify(postgresDataAccess, times(1))
        .upsertStatement(eq(SQL_REVIEW_DELETE_BY_CREATOR_ID), any());
  }

  @Test
  public void testGetReviewByRecipeAndCreator_Found() {
    Map<String, Object> row = new HashMap<>();
    row.put(COLUMN_REVIEW_ID, 1L);
    row.put(COLUMN_REVIEW_RECIPE_ID, 1L);
    row.put(COLUMN_REVIEW_CREATOR_ID, 1L);
    row.put(COLUMN_REVIEW_RATING, 5.0);
    row.put(COLUMN_REVIEW_COMMENTS, "Great recipe!");
    row.put(COLUMN_REVIEW_CREATE_TIME, new Timestamp(System.currentTimeMillis()));
    row.put(COLUMN_REVIEW_UPDATE_TIME, new Timestamp(System.currentTimeMillis()));

    List<Map<String, Object>> results = Collections.singletonList(row);
    when(postgresDataAccess.queryStatement(eq(SQL_REVIEW_GET_BY_RECIPE_AND_CREATOR), any()))
        .thenReturn(results);

    RecipeReview review = recipeReviewDataAccess.getReviewByRecipeAndCreator(1L, 1L);

    assertNotNull(review);
    assertEquals(1L, review.getId());
    assertEquals(1L, review.getRecipeId());
    assertEquals(1L, review.getCreatorId());
    assertEquals(5.0, review.getRating());
    assertEquals("Great recipe!", review.getComments());
  }

  @Test
  public void testGetReviewByRecipeAndCreator_Found_nullValues() {
    Map<String, Object> row = new HashMap<>();
    //    row.put(COLUMN_REVIEW_ID, 1L);
    //    row.put(COLUMN_REVIEW_RECIPE_ID, 1L);
    //    row.put(COLUMN_REVIEW_CREATOR_ID, 1L);
    //    row.put(COLUMN_REVIEW_RATING, 5.0);
    //    row.put(COLUMN_REVIEW_COMMENTS, "Great recipe!");
    //    row.put(COLUMN_REVIEW_CREATE_TIME, new Timestamp(System.currentTimeMillis()));
    //    row.put(COLUMN_REVIEW_UPDATE_TIME, new Timestamp(System.currentTimeMillis()));

    List<Map<String, Object>> results = Collections.singletonList(row);
    when(postgresDataAccess.queryStatement(eq(SQL_REVIEW_GET_BY_RECIPE_AND_CREATOR), any()))
        .thenReturn(results);

    RecipeReview review = recipeReviewDataAccess.getReviewByRecipeAndCreator(1L, 1L);

    assertNotNull(review);
    assertNull(review.getId());
    assertNull(review.getRecipeId());
    assertNull(review.getCreatorId());
    assertNull(review.getRating());
    assertNull(review.getComments());
  }

  @Test
  public void testGetReviewByRecipeAndCreator_NotFound() {

    List<Map<String, Object>> results = null;
    when(postgresDataAccess.queryStatement(eq(SQL_REVIEW_GET_BY_RECIPE_AND_CREATOR), any()))
        .thenReturn(results);

    RecipeReview review = recipeReviewDataAccess.getReviewByRecipeAndCreator(1L, 1L);

    assertNull(review);
  }

  @Test
  public void testGetReviewByRecipeAndCreator_NotFound2() {

    List<Map<String, Object>> results = new ArrayList<>();
    when(postgresDataAccess.queryStatement(eq(SQL_REVIEW_GET_BY_RECIPE_AND_CREATOR), any()))
        .thenReturn(results);

    RecipeReview review = recipeReviewDataAccess.getReviewByRecipeAndCreator(1L, 1L);

    assertNull(review);
  }

  @Test
  public void testGetReviewsByRecipeId() {
    Map<String, Object> row = new HashMap<>();
    row.put(COLUMN_REVIEW_ID, 1L);
    row.put(COLUMN_REVIEW_RECIPE_ID, 1L);
    row.put(COLUMN_REVIEW_CREATOR_ID, 1L);
    row.put(COLUMN_REVIEW_RATING, 5.0);
    row.put(COLUMN_REVIEW_COMMENTS, "Great recipe!");
    row.put(COLUMN_REVIEW_CREATE_TIME, new Timestamp(System.currentTimeMillis()));
    row.put(COLUMN_REVIEW_UPDATE_TIME, new Timestamp(System.currentTimeMillis()));

    List<Map<String, Object>> results = Collections.singletonList(row);
    when(postgresDataAccess.queryStatement(eq(SQL_REVIEW_GET_BY_RECIPE_ID), any()))
        .thenReturn(results);

    List<RecipeReview> reviews = recipeReviewDataAccess.getReviewsByRecipeId(1L);

    assertNotNull(reviews);
    assertEquals(1, reviews.size());
    assertEquals(1L, reviews.getFirst().getRecipeId());
  }

  @Test
  public void testGetReviewsByRecipeId_Failure() {

    List<Map<String, Object>> results = null;
    when(postgresDataAccess.queryStatement(eq(SQL_REVIEW_GET_BY_RECIPE_ID), any()))
        .thenReturn(results);

    List<RecipeReview> reviews = recipeReviewDataAccess.getReviewsByRecipeId(1L);

    assertNull(reviews);
  }

  @Test
  public void testGetReviewsByCreatorId() {
    Map<String, Object> row = new HashMap<>();
    row.put(COLUMN_REVIEW_ID, 1L);
    row.put(COLUMN_REVIEW_RECIPE_ID, 1L);
    row.put(COLUMN_REVIEW_CREATOR_ID, 1L);
    row.put(COLUMN_REVIEW_RATING, 5.0);
    row.put(COLUMN_REVIEW_COMMENTS, "Great recipe!");
    row.put(COLUMN_REVIEW_CREATE_TIME, new Timestamp(System.currentTimeMillis()));
    row.put(COLUMN_REVIEW_UPDATE_TIME, new Timestamp(System.currentTimeMillis()));

    List<Map<String, Object>> results = Collections.singletonList(row);
    when(postgresDataAccess.queryStatement(eq(SQL_REVIEW_GET_BY_CREATOR_ID), any()))
        .thenReturn(results);

    List<RecipeReview> reviews = recipeReviewDataAccess.getReviewsByCreatorId(1L);

    assertNotNull(reviews);
    assertEquals(1, reviews.size());
    assertEquals(1L, reviews.getFirst().getCreatorId());
  }

  @Test
  public void testGetReviewsByCreatorId_Failure() {

    List<Map<String, Object>> results = null;
    when(postgresDataAccess.queryStatement(eq(SQL_REVIEW_GET_BY_CREATOR_ID), any()))
        .thenReturn(results);

    List<RecipeReview> reviews = recipeReviewDataAccess.getReviewsByCreatorId(1L);

    assertNull(reviews);
  }
}
