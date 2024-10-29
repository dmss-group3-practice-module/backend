package nus.iss.team3.backend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import nus.iss.team3.backend.dataaccess.IIngredientDataAccess;
import nus.iss.team3.backend.domainService.ingredient.IngredientService;
import nus.iss.team3.backend.entity.UserIngredient;
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
    assertThrows(IllegalArgumentException.class, () -> ingredientService.addIngredient(null));

    // null/empty name
    UserIngredient inputIngredient = new UserIngredient();

    inputIngredient.setName(null);
    assertThrows(
        IllegalArgumentException.class, () -> ingredientService.addIngredient(inputIngredient));

    inputIngredient.setName("");
    assertThrows(
        IllegalArgumentException.class, () -> ingredientService.addIngredient(inputIngredient));

    // valid ingredient name, null/empty uom
    inputIngredient.setName("apple");
    inputIngredient.setUom("");
    assertThrows(
        IllegalArgumentException.class, () -> ingredientService.addIngredient(inputIngredient));
    inputIngredient.setUom(null);
    assertThrows(
        IllegalArgumentException.class, () -> ingredientService.addIngredient(inputIngredient));

    // valid ingredient name and uom, missing/invalid quantity
    inputIngredient.setUom("kg");
    inputIngredient.setQuantity(-1.0);
    assertThrows(
        IllegalArgumentException.class, () -> ingredientService.addIngredient(inputIngredient));
    inputIngredient.setQuantity(0.0);
    assertThrows(
        IllegalArgumentException.class, () -> ingredientService.addIngredient(inputIngredient));

    // valid ingredient name, uom and quantity, missing expiryDate
    inputIngredient.setQuantity(1.5);
    inputIngredient.setExpiryDate(null);
    assertThrows(
        IllegalArgumentException.class, () -> ingredientService.addIngredient(inputIngredient));

    // valid ingredient with name, uom, quantity and expiryDate, missing userId
    LocalDate localDate = LocalDate.of(2024, 10, 10);
    Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    inputIngredient.setExpiryDate(date);
    assertThrows(
        IllegalArgumentException.class, () -> ingredientService.addIngredient(inputIngredient));

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
    UserIngredient existingIngredient = new UserIngredient();
    existingIngredient.setId(ingredientId);
    when(ingredientDataAccess.getIngredientById(ingredientId)).thenReturn(existingIngredient);
    assertEquals(existingIngredient, ingredientService.getIngredientById(ingredientId));

    // get all ingredients by userId (no ingredients)
    Integer userId = 1;
    when(ingredientDataAccess.getIngredientsByUser(userId)).thenReturn(Arrays.asList());
    List<UserIngredient> result = ingredientService.getIngredientsByUser(userId);
    assertTrue(result.isEmpty());

    // get all ingredients by userId (with ingredients)
    UserIngredient apple = new UserIngredient();
    UserIngredient orange = new UserIngredient();
    List<UserIngredient> ingredientList = Arrays.asList(apple, orange);
    when(ingredientDataAccess.getIngredientsByUser(userId)).thenReturn(ingredientList);

    List<UserIngredient> ingredients = ingredientService.getIngredientsByUser(userId);
    assertEquals(2, ingredients.size());
  }

  @Test
  public void updateIngredient() {
    // cannot update null ingredient
    assertThrows(IllegalArgumentException.class, () -> ingredientService.updateIngredient(null));

    // missing ingredient id
    UserIngredient inputIngredient = new UserIngredient();
    inputIngredient.setName("apple");
    inputIngredient.setQuantity(1.0);
    inputIngredient.setUom("kg");
    inputIngredient.setUserId(1);
    LocalDate localDate = LocalDate.of(2024, 10, 10);
    Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    inputIngredient.setExpiryDate(date);
    assertThrows(
        IllegalArgumentException.class, () -> ingredientService.updateIngredient(inputIngredient));

    // set id to have a valid ingredient
    inputIngredient.setId(1);

    // update a non-existing ingredient
    when(ingredientDataAccess.getIngredientById(1)).thenReturn(null);
    assertThrows(
        IllegalArgumentException.class, () -> ingredientService.updateIngredient(inputIngredient));

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
    UserIngredient existingIngredient = new UserIngredient();
    existingIngredient.setId(ingredientId);
    when(ingredientDataAccess.getIngredientById(ingredientId)).thenReturn(existingIngredient);
    when(ingredientDataAccess.deleteIngredientById(ingredientId)).thenReturn(true);
    assertTrue(ingredientService.deleteIngredientById(ingredientId));
  }
}
