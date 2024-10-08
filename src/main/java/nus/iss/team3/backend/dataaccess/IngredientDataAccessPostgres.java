/* (C)2024 */
package nus.iss.team3.backend.dataaccess;

import java.util.ArrayList;
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
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_ID, ingredient.getIngredientId());
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_NAME, ingredient.getName());
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ID, ingredient.getUserId());
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_UOM, ingredient.getUom());
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_QUANTITY, ingredient.getQuantity());
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_EXPIRY_DATE, ingredient.getExpiryDate());
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_CREATE_TIME, ingredient.getCreateTime());
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_UPDATE_TIME, ingredient.getUpdateTime());

    int result =
        postgresDataAccess.upsertStatement(PostgresSqlStatement.SQL_INGREDIENT_ADD, sqlInput);

    // logger.info("sql is {}, rowAdded is {}", sqlInput, PostgresSqlStatement.SQL_INGREDIENT_ADD);

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
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_CREATE_TIME, ingredient.getCreateTime());
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_UPDATE_TIME, ingredient.getUpdateTime());
    int rowUpdated =
        postgresDataAccess.upsertStatement(PostgresSqlStatement.SQL_INGREDIENT_UPDATE, sqlInput);
    if (rowUpdated == 1) {
      logger.info("ingredient updated for {}", ingredient.getIngredientId());
      return true;
    }
    if (rowUpdated == 0) {
      logger.info(
          "ingredient update for {} failed, no ingredient found", ingredient.getIngredientId());
      return false;
    }
    if (rowUpdated > 1) {
      logger.error(
          "ingredient update for {} happened but multiple rows updated, please review",
          ingredient.getIngredientId());
    }

    return false;
  }

  /**
   * @param ingredientId id of ingredient to be deleted
   * @return whether deleting the ingredient was successful or not
   */
  @Override
  public boolean deleteIngredientById(int id) {
    Map<String, Object> sqlInput = new HashMap<>();
    sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_ID, id);

    int rowDeleted =
        postgresDataAccess.upsertStatement(PostgresSqlStatement.SQL_INGREDIENT_DELETE, sqlInput);
    if (rowDeleted == 1) {
      logger.info("ingredient deleted for {}", id);
      return true;
    }

    if (rowDeleted == 0) {
      logger.info("ingredient deletion for {} failed, no ingredient found", id);
      return false;
    }

    if (rowDeleted > 1) {
      logger.error(
          "ingredient deletion for {} happened but multiple rows updated, please review", id);
    }

    logger.info("ingredient deletion for {} failed", id);
    return false;
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
    if (entityReturned == null) {
      logger.error("no entity found for {}, please review", id);
    } else {
      if (entityReturned.size() == 1) {
        return translateDBRecordToIngredient(entityReturned.getFirst());
      }

      if (entityReturned.size() > 1) {
        logger.error("Multiple record found for {}, please review", id);
      }
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
      logger.info("No ingredients found for user {}", userId);
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

    int rowDeleted =
        postgresDataAccess.upsertStatement(
            PostgresSqlStatement.SQL_INGREDIENTS_DELETE_BY_USER, sqlInput);

    if (rowDeleted == 0) {
      logger.info("ingredient deletion for {} failed, no ingredient found", userId);
    }

    if (rowDeleted > 0) {
      logger.info("ingredient deleted for {}", userId);
      return true;
    }

    return false;
  }

  private Ingredient translateDBRecordToIngredient(Map<String, Object> entity) {
    Ingredient returnItem = new Ingredient();

    // if (entity.containsKey(PostgresSqlStatement.COLUMN_INGREDIENT_ID)
    // && (entity.get(PostgresSqlStatement.COLUMN_INGREDIENT_ID) instanceof String))
    // {
    // returnItem.setIngredientId((String)
    // entity.get(PostgresSqlStatement.COLUMN_INGREDIENT_ID));
    // }
    if (entity.containsKey(PostgresSqlStatement.COLUMN_INGREDIENT_NAME)
        && (entity.get(PostgresSqlStatement.COLUMN_INGREDIENT_NAME) instanceof String)) {
      returnItem.setName((String) entity.get(PostgresSqlStatement.COLUMN_INGREDIENT_NAME));
    }
    if (entity.containsKey(PostgresSqlStatement.COLUMN_INGREDIENT_QUANTITY)
        && (entity.get(PostgresSqlStatement.COLUMN_INGREDIENT_QUANTITY) instanceof Double)) {
      returnItem.setQuantity((Double) entity.get(PostgresSqlStatement.COLUMN_INGREDIENT_QUANTITY));
    }

    return returnItem;
  }
}
