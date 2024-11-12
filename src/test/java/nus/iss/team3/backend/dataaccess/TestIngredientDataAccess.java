package nus.iss.team3.backend.dataaccess;

import static nus.iss.team3.backend.dataaccess.PostgresSqlStatement.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import nus.iss.team3.backend.dataaccess.postgres.PostgresDataAccess;
import nus.iss.team3.backend.entity.UserIngredient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TestIngredientDataAccess {

  @Mock private PostgresDataAccess postgresDataAccess;

  @InjectMocks private IngredientDataAccess ingredientDataAccess;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void addIngredient_success() {
    UserIngredient ingredient = new UserIngredient();

    when(postgresDataAccess.upsertStatement(eq(SQL_INGREDIENT_ADD), any())).thenReturn(1);

    boolean result = ingredientDataAccess.addIngredient(ingredient);

    assertTrue(result);
    verify(postgresDataAccess, times(1)).upsertStatement(eq(SQL_INGREDIENT_ADD), any());
  }

  @Test
  public void addIngredient_fail() {
    UserIngredient ingredient = new UserIngredient();

    when(postgresDataAccess.upsertStatement(eq(SQL_INGREDIENT_ADD), any())).thenReturn(0);

    boolean result = ingredientDataAccess.addIngredient(ingredient);

    assertFalse(result);
    verify(postgresDataAccess, times(1)).upsertStatement(eq(SQL_INGREDIENT_ADD), any());
  }

  @Test
  public void updateIngredient_success() {
    UserIngredient ingredient = new UserIngredient();

    when(postgresDataAccess.upsertStatement(eq(SQL_INGREDIENT_UPDATE), any())).thenReturn(1);

    boolean result = ingredientDataAccess.updateIngredient(ingredient);

    assertTrue(result);
    verify(postgresDataAccess, times(1)).upsertStatement(eq(SQL_INGREDIENT_UPDATE), any());
  }

  @Test
  public void updateIngredient_fail() {
    UserIngredient ingredient = new UserIngredient();

    when(postgresDataAccess.upsertStatement(eq(SQL_INGREDIENT_UPDATE), any())).thenReturn(0);

    boolean result = ingredientDataAccess.updateIngredient(ingredient);

    assertFalse(result);
    verify(postgresDataAccess, times(1)).upsertStatement(eq(SQL_INGREDIENT_UPDATE), any());
  }

  @Test
  public void updateIngredient_error() {
    UserIngredient ingredient = new UserIngredient();

    when(postgresDataAccess.upsertStatement(eq(SQL_INGREDIENT_UPDATE), any())).thenReturn(2);

    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              ingredientDataAccess.updateIngredient(ingredient);
            });

    assertEquals("Multiple rows affected during update", exception.getMessage());
    verify(postgresDataAccess, times(1)).upsertStatement(eq(SQL_INGREDIENT_UPDATE), any());
  }

  @Test
  public void deleteIngredientById_return1() {
    int ingredientId = 1;

    when(postgresDataAccess.upsertStatement(eq(SQL_INGREDIENT_DELETE), any())).thenReturn(1);

    boolean result = ingredientDataAccess.deleteIngredientById(ingredientId);

    assertTrue(result);
    verify(postgresDataAccess, times(1)).upsertStatement(eq(SQL_INGREDIENT_DELETE), any());
  }

  @Test
  public void deleteIngredientById_return0() {
    int ingredientId = 1;

    when(postgresDataAccess.upsertStatement(eq(SQL_INGREDIENT_DELETE), any())).thenReturn(0);

    boolean result = ingredientDataAccess.deleteIngredientById(ingredientId);

    assertFalse(result);
    verify(postgresDataAccess, times(1)).upsertStatement(eq(SQL_INGREDIENT_DELETE), any());
  }

  @Test
  public void deleteIngredientById_error() {
    int ingredientId = 1;

    when(postgresDataAccess.upsertStatement(eq(SQL_INGREDIENT_DELETE), any())).thenReturn(2);

    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              ingredientDataAccess.deleteIngredientById(ingredientId);
            });

    assertEquals("Multiple rows affected during delete", exception.getMessage());
    verify(postgresDataAccess, times(1)).upsertStatement(eq(SQL_INGREDIENT_DELETE), any());
  }

  @Test
  public void getIngredientById_returnNull() {
    int ingredientId = 1;
    List<Map<String, Object>> entityReturned = null;

    when(postgresDataAccess.queryStatement(eq(SQL_INGREDIENT_GET_BY_ID), any()))
        .thenReturn(entityReturned);

    UserIngredient result = ingredientDataAccess.getIngredientById(ingredientId);

    assertNull(result);
    verify(postgresDataAccess, times(1)).queryStatement(eq(SQL_INGREDIENT_GET_BY_ID), any());
  }

  @Test
  public void getIngredientById_returnValidItem() {
    int ingredientId = 1;
    List<Map<String, Object>> entityReturned =
        new ArrayList<>() {
          {
            add(Map.of("id", 1, "name", "ingredient1", "quantity", 1.0, "uom", "unit1"));
          }
        };
    when(postgresDataAccess.queryStatement(eq(SQL_INGREDIENT_GET_BY_ID), any()))
        .thenReturn(entityReturned);

    UserIngredient result = ingredientDataAccess.getIngredientById(ingredientId);

    assertEquals(result.getId(), 1);
    verify(postgresDataAccess, times(1)).queryStatement(eq(SQL_INGREDIENT_GET_BY_ID), any());
  }

  @Test
  public void getIngredientById_returnMultipleItem() {
    int ingredientId = 1;
    List<Map<String, Object>> entityReturned =
        new ArrayList<>() {
          {
            add(Map.of("id", 1, "name", "ingredient1", "quantity", 1.0, "uom", "unit1"));
            add(Map.of("id", 2, "name", "ingredient2", "quantity", 2.0, "uom", "unit2"));
          }
        };
    when(postgresDataAccess.queryStatement(eq(SQL_INGREDIENT_GET_BY_ID), any()))
        .thenReturn(entityReturned);

    UserIngredient result = ingredientDataAccess.getIngredientById(ingredientId);

    assertNull(result);
    verify(postgresDataAccess, times(1)).queryStatement(eq(SQL_INGREDIENT_GET_BY_ID), any());
  }

  @Test
  public void getIngredientsByName_returnNull() {
    String ingredientName = "ingredientName";
    List<Map<String, Object>> entityReturned = null;
    when(postgresDataAccess.queryStatement(eq(SQL_INGREDIENTS_GET_BY_NAME), any()))
        .thenReturn(entityReturned);

    List<UserIngredient> result = ingredientDataAccess.getIngredientsByName(ingredientName);

    assertEquals(result.size(), 0);
    verify(postgresDataAccess, times(1)).queryStatement(eq(SQL_INGREDIENTS_GET_BY_NAME), any());
  }

  @Test
  public void getIngredientsByName_returnMultiple() {
    String ingredientName = "ingredientName";

    List<Map<String, Object>> entityReturned =
        new ArrayList<>() {
          {
            add(Map.of("id", 1, "name", "ingredient1", "quantity", 1.0, "uom", "unit1"));
            add(Map.of("id", 2, "name", "ingredient2", "quantity", 2.0, "uom", "unit2"));
          }
        };

    when(postgresDataAccess.queryStatement(eq(SQL_INGREDIENTS_GET_BY_NAME), any()))
        .thenReturn(entityReturned);

    List<UserIngredient> result = ingredientDataAccess.getIngredientsByName(ingredientName);

    assertEquals(result.get(0).getId(), 1);
    assertEquals(result.get(0).getName(), "ingredient1");
    assertEquals(result.get(0).getQuantity(), 1.0);
    assertEquals(result.get(0).getUom(), "unit1");
    assertEquals(result.get(1).getId(), 2);
    assertEquals(result.get(1).getName(), "ingredient2");
    assertEquals(result.get(1).getQuantity(), 2.0);
    assertEquals(result.get(1).getUom(), "unit2");

    verify(postgresDataAccess, times(1)).queryStatement(eq(SQL_INGREDIENTS_GET_BY_NAME), any());
  }

  @Test
  public void getIngredientsByUser_returnNull() {
    int userId = 1;
    List<Map<String, Object>> entityReturned = null;

    when(postgresDataAccess.queryStatement(eq(SQL_INGREDIENTS_GET_BY_USER_ID), any()))
        .thenReturn(entityReturned);

    List<UserIngredient> result = ingredientDataAccess.getIngredientsByUser(userId);

    assertEquals(result.size(), 0);
    verify(postgresDataAccess, times(1)).queryStatement(eq(SQL_INGREDIENTS_GET_BY_USER_ID), any());
  }

  @Test
  public void getIngredientsByUser_returnMultiple() {
    int userId = 1;
    List<Map<String, Object>> entityReturned = new ArrayList<>();
    {
      entityReturned.add(
          Map.of(
              "id",
              1,
              "name",
              "ingredient1",
              "quantity",
              1.0,
              "uom",
              "unit1",
              "user_id",
              1,
              "expiry_date",
              new Date(2024, 12, 31),
              "create_datetime",
              new Timestamp(2024, 12, 30, 1, 1, 1, 1),
              "update_datetime",
              new Timestamp(2024, 12, 29, 1, 1, 1, 1)));

      entityReturned.add(Map.of("id", 2, "name", "ingredient2", "quantity", 2.0, "uom", "unit2"));
    }

    when(postgresDataAccess.queryStatement(eq(SQL_INGREDIENTS_GET_BY_USER_ID), any()))
        .thenReturn(entityReturned);

    List<UserIngredient> result = ingredientDataAccess.getIngredientsByUser(userId);

    assertEquals(result.size(), 2);
    assertEquals(result.get(0).getId(), 1);
    assertEquals(result.get(0).getName(), "ingredient1");
    assertEquals(result.get(0).getQuantity(), 1.0);
    assertEquals(result.get(0).getUom(), "unit1");
    assertEquals(result.get(1).getId(), 2);
    assertEquals(result.get(1).getName(), "ingredient2");
    assertEquals(result.get(1).getQuantity(), 2.0);
    assertEquals(result.get(1).getUom(), "unit2");
    verify(postgresDataAccess, times(1)).queryStatement(eq(SQL_INGREDIENTS_GET_BY_USER_ID), any());
  }

  @Test
  public void getIngredientsByUser_returnMultiple_TestValue() {
    int userId = 1;
    List<Map<String, Object>> entityReturned = new ArrayList<>();
    {
      entityReturned.add(
          Map.of(
              "id",
              "1",
              "name",
              1,
              "quantity",
              "1.0",
              "uom",
              1,
              "user_id",
              "1",
              "expiry_date",
              1,
              "create_datetime",
              2,
              "update_datetime",
              3));

      entityReturned.add(
          Map.of(
              "id2",
              "1",
              "name2",
              1,
              "quantity2",
              "1.0",
              "uom2",
              1,
              "user_id2",
              "1",
              "expiry_date2",
              new Date(2024, 12, 31),
              "create_datetime2",
              new Timestamp(2024, 12, 30, 1, 1, 1, 1),
              "update_datetime2",
              new Timestamp(2024, 12, 29, 1, 1, 1, 1)));
    }

    when(postgresDataAccess.queryStatement(eq(SQL_INGREDIENTS_GET_BY_USER_ID), any()))
        .thenReturn(entityReturned);

    List<UserIngredient> result = ingredientDataAccess.getIngredientsByUser(userId);

    assertEquals(result.size(), 2);
    verify(postgresDataAccess, times(1)).queryStatement(eq(SQL_INGREDIENTS_GET_BY_USER_ID), any());
  }

  @Test
  public void deleteIngredientsByUser_return0() {
    int userId = 1;
    int returnValue = 0;

    when(postgresDataAccess.upsertStatement(eq(SQL_INGREDIENTS_DELETE_BY_USER_ID), any()))
        .thenReturn(returnValue);

    boolean result = ingredientDataAccess.deleteIngredientsByUser(userId);

    assertFalse(result);
    verify(postgresDataAccess, times(1))
        .upsertStatement(eq(SQL_INGREDIENTS_DELETE_BY_USER_ID), any());
  }

  @Test
  public void deleteIngredientsByUser_return1() {
    int userId = 1;
    int returnValue = 1;

    when(postgresDataAccess.upsertStatement(eq(SQL_INGREDIENTS_DELETE_BY_USER_ID), any()))
        .thenReturn(returnValue);

    boolean result = ingredientDataAccess.deleteIngredientsByUser(userId);

    assertTrue(result);
    verify(postgresDataAccess, times(1))
        .upsertStatement(eq(SQL_INGREDIENTS_DELETE_BY_USER_ID), any());
  }

  @Test
  public void deleteIngredientsByUser_return2() {
    int userId = 1;
    int returnValue = 2;

    when(postgresDataAccess.upsertStatement(eq(SQL_INGREDIENTS_DELETE_BY_USER_ID), any()))
        .thenReturn(returnValue);

    boolean result = ingredientDataAccess.deleteIngredientsByUser(userId);

    assertTrue(result);
    verify(postgresDataAccess, times(1))
        .upsertStatement(eq(SQL_INGREDIENTS_DELETE_BY_USER_ID), any());
  }

  @Test
  public void getExpiringIngredientsInRange_null() {
    List<Map<String, Object>> returnValue = null;

    when(postgresDataAccess.queryStatement(eq(SQL_GET_EXPIRING_INGREDIENTS), any()))
        .thenReturn(returnValue);

    List<UserIngredient> result = ingredientDataAccess.getExpiringIngredientsInRange();

    assertEquals(result.size(), 0);
    verify(postgresDataAccess, times(1)).queryStatement(eq(SQL_GET_EXPIRING_INGREDIENTS), any());
  }

  @Test
  public void getExpiringIngredientsInRange_MulitpleValues() {
    List<Map<String, Object>> returnValue =
        new ArrayList<>() {
          {
            add(Map.of("id", 1, "name", "ingredient1", "quantity", 1.0, "uom", "unit1"));
            add(Map.of("id", 2, "name", "ingredient2", "quantity", 2.0, "uom", "unit2"));
          }
        };

    when(postgresDataAccess.queryStatement(eq(SQL_GET_EXPIRING_INGREDIENTS), any()))
        .thenReturn(returnValue);

    List<UserIngredient> result = ingredientDataAccess.getExpiringIngredientsInRange();

    assertEquals(result.size(), 2);
    verify(postgresDataAccess, times(1)).queryStatement(eq(SQL_GET_EXPIRING_INGREDIENTS), any());
  }
}
