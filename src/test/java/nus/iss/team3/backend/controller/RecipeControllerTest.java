package nus.iss.team3.backend.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import nus.iss.team3.backend.businessService.recipeReview.IRecipeReviewService;
import nus.iss.team3.backend.domainService.recipe.IRecipePreferenceContext;
import nus.iss.team3.backend.domainService.recipe.IRecipeService;
import nus.iss.team3.backend.domainService.user.IUserAccountService;
import nus.iss.team3.backend.entity.ERecipeStatus;
import nus.iss.team3.backend.entity.Recipe;
import nus.iss.team3.backend.entity.RecipeWithReviews;
import nus.iss.team3.backend.service.jwt.JwtRequestFilter;
import nus.iss.team3.backend.service.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Unit test class: RecipeControllerTest is used to test various endpoints of the RecipeController
 * class to ensure its behavior meets expectations.
 */
@WebMvcTest(RecipeController.class)
public class RecipeControllerTest {

  @MockBean private JwtUtil jwtUtil;
  @MockBean private IUserAccountService userAccountService;
  @InjectMocks private JwtRequestFilter jwtRequestFilter;
  @Autowired private WebApplicationContext context;

  @Autowired private MockMvc mockMvc; // Tool for simulating HTTP requests

  @MockBean private IRecipeService recipeService; // Mocking the service layer dependency

  @MockBean
  private IRecipeReviewService recipeReviewService; // Mocking the service layer dependency

  @MockBean
  private IRecipePreferenceContext recipePreferenceContext; // Mocking the service layer dependency

  @Autowired private ObjectMapper objectMapper; // Used to serialize objects to JSON

  private Recipe sampleRecipe; // Sample recipe object for testing

  /** Initializes the sample recipe object before each test method is executed */
  @BeforeEach
  void setUp() {
    sampleRecipe = new Recipe();
    sampleRecipe.setId(1L);
    sampleRecipe.setCreatorId(1L);
    sampleRecipe.setName("Sample Recipe");
    sampleRecipe.setImage("https://example.com/image.png");
    sampleRecipe.setDescription("A delicious sample recipe.");
    sampleRecipe.setCookingTimeInMin(3600);
    sampleRecipe.setDifficultyLevel(2);
    sampleRecipe.setRating(4.5);
    sampleRecipe.setStatus(ERecipeStatus.DRAFT);
    sampleRecipe.setCuisine("Chinese");
    sampleRecipe.setCreateDatetime(new Timestamp(System.currentTimeMillis()));
    sampleRecipe.setUpdateDatetime(new Timestamp(System.currentTimeMillis()));

    mockMvc =
        MockMvcBuilders.webAppContextSetup(context)
            .addFilters((OncePerRequestFilter) jwtRequestFilter)
            .build();
  }

