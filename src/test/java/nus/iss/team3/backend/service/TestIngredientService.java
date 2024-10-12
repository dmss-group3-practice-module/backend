package nus.iss.team3.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import nus.iss.team3.backend.dataaccess.IIngredientDataAccess;
import nus.iss.team3.backend.entity.Ingredient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class TestIngredientService {

  @InjectMocks private IngredientService ingredientService;

  @Mock private IIngredientDataAccess ingredientDataAccess;

  @Test
  public void addIngredient() {
    // null ingredient
    assertFalse(ingredientService.addIngredient(null));

    // null/empty name
    Ingredient inputIngredient = new Ingredient();

    inputIngredient.setName(null);
    assertFalse(ingredientService.addIngredient(inputIngredient));

    inputIngredient.setName("");
    assertFalse(ingredientService.addIngredient(inputIngredient));

    // valid ingredient name, null/empty uom
    inputIngredient.setName("apple");
    inputIngredient.setUom("");
    assertFalse(ingredientService.addIngredient(inputIngredient));
    inputIngredient.setUom(null);
    assertFalse(ingredientService.addIngredient(inputIngredient));

    // valid ingredient name and uom, missing/invalid quantity
    inputIngredient.setUom("kg");
    inputIngredient.setQuantity(-1.0);
    assertFalse(ingredientService.addIngredient(inputIngredient));
    inputIngredient.setQuantity(0.0);
    assertFalse(ingredientService.addIngredient(inputIngredient));

    // valid ingredient name, uom and quantity, missing expiryDate
    inputIngredient.setQuantity(1.5);
    inputIngredient.setExpiryDate(null);
    assertFalse(ingredientService.addIngredient(inputIngredient));

    // valid ingredient with name, uom, quantity and expiryDate, missing userId
    LocalDate localDate = LocalDate.of(2024, 10, 10);
    Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    inputIngredient.setExpiryDate(date);
    assertFalse(ingredientService.addIngredient(inputIngredient));

    // valid ingredient with name, uom, quantity, expiryDate and userId
    inputIngredient.setUserId(1);
    when(ingredientDataAccess.addIngredient(inputIngredient)).thenReturn(true);
    assertTrue(ingredientService.addIngredient(inputIngredient));
  }

  @Test
  public void getIngredient() {
    // non-existing ingredient
    Integer ingredientId = 1;
    when(ingredientDataAccess.getIngredientById(ingredientId)).thenReturn(null);
    assertNull(ingredientService.getIngredientById(ingredientId));

    // existing ingredient
    Ingredient existingIngredient = new Ingredient();
    existingIngredient.setId(ingredientId);
    when(ingredientDataAccess.getIngredientById(ingredientId)).thenReturn(existingIngredient);
    assertEquals(existingIngredient, ingredientService.getIngredientById(ingredientId));

    // get all ingredients by userId (no ingredients)
    Integer userId = 1;
    when(ingredientDataAccess.getIngredientsByUser(userId)).thenReturn(Arrays.asList());
    List<Ingredient> result = ingredientService.getIngredientsByUser(userId);
    assertTrue(result.isEmpty());

    // get all ingredients by userId (with ingredients)
    Ingredient apple = new Ingredient();
    Ingredient orange = new Ingredient();
    List<Ingredient> ingredientList = Arrays.asList(apple, orange);
    when(ingredientDataAccess.getIngredientsByUser(userId)).thenReturn(ingredientList);

    List<Ingredient> ingredients = ingredientService.getIngredientsByUser(userId);
    assertEquals(2, ingredients.size());
  }

  @Test
  public void updateIngredient() {
    Ingredient nullIngredient = null;
    assertFalse(ingredientService.updateIngredient(nullIngredient));

    // missing id
    Ingredient inputIngredient = new Ingredient();
    inputIngredient.setName("apple");
    inputIngredient.setQuantity(1.0);
    inputIngredient.setUom("kg");
    inputIngredient.setUserId(1);
    LocalDate localDate = LocalDate.of(2024, 10, 10);
    Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    inputIngredient.setExpiryDate(date);
    assertFalse(ingredientService.updateIngredient(inputIngredient));

    // set id to have a valid ingredient
    inputIngredient.setId(1);

    // update a non-existing ingredient
    when(ingredientDataAccess.getIngredientById(1)).thenReturn(null);
    assertFalse(ingredientService.updateIngredient(inputIngredient));

    // update a valid ingredient
    when(ingredientDataAccess.getIngredientById(1)).thenReturn(inputIngredient);
    when(ingredientDataAccess.updateIngredient(inputIngredient)).thenReturn(true);
    assertTrue(ingredientService.updateIngredient(inputIngredient));
  }

  @Test
  public void deleteIngredient() {
    // delete a non-exising ingredient
    Integer ingredientId = 1;
    when(ingredientDataAccess.getIngredientById(ingredientId)).thenReturn(null);
    assertFalse(ingredientService.deleteIngredientById(ingredientId));

    // delete an existing ingredient
    Ingredient existingIngredient = new Ingredient();
    existingIngredient.setId(ingredientId);
    when(ingredientDataAccess.getIngredientById(ingredientId)).thenReturn(existingIngredient);
    when(ingredientDataAccess.deleteIngredientById(ingredientId)).thenReturn(true);
    assertTrue(ingredientService.deleteIngredientById(ingredientId));
  }
}
