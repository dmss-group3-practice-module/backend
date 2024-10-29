package nus.iss.team3.backend.dataaccess;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nus.iss.team3.backend.dataaccess.postgres.IPostgresDataAccess;
import nus.iss.team3.backend.entity.UserIngredient;
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
public class IngredientDataAccess implements IIngredientDataAccess {

  private static final Logger logger = LogManager.getLogger(IngredientDataAccess.class);

  @Autowired IPostgresDataAccess postgresDataAccess;

  /**
   * @param ingredient ingredient to be added
   * @return boolean of whether adding ingredient was successful
   */
  @Override
  public boolean addIngredient(UserIngredient ingredient) {
    Map<String, Object> sqlInput = new HashMap<>();
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_NAME, ingredient.getName());
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ID, ingredient.getUserId());
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_UOM, ingredient.getUom());
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_QUANTITY, ingredient.getQuantity());
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_EXPIRY_DATE, ingredient.getExpiryDate());

    int result =
        postgresDataAccess.upsertStatement(PostgresSqlStatement.SQL_INGREDIENT_ADD, sqlInput);

    if (result == 1) {
      logger.info("ingredient created for {}", ingredient.getId());

      return true;
    }
    logger.info("ingredient creation for {} failed", ingredient.getId());
    return false;
  }

  /**
   * @param ingredient ingredient to be updated
   * @return boolean of whether updating ingredient was successful
   */
  @Override
  public boolean updateIngredient(UserIngredient ingredient) {
    Map<String, Object> sqlInput = new HashMap<>();
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_ID, ingredient.getId());
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_NAME, ingredient.getName());
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ID, ingredient.getUserId());
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_UOM, ingredient.getUom());
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_QUANTITY, ingredient.getQuantity());
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_EXPIRY_DATE, ingredient.getExpiryDate());
    int result =
        postgresDataAccess.upsertStatement(PostgresSqlStatement.SQL_INGREDIENT_UPDATE, sqlInput);
    if (result == 1) {
      logger.info("ingredient updated for {}", ingredient.getId());
      return true;
    } else if (result == 0) {
      logger.debug("ingredient update for {} failed, no ingredient found", ingredient.getId());
      return false;
    } else {
      logger.error(
          "ingredient update for {} affected multiple rows: {}", ingredient.getId(), result);
      throw new RuntimeException("Multiple rows affected during update");
    }
  }

  /**
   * @param id of ingredient to be deleted
   * @return boolean of whether deleting ingredient was successful
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
   * @param id of ingredient to be retrieved
   * @return content of the retrieved ingredient
   */
  @Override
  public UserIngredient getIngredientById(int id) {
    Map<String, Object> sqlInput = new HashMap<>();
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_ID, id);

    List<Map<String, Object>> entityReturned =
        postgresDataAccess.queryStatement(PostgresSqlStatement.SQL_INGREDIENT_GET_BY_ID, sqlInput);

    if (entityReturned == null) {
      logger.error("no entity found for id {}", id);
      return null;
    }

    if (entityReturned.size() == 1) {
      return translateDBRecordToIngredient(entityReturned.getFirst());
    }

    return null;
  }

  /**
   * @param name of the ingredient to be retrieved
   * @return list of ingredients matching the name
   */
  @Override
  public List<UserIngredient> getIngredientsByName(String name) {
    Map<String, Object> sqlInput = new HashMap<>();
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_NAME, name);
    List<Map<String, Object>> entityReturned =
        postgresDataAccess.queryStatement(
            PostgresSqlStatement.SQL_INGREDIENTS_GET_BY_NAME, sqlInput);
    List<UserIngredient> returnList = new ArrayList<>();
    if (entityReturned != null) {
      for (Map<String, Object> entity : entityReturned) {
        returnList.add(translateDBRecordToIngredient(entity));
      }
    } else {
      logger.error("No ingredients found for name {}", name);
    }
    return returnList;
  }

  /**
   * @param userId id of user to be retrieved
   * @return content of retrieved ingredient based on userId
   */
  @Override
  public List<UserIngredient> getIngredientsByUser(int userId) {
    Map<String, Object> sqlInput = new HashMap<>();
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_USER_ACCOUNT_ID, userId);

    List<Map<String, Object>> entityReturned =
        postgresDataAccess.queryStatement(
            PostgresSqlStatement.SQL_INGREDIENTS_GET_BY_USER_ID, sqlInput);
    if (entityReturned == null) {
      logger.error("No ingredients found for user {}", userId);
      return new ArrayList<>();
    }
    List<UserIngredient> returnList = new ArrayList<>();
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
            PostgresSqlStatement.SQL_INGREDIENTS_DELETE_BY_USER_ID, sqlInput);

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
  private UserIngredient translateDBRecordToIngredient(Map<String, Object> entity) {
    UserIngredient returnItem = new UserIngredient();

    if (entity.containsKey(PostgresSqlStatement.COLUMN_INGREDIENT_ID)
        && (entity.get(PostgresSqlStatement.COLUMN_INGREDIENT_ID) instanceof Integer)) {
      returnItem.setId((Integer) entity.get(PostgresSqlStatement.COLUMN_INGREDIENT_ID));
    }

    if (entity.containsKey(PostgresSqlStatement.COLUMN_USER_ID)
        && (entity.get(PostgresSqlStatement.COLUMN_USER_ID) instanceof Integer)) {
      returnItem.setUserId((Integer) entity.get(PostgresSqlStatement.COLUMN_USER_ID));
    }

    if (entity.containsKey(PostgresSqlStatement.COLUMN_INGREDIENT_NAME)
        && (entity.get(PostgresSqlStatement.COLUMN_INGREDIENT_NAME) instanceof String)) {
      returnItem.setName((String) entity.get(PostgresSqlStatement.COLUMN_INGREDIENT_NAME));
    }

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

    if (entity.containsKey(PostgresSqlStatement.COLUMN_INGREDIENT_CREATE_DATETIME)
        && (entity.get(PostgresSqlStatement.COLUMN_INGREDIENT_CREATE_DATETIME)
            instanceof java.sql.Timestamp timestamp)) {
      returnItem.setCreateDateTime(timestamp.toInstant().atZone(ZoneId.systemDefault()));
    }

    if (entity.containsKey(PostgresSqlStatement.COLUMN_INGREDIENT_UPDATE_DATETIME)
        && (entity.get(PostgresSqlStatement.COLUMN_INGREDIENT_UPDATE_DATETIME)
            instanceof java.sql.Timestamp timestamp)) {
      returnItem.setUpdateDateTime(timestamp.toInstant().atZone(ZoneId.systemDefault()));
    }
    return returnItem;
  }
}