  /**
   * Test successful addition of a new recipe. Verify returns 201 CREATED status and that the
   * service layer's addRecipe method is called
   */
  @Test
  void addRecipe_Success() throws Exception {
    // Arrange: Mock the service layer to successfully add a recipe
    when(recipeService.addRecipe(any(Recipe.class))).thenReturn(true);

    // Act & Assert: Send POST request and verify response status and content
    mockMvc
        .perform(
            post("/recipe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRecipe)))
        .andExpect(status().isCreated())
        .andExpect(content().string("Recipe added successfully"));

    // Verify the service layer method is called once
    verify(recipeService, times(1)).addRecipe(any(Recipe.class));
  }

  /**
   * Test adding a recipe with invalid data, service layer throws IllegalArgumentException. Verify
   * returns 400 BAD REQUEST status and error message
   */
  @Test
  void addRecipe_InvalidData_ReturnsBadRequest() throws Exception {
    // Arrange: Mock the service layer to throw IllegalArgumentException
    when(recipeService.addRecipe(any(Recipe.class)))
        .thenThrow(new IllegalArgumentException("Invalid recipe data"));

    // Act & Assert: Send POST request and verify response status and error message
    mockMvc
        .perform(
            post("/recipe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRecipe)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid recipe data"));

    // Verify the service layer method is called once
    verify(recipeService, times(1)).addRecipe(any(Recipe.class));
  }

  /**
   * Test successful update of an existing recipe. Verify returns 200 OK status and that the service
   * layer's updateRecipe method is called
   */
  @Test
  void updateRecipe_Success() throws Exception {
    // Arrange: Mock the service layer to successfully update a recipe
    when(recipeService.updateRecipe(any(Recipe.class))).thenReturn(true);

    Long recipeId = sampleRecipe.getId();

    // Act & Assert: Send PUT request and verify response status and content
    mockMvc
        .perform(
            put("/recipe/{id}", recipeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRecipe)))
        .andExpect(status().isOk())
        .andExpect(content().string("Recipe updated successfully"));

    // Verify the service layer method is called once
    verify(recipeService, times(1)).updateRecipe(any(Recipe.class));
  }

  @Test
  void updateRecipe_Failure() throws Exception {
    // Arrange: Mock the service layer to successfully update a recipe
    when(recipeService.updateRecipe(any(Recipe.class))).thenReturn(false);

    Long recipeId = sampleRecipe.getId();

    // Act & Assert: Send PUT request and verify response status and content
    mockMvc
        .perform(
            put("/recipe/{id}", recipeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRecipe)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Failed to update recipe"));

    // Verify the service layer method is called once
    verify(recipeService, times(1)).updateRecipe(any(Recipe.class));
  }

  /**
   * Test successful deletion of a recipe. Verify returns 200 OK status and that the service layer's
   * deleteRecipeById method is called
   */
  @Test
  void deleteRecipe_Success() throws Exception {
    // Arrange: Mock the service layer to successfully delete a recipe
    Long recipeId = sampleRecipe.getId();

    // Act & Assert: Send DELETE request and verify response status and content
    mockMvc
        .perform(delete("/recipe/{id}", recipeId))
        .andExpect(status().isOk())
        .andExpect(content().string("Recipe deleted successfully"));

    // Verify the service layer method is called once
    verify(recipeService, times(1)).deleteRecipeById(recipeId);
  }

  /**
   * Test successful retrieval of an existing recipe. Verify returns 200 OK status and recipe data
   */
  @Test
  void getRecipe_Found() throws Exception {
    // Arrange: Mock the service layer to return an existing recipe
    when(recipeService.getRecipeById(anyLong())).thenReturn(sampleRecipe);
    Long recipeId = sampleRecipe.getId();

    // Act & Assert: Send GET request and verify response status and returned JSON content
    mockMvc
        .perform(get("/recipe/{id}", recipeId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(sampleRecipe.getId().intValue())))
        .andExpect(jsonPath("$.name", is(sampleRecipe.getName())))
        .andExpect(jsonPath("$.description", is(sampleRecipe.getDescription())));

    // Verify the service layer method is called once
    verify(recipeService, times(1)).getRecipeById(recipeId);
  }

  @Test
  void getRecipe_NotFound() throws Exception {
    // Arrange: Mock the service layer to return an existing recipe
    when(recipeService.getRecipeById(anyLong())).thenReturn(null);
    Long recipeId = sampleRecipe.getId();

    // Act & Assert: Send GET request and verify response status and returned JSON content
    mockMvc.perform(get("/recipe/{id}", recipeId)).andExpect(status().isNotFound());

    // Verify the service layer method is called once
    verify(recipeService, times(1)).getRecipeById(recipeId);
  }

  /** Test successful retrieval of all recipes. Verify returns 200 OK status and recipe list */
  @Test
  void getAllRecipes_Success() throws Exception {
    // Arrange: Create a list of recipes and mock the service layer to return it
    Recipe recipe1 = new Recipe();
    recipe1.setId(1L);
    recipe1.setName("Recipe One");
    Recipe recipe2 = new Recipe();
    recipe2.setId(2L);
    recipe2.setName("Recipe Two");
    List<Recipe> recipes = List.of(recipe1, recipe2);

    when(recipeService.getAllRecipes()).thenReturn(recipes);

    // Act & Assert: Send GET request and verify response status and returned JSON content
    mockMvc
        .perform(get("/recipe"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()", is(recipes.size())))
        .andExpect(jsonPath("$[0].id", is(recipe1.getId().intValue())))
        .andExpect(jsonPath("$[0].name", is(recipe1.getName())))
        .andExpect(jsonPath("$[1].id", is(recipe2.getId().intValue())))
        .andExpect(jsonPath("$[1].name", is(recipe2.getName())));

    // Verify the service layer method is called once
    verify(recipeService, times(1)).getAllRecipes();
  }

  @Test
  void getAllPublishedRecipes_Success() throws Exception {
    // Arrange: Create a list of recipes and mock the service layer to return it
    Recipe recipe1 = new Recipe();
    recipe1.setId(1L);
    recipe1.setName("Recipe One");
    Recipe recipe2 = new Recipe();
    recipe2.setId(2L);
    recipe2.setName("Recipe Two");
    List<Recipe> recipes = List.of(recipe1, recipe2);

    when(recipeService.getAllPublishedRecipes()).thenReturn(recipes);

    // Act & Assert: Send GET request and verify response status and returned JSON content
    mockMvc
        .perform(get("/recipe/published"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()", is(recipes.size())))
        .andExpect(jsonPath("$[0].id", is(recipe1.getId().intValue())))
        .andExpect(jsonPath("$[0].name", is(recipe1.getName())))
        .andExpect(jsonPath("$[1].id", is(recipe2.getId().intValue())))
        .andExpect(jsonPath("$[1].name", is(recipe2.getName())));

    // Verify the service layer method is called once
    verify(recipeService, times(1)).getAllPublishedRecipes();
  }

  @Test
  void searchRecipesByCreatorId_Success() throws Exception {
    // Arrange: Create a list of recipes and mock the service layer to return it
    int creatorId = 1;
    Recipe recipe1 = new Recipe();
    recipe1.setId(1L);
    recipe1.setName("Recipe One");
    Recipe recipe2 = new Recipe();
    recipe2.setId(2L);
    recipe2.setName("Recipe Two");
    List<Recipe> recipes = List.of(recipe1, recipe2);

    when(recipeService.getRecipesByCreatorId(creatorId)).thenReturn(recipes);

    // Act & Assert: Send GET request and verify response status and returned JSON content
    mockMvc
        .perform(get("/recipe/creator/{creatorId}", 1L))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()", is(recipes.size())))
        .andExpect(jsonPath("$[0].id", is(recipe1.getId().intValue())))
        .andExpect(jsonPath("$[0].name", is(recipe1.getName())))
        .andExpect(jsonPath("$[1].id", is(recipe2.getId().intValue())))
        .andExpect(jsonPath("$[1].name", is(recipe2.getName())));

    // Verify the service layer method is called once
    verify(recipeService, times(1)).getRecipesByCreatorId(eq(creatorId));
  }

  @Test
  void searchRecipesByCreatorId_Success_0Recipes() throws Exception {
    // Arrange: Create a list of recipes and mock the service layer to return it
    int creatorId = 1;
    List<Recipe> recipes = null;

    when(recipeService.getRecipesByCreatorId(creatorId)).thenReturn(recipes);

    // Act & Assert: Send GET request and verify response status and returned JSON content
    mockMvc.perform(get("/recipe/creator/{creatorId}", 1L)).andExpect(status().isOk());

    // Verify the service layer method is called once
    verify(recipeService, times(1)).getRecipesByCreatorId(eq(creatorId));
  }

  @Test
  void getRecipeWithReviews_Success() throws Exception {
    // Arrange: Create a list of recipes and mock the service layer to return it
    long recipeId = 1L;
    Recipe recipe1 = new Recipe();
    recipe1.setId(1L);
    recipe1.setName("Recipe One");
    RecipeWithReviews recipe1Review = new RecipeWithReviews(recipe1, null);

    when(recipeReviewService.getRecipeWithReviewsById(recipeId)).thenReturn(recipe1Review);

    // Act & Assert: Send GET request and verify response status and returned JSON content
    mockMvc
        .perform(get("/recipe/{id}/with-reviews", recipeId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("recipe.id", is(recipe1.getId().intValue())))
        .andExpect(jsonPath("recipe.name", is(recipe1.getName())));

    // Verify the service layer method is called once
    verify(recipeReviewService, times(1)).getRecipeWithReviewsById(eq(recipeId));
  }

  @Test
  void getRecipeWithReviews_Failure() throws Exception {
    // Arrange: Create a list of recipes and mock the service layer to return it
    long recipeId = 1L;
    Recipe recipe1 = new Recipe();
    recipe1.setId(1L);
    recipe1.setName("Recipe One");
    RecipeWithReviews recipe1Review = null;

    when(recipeReviewService.getRecipeWithReviewsById(recipeId)).thenReturn(recipe1Review);

    // Act & Assert: Send GET request and verify response status and returned JSON content
    mockMvc.perform(get("/recipe/{id}/with-reviews", recipeId)).andExpect(status().isNotFound());

    // Verify the service layer method is called once
    verify(recipeReviewService, times(1)).getRecipeWithReviewsById(eq(recipeId));
  }

  @Test
  void getAllRecipesWithReviews_Success() throws Exception {
    // Arrange: Create a list of recipes and mock the service layer to return it
    List<RecipeWithReviews> recipeWithReviews = new ArrayList<>();
    {
      Recipe recipe1 = new Recipe();
      recipe1.setId(1L);
      recipe1.setName("Recipe One");
      RecipeWithReviews recipe1Review = new RecipeWithReviews(recipe1, null);
      recipeWithReviews.add(recipe1Review);
    }
    {
      Recipe recipe1 = new Recipe();
      recipe1.setId(2L);
      recipe1.setName("Recipe Two");
      RecipeWithReviews recipe1Review = new RecipeWithReviews(recipe1, null);
      recipeWithReviews.add(recipe1Review);
    }

    when(recipeReviewService.getAllRecipesWithReviews()).thenReturn(recipeWithReviews);

    // Act & Assert: Send GET request and verify response status and returned JSON content
    mockMvc
        .perform(get("/recipe/with-reviews"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].recipe.id", is(1)))
        .andExpect(jsonPath("$[0].recipe.name", is("Recipe One")));

    // Verify the service layer method is called once
    verify(recipeReviewService, times(1)).getAllRecipesWithReviews();
  }

  @Test
  void getRecipesViaRecommendation_UserReview() throws Exception {
    // Arrange: Create a list of recipes and mock the service layer to return it

    List<Recipe> recipes = new ArrayList<>();
    {
      Recipe recipe1 = new Recipe();
      recipe1.setId(1L);
      recipe1.setName("Recipe One");
      recipes.add(recipe1);
    }
    {
      Recipe recipe1 = new Recipe();
      recipe1.setId(2L);
      recipe1.setName("Recipe Two");
      recipes.add(recipe1);
    }

    when(recipePreferenceContext.recommend(any(), anyInt(), anyBoolean())).thenReturn(recipes);

    // Act & Assert: Send GET request and verify response status and returned JSON content
    mockMvc
        .perform(get("/recipe/recommend?userId=1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].name", is("Recipe One")));

    // Verify the service layer method is called once
    verify(recipePreferenceContext, times(1)).recommend(any(), anyInt(), anyBoolean());
  }

  @Test
  void getRecipesViaRecommendation_isByRating() throws Exception {
    // Arrange: Create a list of recipes and mock the service layer to return it

    List<Recipe> recipes = new ArrayList<>();
    {
      Recipe recipe1 = new Recipe();
      recipe1.setId(1L);
      recipe1.setName("Recipe One");
      recipes.add(recipe1);
    }
    {
      Recipe recipe1 = new Recipe();
      recipe1.setId(2L);
      recipe1.setName("Recipe Two");
      recipes.add(recipe1);
    }

    when(recipePreferenceContext.recommend(any(), anyInt(), anyBoolean())).thenReturn(recipes);

    // Act & Assert: Send GET request and verify response status and returned JSON content
    mockMvc
        .perform(get("/recipe/recommend?isByRating=true"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].name", is("Recipe One")));

    // Verify the service layer method is called once
    verify(recipePreferenceContext, times(1)).recommend(any(), anyInt(), anyBoolean());
  }

  @Test
  void getRecipesViaRecommendation_isByDifficulty() throws Exception {
    // Arrange: Create a list of recipes and mock the service layer to return it

    List<Recipe> recipes = new ArrayList<>();
    {
      Recipe recipe1 = new Recipe();
      recipe1.setId(1L);
      recipe1.setName("Recipe One");
      recipes.add(recipe1);
    }
    {
      Recipe recipe1 = new Recipe();
      recipe1.setId(2L);
      recipe1.setName("Recipe Two");
      recipes.add(recipe1);
    }

    when(recipePreferenceContext.recommend(any(), anyInt(), anyBoolean())).thenReturn(recipes);

    // Act & Assert: Send GET request and verify response status and returned JSON content
    mockMvc
        .perform(get("/recipe/recommend?isByRating=false"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].name", is("Recipe One")));

    // Verify the service layer method is called once
    verify(recipePreferenceContext, times(1)).recommend(any(), anyInt(), anyBoolean());
  }

  @Test
  void postReviewRating_Success() throws Exception {
    // Arrange: Create a list of recipes and mock the service layer to return it

    Long recipeId = 1L;
    double rating = 4.5;
    boolean updateSuccessful = true;
    when(recipeService.updateRecipeRating(anyLong(), anyDouble())).thenReturn(updateSuccessful);

    mockMvc
        .perform(
            post("/recipe/{id}/rating", recipeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rating)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    // Act & Assert: Send POST request and verify response status and content

    // Verify the service layer method is called once
    verify(recipeService, times(1)).updateRecipeRating(anyLong(), anyDouble());
  }
}
