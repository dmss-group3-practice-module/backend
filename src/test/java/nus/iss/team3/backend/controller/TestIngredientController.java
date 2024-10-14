package nus.iss.team3.backend.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import nus.iss.team3.backend.entity.Ingredient;
import nus.iss.team3.backend.service.IIngredientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(IngredientController.class)
public class TestIngredientController {
  @Autowired private MockMvc mockMvc;

  @MockBean private IIngredientService ingredientService;

  private ObjectMapper objectMapper;

  @BeforeEach
  public void setup() {
    objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Test
  public void testAddIngredient() throws Exception {
    // Success
    Ingredient ingredient = createValidIngredient();
    when(ingredientService.addIngredient(any(Ingredient.class))).thenReturn(true);

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
    when(ingredientService.addIngredient(any(Ingredient.class)))
        .thenThrow(new IllegalArgumentException("Ingredient userId must be valid"));

    // Failure - invalid ingredient name
    ingredient.setUserId(1);
    ingredient.setName(null);
    when(ingredientService.addIngredient(any(Ingredient.class)))
        .thenThrow(new IllegalArgumentException("Ingredient name cannot be empty or blank"));

    // Failure - invalid ingredient uom
    ingredient.setName("apple");
    ingredient.setUom(null);
    when(ingredientService.addIngredient(any(Ingredient.class)))
        .thenThrow(new IllegalArgumentException("Ingredient uom cannot be empty or blank"));

    // Failure - invalid ingredient quantity
    ingredient.setUom("kg");
    ingredient.setQuantity(0.0);
    when(ingredientService.addIngredient(any(Ingredient.class)))
        .thenThrow(new IllegalArgumentException("Ingredient quantity must be greater than 0"));

    // Failure - missing date
    ingredient.setQuantity(1.0);
    ingredient.setExpiryDate(null);
    when(ingredientService.addIngredient(any(Ingredient.class)))
        .thenThrow(new IllegalArgumentException("Ingredient expiry date cannot be null"));
  }

  @Test
  public void testGetIngredient() throws Exception {
    Ingredient ingredient = createValidIngredient();
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
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("false"));

    // get all ingredients belonging to a user
    List<Ingredient> ingredientList =
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
  public void testUpdateIngredient() throws Exception {
    Ingredient ingredient = createValidIngredient();
    ingredient.setId(1);

    // success
    when(ingredientService.updateIngredient(any(Ingredient.class))).thenReturn(true);
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
    when(ingredientService.updateIngredient(any(Ingredient.class)))
        .thenThrow(new IllegalArgumentException("Ingredient id must be valid"));
    mockMvc
        .perform(
            post("/ingredient/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ingredient)))
        .andExpect(status().isInternalServerError());

    // non existing ingredient
    ingredient.setId(999);
    when(ingredientService.updateIngredient(any(Ingredient.class))).thenReturn(false);

    mockMvc
        .perform(
            post("/ingredient/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ingredient)))
        .andExpect(status().isInternalServerError());
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
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("false"));
  }

  private Ingredient createValidIngredient() {
    Ingredient ingredient = new Ingredient();
    ingredient.setName("apple");
    ingredient.setUserId(1);
    ingredient.setUom("kg");
    ingredient.setQuantity(1.0);
    LocalDate localDate = LocalDate.of(2024, 10, 10);
    Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    ingredient.setExpiryDate(date);
    return ingredient;
  }
}