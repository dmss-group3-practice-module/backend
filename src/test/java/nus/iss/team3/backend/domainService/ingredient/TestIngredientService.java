package nus.iss.team3.backend.domainService.ingredient;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import nus.iss.team3.backend.dataaccess.IIngredientDataAccess;
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

    UserIngredient inputIngredient = new UserIngredient();

    // test userId
    inputIngredient.setUserId(-1);
    assertThrows(
        IllegalArgumentException.class, () -> ingredientService.addIngredient(inputIngredient));
    inputIngredient.setUserId(0);
    assertThrows(
        IllegalArgumentException.class, () -> ingredientService.addIngredient(inputIngredient));
    inputIngredient.setUserId(1);

    // test name
    inputIngredient.setName(null);
    assertThrows(
        IllegalArgumentException.class, () -> ingredientService.addIngredient(inputIngredient));

    inputIngredient.setName("");
    assertThrows(
        IllegalArgumentException.class, () -> ingredientService.addIngredient(inputIngredient));
    inputIngredient.setName("apple");

    // test uom
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

    // valid ingredient with name, uom, quantity, expiryDate and userId
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
    when(ingredientDataAccess.getIngredientsByUser(userId)).thenReturn(List.of());
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
  public void getIngredientsByName() {
    // non-existing ingredient
    String ingredientName = "apple";

    when(ingredientDataAccess.getIngredientsByName(ingredientName)).thenReturn(null);
    assertNull(ingredientService.getIngredientsByName(ingredientName));

    // existing ingredient
    when(ingredientDataAccess.getIngredientsByName(ingredientName)).thenReturn(new ArrayList<>());
    List<UserIngredient> result = ingredientService.getIngredientsByName(ingredientName);
    assertNotNull(ingredientService.getIngredientsByName(ingredientName));
    assertEquals(0, result.size());

    // get all ingredients by userId (no ingredients)
    List<UserIngredient> returnList = new ArrayList<>();
    {
      UserIngredient apple = new UserIngredient();
      apple.setId(1);
      apple.setName("apple");
      apple.setUom("kg");
      apple.setQuantity(1.0);
      apple.setExpiryDate(
          Date.from(LocalDate.of(2024, 10, 10).atStartOfDay(ZoneId.systemDefault()).toInstant()));
      returnList.add(apple);
    }
    when(ingredientDataAccess.getIngredientsByName(ingredientName)).thenReturn(returnList);
    result = ingredientService.getIngredientsByName(ingredientName);
    assertNotNull(ingredientService.getIngredientsByName(ingredientName));
    assertEquals(1, result.size());
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

  @Test
  public void deleteIngredientsByUser() {
    // delete a non-exising ingredient
    Integer userId = 1;
    when(ingredientDataAccess.deleteIngredientsByUser(userId)).thenReturn(false);
    assertFalse(ingredientService.deleteIngredientsByUser(userId));

    // delete an existing ingredient
    when(ingredientDataAccess.deleteIngredientsByUser(userId)).thenReturn(true);
    assertTrue(ingredientService.deleteIngredientsByUser(userId));
  }

  @Test
  public void getExpiringIngredients_returnNullList() {
    // delete a non-exising ingredient
    Integer userId = 1;
    int days = 1;

    List<UserIngredient> returnList = null;
    when(ingredientDataAccess.getIngredientsByUser(userId)).thenReturn(returnList);

    List<UserIngredient> results = ingredientService.getExpiringIngredients(userId, days);

    assertEquals(results.size(), 0);
  }

  @Test
  public void getExpiringIngredients_returnEmptyList() {
    // delete a non-exising ingredient
    Integer userId = 1;
    int days = 1;

    List<UserIngredient> returnList = new ArrayList<>();
    when(ingredientDataAccess.getIngredientsByUser(userId)).thenReturn(returnList);

    List<UserIngredient> results = ingredientService.getExpiringIngredients(userId, days);

    assertEquals(results.size(), 0);
  }

  @Test
  public void getExpiringIngredients_validList_1ValidItem() {
    LocalDate localDate = LocalDate.now();

    // delete a non-exising ingredient
    Integer userId = 1;
    int days = 1;

    List<UserIngredient> returnList = new ArrayList<>();
    {
      UserIngredient apple = new UserIngredient();
      apple.setId(1);
      apple.setName("apple");
      apple.setUom("kg");
      apple.setQuantity(1.0);
      apple.setExpiryDate(
          Date.from(
              LocalDate.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth())
                  .atStartOfDay(ZoneId.systemDefault())
                  .toInstant()));
      returnList.add(apple);
    }
    when(ingredientDataAccess.getIngredientsByUser(userId)).thenReturn(returnList);

    List<UserIngredient> results = ingredientService.getExpiringIngredients(userId, days);

    assertEquals(results.size(), 1);
  }

  @Test
  public void getExpiringIngredients_validList_2ValidItem_2Not() {
    LocalDate today = LocalDate.now();

    // delete a non-exising ingredient
    Integer userId = 1;
    int days = 1;

    List<UserIngredient> returnList = new ArrayList<>();
    {
      UserIngredient apple = new UserIngredient();
      apple.setId(1);
      apple.setName("apple");
      apple.setUom("kg");
      apple.setQuantity(1.0);
      apple.setExpiryDate(
          Date.from(
              LocalDate.of(today.getYear(), today.getMonth(), today.getDayOfMonth())
                  .atStartOfDay(ZoneId.systemDefault())
                  .toInstant()));
      returnList.add(apple);
    }
    {
      LocalDate itemDate = today.plusDays(1);
      UserIngredient apple = new UserIngredient();
      apple.setId(2);
      apple.setName("apple");
      apple.setUom("kg");
      apple.setQuantity(1.0);
      apple.setExpiryDate(
          Date.from(
              LocalDate.of(itemDate.getYear(), itemDate.getMonth(), itemDate.getDayOfMonth())
                  .atStartOfDay(ZoneId.systemDefault())
                  .toInstant()));
      returnList.add(apple);
    }
    {
      LocalDate itemDate = today.plusDays(-1);
      UserIngredient apple = new UserIngredient();
      apple.setId(3);
      apple.setName("apple");
      apple.setUom("kg");
      apple.setQuantity(1.0);
      apple.setExpiryDate(
          Date.from(
              LocalDate.of(itemDate.getYear(), itemDate.getMonth(), itemDate.getDayOfMonth())
                  .atStartOfDay(ZoneId.systemDefault())
                  .toInstant()));
      returnList.add(apple);
    }
    {
      UserIngredient apple = new UserIngredient();
      apple.setId(4);
      apple.setName("apple");
      apple.setUom("kg");
      apple.setQuantity(1.0);
      apple.setExpiryDate(
          Date.from(
              LocalDate.of(today.getYear(), today.getMonth(), today.getDayOfMonth())
                  .atStartOfDay(ZoneId.systemDefault())
                  .toInstant()));
      returnList.add(apple);
    }
    {
      LocalDate itemDate = today.plusDays(2);
      UserIngredient apple = new UserIngredient();
      apple.setId(5);
      apple.setName("apple");
      apple.setUom("kg");
      apple.setQuantity(1.0);
      apple.setExpiryDate(
          Date.from(
              LocalDate.of(itemDate.getYear(), itemDate.getMonth(), itemDate.getDayOfMonth())
                  .atStartOfDay(ZoneId.systemDefault())
                  .toInstant()));
      returnList.add(apple);
    }
    when(ingredientDataAccess.getIngredientsByUser(userId)).thenReturn(returnList);

    List<UserIngredient> results = ingredientService.getExpiringIngredients(userId, days);

    assertEquals(3, results.size());
  }

  @Test
  public void getExpiringIngredientsInRange_null() {

    // delete a non-exising ingredient
    Integer userId = 1;
    int days = 1;
    List<UserIngredient> userIngredients = null;
    when(ingredientDataAccess.getExpiringIngredientsInRange()).thenReturn(userIngredients);

    List<UserIngredient> results = ingredientService.getExpiringIngredientsInRange();

    assertEquals(results.size(), 0);
  }

  @Test
  public void getExpiringIngredientsInRange_emptyList() {

    // delete a non-exising ingredient
    Integer userId = 1;
    int days = 1;
    List<UserIngredient> userIngredients = new ArrayList<>();
    when(ingredientDataAccess.getExpiringIngredientsInRange()).thenReturn(userIngredients);

    List<UserIngredient> results = ingredientService.getExpiringIngredientsInRange();

    assertEquals(results.size(), 0);
  }

  @Test
  public void getExpiringIngredientsInRange_1itemList() {

    // delete a non-exising ingredient
    Integer userId = 1;
    int days = 1;
    List<UserIngredient> userIngredients = new ArrayList<>();
    {
      UserIngredient apple = new UserIngredient();
      apple.setId(1);
      apple.setName("apple");
      apple.setUom("kg");
      apple.setQuantity(1.0);
      apple.setExpiryDate(
          Date.from(LocalDate.of(2024, 10, 10).atStartOfDay(ZoneId.systemDefault()).toInstant()));
      userIngredients.add(apple);
    }
    when(ingredientDataAccess.getExpiringIngredientsInRange()).thenReturn(userIngredients);

    List<UserIngredient> results = ingredientService.getExpiringIngredientsInRange();

    assertEquals(results.size(), 1);
  }

  @Test
  public void getExpiringIngredientsInRange_misitemList_sorted() {

    // delete a non-exising ingredient
    Integer userId = 1;
    int days = 1;
    List<UserIngredient> userIngredients = new ArrayList<>();
    {
      UserIngredient userIngredient = new UserIngredient();
      userIngredient.setId(1);
      userIngredient.setUserId(1);
      userIngredient.setName("second apple for user 1");
      userIngredient.setUom("kg");
      userIngredient.setQuantity(1.0);
      userIngredient.setExpiryDate(
          Date.from(LocalDate.of(2024, 10, 10).atStartOfDay(ZoneId.systemDefault()).toInstant()));
      userIngredients.add(userIngredient);
    }
    {
      UserIngredient userIngredient = new UserIngredient();
      userIngredient.setId(2);
      userIngredient.setUserId(2);
      userIngredient.setName("first apple for user 2");
      userIngredient.setUom("kg");
      userIngredient.setQuantity(1.0);
      userIngredient.setExpiryDate(
          Date.from(LocalDate.of(2024, 10, 10).atStartOfDay(ZoneId.systemDefault()).toInstant()));
      userIngredients.add(userIngredient);
    }
    {
      UserIngredient userIngredient = new UserIngredient();
      userIngredient.setId(3);
      userIngredient.setUserId(1);
      userIngredient.setName("third apple for user 1");
      userIngredient.setUom("kg");
      userIngredient.setQuantity(1.0);
      userIngredient.setExpiryDate(
          Date.from(LocalDate.of(2025, 10, 10).atStartOfDay(ZoneId.systemDefault()).toInstant()));
      userIngredients.add(userIngredient);
    }
    {
      UserIngredient userIngredient = new UserIngredient();
      userIngredient.setId(4);
      userIngredient.setUserId(1);
      userIngredient.setName("first apple for user 1");
      userIngredient.setUom("kg");
      userIngredient.setQuantity(1.0);
      userIngredient.setExpiryDate(
          Date.from(LocalDate.of(2023, 10, 10).atStartOfDay(ZoneId.systemDefault()).toInstant()));
      userIngredients.add(userIngredient);
    }
    {
      UserIngredient userIngredient = new UserIngredient();
      userIngredient.setId(5);
      userIngredient.setUserId(2);
      userIngredient.setName("second apple for user 2");
      userIngredient.setUom("kg");
      userIngredient.setQuantity(1.0);
      userIngredient.setExpiryDate(
          Date.from(LocalDate.of(2024, 10, 10).atStartOfDay(ZoneId.systemDefault()).toInstant()));
      userIngredients.add(userIngredient);
    }
    when(ingredientDataAccess.getExpiringIngredientsInRange()).thenReturn(userIngredients);

    List<UserIngredient> results = ingredientService.getExpiringIngredientsInRange();

    assertEquals(results.size(), 5);
    assertEquals("first apple for user 1", results.get(0).getName());
    assertEquals("second apple for user 1", results.get(1).getName());
    assertEquals("third apple for user 1", results.get(2).getName());
    assertEquals("first apple for user 2", results.get(3).getName());
    assertEquals("second apple for user 2", results.get(4).getName());
  }
}
