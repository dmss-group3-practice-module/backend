package nus.iss.team3.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import nus.iss.team3.backend.businessService.ingredient.IIngredientBusinessService;
import nus.iss.team3.backend.domainService.ingredient.IIngredientService;
import nus.iss.team3.backend.domainService.user.IUserAccountService;
import nus.iss.team3.backend.entity.UserIngredient;
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

@WebMvcTest(IngredientController.class)
public class TestIngredientController {

  @MockBean private JwtUtil jwtUtil;
  @MockBean private IUserAccountService userAccountService;
  @InjectMocks private JwtRequestFilter jwtRequestFilter;
  @Autowired private WebApplicationContext context;

  @Autowired private MockMvc mockMvc;

  @MockBean private IIngredientService ingredientService;
  @MockBean private IIngredientBusinessService ingredientBusinessService;

  private ObjectMapper objectMapper;

  @BeforeEach
  public void setup() {
    objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    mockMvc =
        MockMvcBuilders.webAppContextSetup(context)
            .addFilters((OncePerRequestFilter) jwtRequestFilter)
            .build();
  }

  @Test
  public void testAddIngredient() throws Exception {
    // Success
    UserIngredient ingredient = createValidIngredient();
    when(ingredientService.addIngredient(any(UserIngredient.class))).thenReturn(true);

    mockMvc
        .perform(
            post("/ingredient/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ingredient)))
        .andExpect(status().isCreated())
        .andExpect(content().string("true"));

    // Failure - null ingredient
    mockMvc
        .perform(
            post("/ingredient/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(null)))
        .andExpect(status().isBadRequest());

    // Failure - invalid ingredient name
    ingredient.setUserId(0);
    when(ingredientService.addIngredient(any(UserIngredient.class)))
        .thenThrow(new IllegalArgumentException("Ingredient userId must be valid"));

    // Failure - invalid ingredient name
    ingredient.setUserId(1);
    ingredient.setName(null);
    when(ingredientService.addIngredient(any(UserIngredient.class)))
        .thenThrow(new IllegalArgumentException("Ingredient name cannot be empty or blank"));

    // Failure - invalid ingredient uom
    ingredient.setName("apple");
    ingredient.setUom(null);
    when(ingredientService.addIngredient(any(UserIngredient.class)))
        .thenThrow(new IllegalArgumentException("Ingredient uom cannot be empty or blank"));

    // Failure - invalid ingredient quantity
    ingredient.setUom("kg");
    ingredient.setQuantity(0.0);
    when(ingredientService.addIngredient(any(UserIngredient.class)))
        .thenThrow(new IllegalArgumentException("Ingredient quantity must be greater than 0"));

    // Failure - missing date
    ingredient.setQuantity(1.0);
    ingredient.setExpiryDate(null);
    when(ingredientService.addIngredient(any(UserIngredient.class)))
        .thenThrow(new IllegalArgumentException("Ingredient expiry date cannot be null"));
  }

  @Test
  public void testAddIngredient_addFail() throws Exception {
    // Success
    UserIngredient ingredient = createValidIngredient();
    when(ingredientService.addIngredient(any(UserIngredient.class))).thenReturn(false);

    mockMvc
        .perform(
            post("/ingredient/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ingredient)))
        .andExpect(status().isBadRequest());

    // Failure - null ingredient
    mockMvc
        .perform(
            post("/ingredient/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(null)))
        .andExpect(status().isBadRequest());

    // Failure - invalid ingredient name
    ingredient.setUserId(0);
    when(ingredientService.addIngredient(any(UserIngredient.class)))
        .thenThrow(new IllegalArgumentException("Ingredient userId must be valid"));

    // Failure - invalid ingredient name
    ingredient.setUserId(1);
    ingredient.setName(null);
    when(ingredientService.addIngredient(any(UserIngredient.class)))
        .thenThrow(new IllegalArgumentException("Ingredient name cannot be empty or blank"));

    // Failure - invalid ingredient uom
    ingredient.setName("apple");
    ingredient.setUom(null);
    when(ingredientService.addIngredient(any(UserIngredient.class)))
        .thenThrow(new IllegalArgumentException("Ingredient uom cannot be empty or blank"));

    // Failure - invalid ingredient quantity
    ingredient.setUom("kg");
    ingredient.setQuantity(0.0);
    when(ingredientService.addIngredient(any(UserIngredient.class)))
        .thenThrow(new IllegalArgumentException("Ingredient quantity must be greater than 0"));

    // Failure - missing date
    ingredient.setQuantity(1.0);
    ingredient.setExpiryDate(null);
    when(ingredientService.addIngredient(any(UserIngredient.class)))
        .thenThrow(new IllegalArgumentException("Ingredient expiry date cannot be null"));
  }

  @Test
  public void testGetIngredient() throws Exception {
    UserIngredient ingredient = createValidIngredient();
    ingredient.setId(1);

    // success
    when(ingredientService.getIngredientById(1)).thenReturn(ingredient);
    mockMvc
        .perform(get("/ingredient/get/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("apple"))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.uom").value("kg"))
        .andExpect(jsonPath("$.quantity").value(1.0))
        .andExpect(jsonPath("$.expiryDate").exists());

    // not found
    when(ingredientService.getIngredientById(999)).thenReturn(null);
    mockMvc
        .perform(get("/ingredient/get/999"))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("false"));

    // get all ingredients belonging to a user
    List<UserIngredient> ingredientList =
        Arrays.asList(createValidIngredient(), createValidIngredient());
    ingredientList.get(0).setId(1);
    ingredientList.get(1).setId(2);

    when(ingredientService.getIngredientsByUser(1)).thenReturn(ingredientList);

    mockMvc
        .perform(get("/ingredient/getAll/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[0].name").value("apple"))
        .andExpect(jsonPath("$[1].name").value("apple"));
  }

  @Test
  public void testGetIngredientByName() throws Exception {

    String ingredientName = "apple";
    List<UserIngredient> ingredientList = new ArrayList<>();
    {
      UserIngredient ingredient = createValidIngredient();
      ingredient.setId(1);
      ingredientList.add(ingredient);
    }
    // success
    when(ingredientService.getIngredientsByName(anyString())).thenReturn(ingredientList);
    mockMvc
        .perform(get("/ingredient/get/name/{name}", ingredientName))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("apple"))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].uom").value("kg"))
        .andExpect(jsonPath("$[0].quantity").value(1.0))
        .andExpect(jsonPath("$[0].expiryDate").exists());

    // nil return
    when(ingredientService.getIngredientsByName(anyString())).thenReturn(null);
    mockMvc
        .perform(get("/ingredient/get/name/{name}", ingredientName))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testUpdateIngredient() throws Exception {
    UserIngredient ingredient = createValidIngredient();
    ingredient.setId(1);

    // success
    when(ingredientService.updateIngredient(any(UserIngredient.class))).thenReturn(true);
    mockMvc
        .perform(
            post("/ingredient/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ingredient)))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));

    // missing ingredient update content
    mockMvc
        .perform(post("/ingredient/update").contentType(MediaType.APPLICATION_JSON).content(""))
        .andExpect(status().isBadRequest());

    // missing ingredient id
    when(ingredientService.updateIngredient(any(UserIngredient.class)))
        .thenThrow(new IllegalArgumentException("Ingredient id must be valid"));
    mockMvc
        .perform(
            post("/ingredient/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ingredient)))
        .andExpect(status().isBadRequest());

    // non existing ingredient
    ingredient.setId(999);
    when(ingredientService.updateIngredient(any(UserIngredient.class))).thenReturn(false);

    mockMvc
        .perform(
            post("/ingredient/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ingredient)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testDeleteIngredient() throws Exception {
    // success
    when(ingredientService.deleteIngredientById(1)).thenReturn(true);
    mockMvc
        .perform(delete("/ingredient/delete/1"))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));

    // delete non-existing ingredient
    when(ingredientService.deleteIngredientById(999)).thenReturn(false);
    mockMvc
        .perform(delete("/ingredient/delete/999"))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("false"));
  }

  @Test
  public void testDeleteIngredient_byUser() throws Exception {
    // success
    when(ingredientService.deleteIngredientsByUser(1)).thenReturn(true);
    mockMvc
        .perform(delete("/ingredient/delete/user/1"))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));

    // delete non-existing ingredient
    when(ingredientService.deleteIngredientsByUser(999)).thenReturn(false);
    mockMvc
        .perform(delete("/ingredient/delete/user/999"))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("false"));
  }

  private UserIngredient createValidIngredient() {
    UserIngredient ingredient = new UserIngredient();
    ingredient.setName("apple");
    ingredient.setUserId(1);
    ingredient.setUom("kg");
    ingredient.setQuantity(1.0);
    LocalDate localDate = LocalDate.of(2024, 10, 10);
    Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    ingredient.setExpiryDate(date);
    return ingredient;
  }

  @Test
  public void testGetExpiringIngredients() throws Exception {
    // Prepare test data
    List<UserIngredient> expiringIngredients =
        Arrays.asList(createValidIngredient(), createValidIngredient());
    expiringIngredients.get(0).setId(1);
    expiringIngredients.get(1).setId(2);

    // Test success with default days parameter
    when(ingredientService.getExpiringIngredients(1, 3)).thenReturn(expiringIngredients);
    mockMvc
        .perform(get("/ingredient/expiring-list").param("userId", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[1].id").value(2));

    // Test success with custom days parameter
    when(ingredientService.getExpiringIngredients(1, 5)).thenReturn(expiringIngredients);
    mockMvc
        .perform(get("/ingredient/expiring-list").param("userId", "1").param("days", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[1].id").value(2));

    // Test when service throws exception
    when(ingredientService.getExpiringIngredients(999, 3))
        .thenThrow(new RuntimeException("Error getting expiring ingredients"));
    mockMvc
        .perform(get("/ingredient/expiring-list").param("userId", "999"))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void getExpiringIngredientsInRange() throws Exception {
    // Prepare test data
    List<UserIngredient> expiringIngredients =
        Arrays.asList(createValidIngredient(), createValidIngredient());
    expiringIngredients.get(0).setId(1);
    expiringIngredients.get(1).setId(2);

    // Test success with default days parameter
    when(ingredientService.getExpiringIngredientsInRange()).thenReturn(expiringIngredients);
    mockMvc
        .perform(get("/ingredient/expiring-range"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[1].id").value(2));
  }

  @Test
  public void triggerExpiryCheck() throws Exception {
    // Prepare test data
    List<UserIngredient> expiringIngredients =
        Arrays.asList(createValidIngredient(), createValidIngredient());
    expiringIngredients.get(0).setId(1);
    expiringIngredients.get(1).setId(2);

    // Test success with default days parameter
    mockMvc.perform(get("/ingredient/trigger-expiry-check")).andExpect(status().isOk());
  }
}
