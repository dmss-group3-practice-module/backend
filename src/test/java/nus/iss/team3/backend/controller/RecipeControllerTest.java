package nus.iss.team3.backend.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import java.util.List;
import nus.iss.team3.backend.businessService.recipeReview.IRecipeReviewService;
import nus.iss.team3.backend.domainService.recipe.IRecipeService;
import nus.iss.team3.backend.entity.ERecipeStatus;
import nus.iss.team3.backend.entity.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Unit test class: RecipeControllerTest is used to test various endpoints of the RecipeController
 * class to ensure its behavior meets expectations.
 */
@WebMvcTest(RecipeController.class)
public class RecipeControllerTest {

  @Autowired private MockMvc mockMvc; // Tool for simulating HTTP requests

  @MockBean private IRecipeService recipeService; // Mocking the service layer dependency

  @MockBean
  private IRecipeReviewService recipeReviewService; // Mocking the service layer dependency

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
    sampleRecipe.setCookingTimeInSec(3600);
    sampleRecipe.setDifficultyLevel(2);
    sampleRecipe.setRating(4.5);
    sampleRecipe.setStatus(ERecipeStatus.DRAFT);
    sampleRecipe.setCuisine("Chinese");
    sampleRecipe.setCreateDatetime(new Timestamp(System.currentTimeMillis()));
    sampleRecipe.setUpdateDatetime(new Timestamp(System.currentTimeMillis()));
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

  /**
   * Test successful search for recipes by name. Verify returns 200 OK status and matching recipe
   * list
   */
  @Test
  void searchRecipes_Found() throws Exception {
    // Arrange: Create a matching recipe list and mock the service layer to return it
    String searchName = "Chicken";
    Recipe recipe = new Recipe();
    recipe.setId(1L);
    recipe.setName("Chicken Curry");
    List<Recipe> recipes = List.of(recipe);

    when(recipeService.getRecipesByName(searchName)).thenReturn(recipes);

    // Act & Assert: Send GET request and verify response status and returned JSON content
    mockMvc
        .perform(get("/recipe/search").param("name", searchName))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()", is(recipes.size())))
        .andExpect(jsonPath("$[0].id", is(recipe.getId().intValue())))
        .andExpect(jsonPath("$[0].name", is(recipe.getName())));

    // Verify the service layer method is called once
    verify(recipeService, times(1)).getRecipesByName(searchName);
  }
}
