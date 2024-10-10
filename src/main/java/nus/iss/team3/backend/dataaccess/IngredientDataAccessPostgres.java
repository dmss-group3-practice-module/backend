/* (C)2024 */
package nus.iss.team3.backend.dataaccess;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nus.iss.team3.backend.dataaccess.postgres.IPostgresDataAccess;
import nus.iss.team3.backend.entity.Ingredient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Repository class to connect to postgres for ingredient Data
 *
 * @author Liu Kun
 */
@Repository
public class IngredientDataAccessPostgres implements IIngredientDataAccess {

  private static final Logger logger = LogManager.getLogger(IngredientDataAccessPostgres.class);

  @Autowired IPostgresDataAccess postgresDataAccess;

  /**
   * @param ingredient ingredient to be added
   * @return whether adding the ingredient was successful or not
   */
  @Override
  public boolean addIngredient(Ingredient ingredient) {
    Map<String, Object> sqlInput = new HashMap<>();
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_NAME, ingredient.getName());
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ID, ingredient.getUserId());
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_UOM, ingredient.getUom());
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_QUANTITY, ingredient.getQuantity());
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_EXPIRY_DATE, ingredient.getExpiryDate());

    int result =
        postgresDataAccess.upsertStatement(PostgresSqlStatement.SQL_INGREDIENT_ADD, sqlInput);

    if (result == 1) {
      logger.info("ingredient created for {}", ingredient.getIngredientId());

      return true;
    }
    logger.info("ingredient creation for {} failed", ingredient.getIngredientId());
    return false;
  }

  /**
   * @param ingredient ingredient to be updated
   * @return whether updating the ingredient was successful or not
   */
  @Override
  public boolean updateIngredient(Ingredient ingredient) {
    Map<String, Object> sqlInput = new HashMap<>();
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_ID, ingredient.getIngredientId());
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_NAME, ingredient.getName());
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ID, ingredient.getUserId());
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_UOM, ingredient.getUom());
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_QUANTITY, ingredient.getQuantity());
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_EXPIRY_DATE, ingredient.getExpiryDate());
    int result =
        postgresDataAccess.upsertStatement(PostgresSqlStatement.SQL_INGREDIENT_UPDATE, sqlInput);
    if (result == 1) {
      logger.info("ingredient updated for {}", ingredient.getIngredientId());
      return true;
    } else if (result == 0) {
      logger.debug("account update for {} failed, no account found", ingredient.getIngredientId());
      return false;
    } else {
      logger.error(
          "account update for {} affected multiple rows: {}", ingredient.getIngredientId(), result);
      throw new RuntimeException("Multiple rows affected during update");
    }
  }

  /**
   * @param ingredientId id of ingredient to be deleted
   * @return whether deleting the ingredient was successful or not
   */
  @Override
  public boolean deleteIngredientById(int id) {
    Map<String, Object> sqlInput = new HashMap<>();
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_ID, id);

    int result =
        postgresDataAccess.upsertStatement(PostgresSqlStatement.SQL_INGREDIENT_DELETE, sqlInput);
    if (result == 1) {
      logger.info("ingredient deleted for {}", id);
      return true;
    } else if (result == 0) {
      logger.debug("account delete for {} failed, no account found", id);
      return false;
    } else {
      logger.error("account delete for {} affected multiple rows: {}", id, result);
      throw new RuntimeException("Multiple rows affected during delete");
    }
  }

  /**
   * @param ingredientId id of ingredient to be retrieved
   * @return the content of the ingredient that was retrieved
   */
  @Override
  public Ingredient getIngredientById(int id) {
    Map<String, Object> sqlInput = new HashMap<>();
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_ID, id);

    List<Map<String, Object>> entityReturned =
        postgresDataAccess.queryStatement(PostgresSqlStatement.SQL_INGREDIENT_GET_BY_ID, sqlInput);

    // entity may be null, so I think here should add this condition
    if (entityReturned == null) {
      logger.error("no entity found for {}, please review", id);
      return new Ingredient();
    }

    if (entityReturned.size() == 1) {
      return translateDBRecordToIngredient(entityReturned.getFirst());
    } else {
      logger.error("record abnormal as {} for {}, please review", entityReturned.size(), id);
    }

    return null;
  }

  /**
   * @param userId id of user to be retrieved
   * @return the content of the ingredient that was retrieved
   */
  @Override
  public List<Ingredient> getIngredientsByUser(int userId) {
    List<Map<String, Object>> entityReturned =
        postgresDataAccess.queryStatement(PostgresSqlStatement.SQL_INGREDIENTS_GET_BY_USER, null);

    if (entityReturned == null) {
      logger.error("No ingredients found for user {}", userId);
      return new ArrayList<>();
    }
    List<Ingredient> returnList = new ArrayList<>();
    for (Map<String, Object> entity : entityReturned) {
      returnList.add(translateDBRecordToIngredient(entity));
    }
    return returnList;
  }

  @Override
  public boolean deleteIngredientsByUser(int userId) {
    Map<String, Object> sqlInput = new HashMap<>();
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ID, userId);

    int result =
        postgresDataAccess.upsertStatement(
            PostgresSqlStatement.SQL_INGREDIENTS_DELETE_BY_USER, sqlInput);

    if (result == 0) {
      logger.info("ingredient deletion for {} failed, no ingredient found", userId);
    }

    if (result > 0) {
      logger.info("{} ingredient deleted for {}", result, userId);
      return true;
    }

    return false;
  }

  // private functions
  private Ingredient translateDBRecordToIngredient(Map<String, Object> entity) {
    Ingredient returnItem = new Ingredient();

    if (entity.containsKey(PostgresSqlStatement.COLUMN_INGREDIENT_ID)
        && (entity.get(PostgresSqlStatement.COLUMN_INGREDIENT_ID) instanceof Integer)) {
      returnItem.setIngredientId((Integer) entity.get(PostgresSqlStatement.COLUMN_INGREDIENT_ID));
    }

    if (entity.containsKey(PostgresSqlStatement.COLUMN_INGREDIENT_NAME)
        && (entity.get(PostgresSqlStatement.COLUMN_INGREDIENT_NAME) instanceof String)) {
      returnItem.setName((String) entity.get(PostgresSqlStatement.COLUMN_INGREDIENT_NAME));
    }

    // userId can I set？
    // if (entity.containsKey(PostgresSqlStatement.COLUMN_USER_ID)
    // && (entity.get(PostgresSqlStatement.COLUMN_USER_ID) instanceof Integer)) {
    // returnItem.setUserId((Integer)
    // entity.get(PostgresSqlStatement.COLUMN_USER_ID));
    // }

    if (entity.containsKey(PostgresSqlStatement.COLUMN_INGREDIENT_QUANTITY)
        && (entity.get(PostgresSqlStatement.COLUMN_INGREDIENT_QUANTITY) instanceof Double)) {
      returnItem.setQuantity((Double) entity.get(PostgresSqlStatement.COLUMN_INGREDIENT_QUANTITY));
    }

    if (entity.containsKey(PostgresSqlStatement.COLUMN_INGREDIENT_UOM)
        && (entity.get(PostgresSqlStatement.COLUMN_INGREDIENT_UOM) instanceof String)) {
      returnItem.setUom((String) entity.get(PostgresSqlStatement.COLUMN_INGREDIENT_UOM));
    }

    if (entity.containsKey(PostgresSqlStatement.COLUMN_INGREDIENT_EXPIRY_DATE)
        && (entity.get(PostgresSqlStatement.COLUMN_INGREDIENT_EXPIRY_DATE) instanceof Date)) {
      returnItem.setExpiryDate(
          (Date) entity.get(PostgresSqlStatement.COLUMN_INGREDIENT_EXPIRY_DATE));
    }
    return returnItem;
  }
}
