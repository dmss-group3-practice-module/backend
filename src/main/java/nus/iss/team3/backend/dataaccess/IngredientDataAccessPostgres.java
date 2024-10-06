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

    @Autowired
    IPostgresDataAccess postgresDataAccess;

    /**
     * @param ingredient ingredient to be added
     * @return whether adding the ingredient was successful or not
     */

    @Override
    public boolean addIngredient(Ingredient ingredient) {
        Map<String, Object> sqlInput = new HashMap<>();
        sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_ID, ingredient.getIngredientId());
        sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_NAME, ingredient.getIngredientName());
        // sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_OWNER,
        // ingredient.getUserId());
        sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_QUANTITY, ingredient.getIngredientQuantity());
        sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_STATUS, ingredient.getIngredientStatus());
        sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_EXPIRY_DATE, ingredient.getIngredientExpiryDate());

        int rowAdded = postgresDataAccess.upsertStatement(PostgresSqlStatement.SQL_INGREDIENT_ADD, sqlInput);
        if (rowAdded == 1) {
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
        sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_NAME, ingredient.getIngredientName());
        sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_QUANTITY, ingredient.getIngredientQuantity());
        sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_STATUS, ingredient.getIngredientStatus());
        sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_EXPIRY_DATE, ingredient.getIngredientExpiryDate());

        int rowUpdated = postgresDataAccess.upsertStatement(PostgresSqlStatement.SQL_INGREDIENT_UPDATE, sqlInput);
        if (rowUpdated == 1) {
            logger.info("ingredient updated for {}", ingredient.getIngredientId());
            return true;
        }
        if (rowUpdated == 0) {
            logger.info("ingredient update for {} failed, no ingredient found", ingredient.getIngredientId());
            return false;
        }
        if (rowUpdated > 1) {
            logger.error("ingredient update for {} happened but multiple rows updated, please review",
                    ingredient.getIngredientId());
        }

        return false;
    }

    /**
     * @param ingredientId id of ingredient to be deleted
     * @return whether deleting the ingredient was successful or not
     */
    @Override
    public boolean deleteIngredientById(String ingredientId) {
        Map<String, Object> sqlInput = new HashMap<>();
        sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_ID, ingredientId);

        int rowDeleted = postgresDataAccess.upsertStatement(PostgresSqlStatement.SQL_INGREDIENT_DELETE, sqlInput);
        if (rowDeleted == 1) {
            logger.info("ingredient deleted for {}", ingredientId);
            return true;
        }

        if (rowDeleted == 0) {
            logger.info("ingredient deletion for {} failed, no ingredient found", ingredientId);
            return false;
        }

        if (rowDeleted > 1) {
            logger.error("ingredient deletion for {} happened but multiple rows updated, please review", ingredientId);
        }

        logger.info("ingredient deletion for {} failed", ingredientId);
        return false;
    }

    /**
     * @param ingredientId id of ingredient to be retrieved
     * @return the content of the ingredient that was retrieved
     */
    @Override
    public Ingredient getIngredientById(String ingredientId) {
        Map<String, Object> sqlInput = new HashMap<>();
        sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_ID, ingredientId);

        List<Map<String, Object>> entityReturned = postgresDataAccess
                .queryStatement(PostgresSqlStatement.SQL_INGREDIENT_GET_BY_ID, sqlInput);

        if (entityReturned.size() == 1) {
            return translateDBRecordToIngredient(entityReturned.getFirst());
        }

        if (entityReturned.size() > 1) {
            logger.error("Multiple record found for {}, please review", ingredientId);
        }

        return null;
    }

    /**
     * @param userId id of user to be retrieved
     * @return the content of the ingredient that was retrieved
     */
    @Override
    public List<Ingredient> getIngredientsByUser(String userId) {
        List<Map<String, Object>> entityReturned = postgresDataAccess
                .queryStatement(PostgresSqlStatement.SQL_INGREDIENTS_GET_BY_USER, null);

        if (entityReturned == null) {
            logger.info("No ingredients found for user {}", userId);
        }
        List<Ingredient> returnList = new ArrayList<>();
        for (Map<String, Object> entity : entityReturned) {
            returnList.add(translateDBRecordToIngredient(entity));
        }
        return returnList;
    }

    @Override
    public boolean deleteIngredientsByUser(String userId) {
        Map<String, Object> sqlInput = new HashMap<>();
        sqlInput.put(PostgresSqlStatement.INPUT_INGREDIENT_OWNER, userId);

        int rowDeleted = postgresDataAccess.upsertStatement(PostgresSqlStatement.SQL_INGREDIENTS_DELETE_BY_USER,
                sqlInput);

        if (rowDeleted == 0) {
            logger.info("ingredient deletion for {} failed, no ingredient found", userId);
        }

        if (rowDeleted > 0) {
            logger.info("ingredient deleted for {}", userId);
            return true;
        }

        return false;
    }

    /**
     * @return the whole list of ingredient in the systems
     */
    @Override
    public List<Ingredient> getAllIngredients() {
        List<Map<String, Object>> entityReturned = postgresDataAccess
                .queryStatement(PostgresSqlStatement.SQL_INGREDIENT_GET_ALL, null);

        if (entityReturned == null) {
            logger.error("Error retriving ingredient from database.");
        }
        List<Ingredient> returnList = new ArrayList<>();
        for (Map<String, Object> entity : entityReturned) {
            returnList.add(translateDBRecordToIngredient(entity));
        }
        return returnList;
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
            returnItem.setIngredientName((String) entity.get(PostgresSqlStatement.COLUMN_INGREDIENT_NAME));
        }
        if (entity.containsKey(PostgresSqlStatement.COLUMN_INGREDIENT_QUANTITY)
                && (entity.get(PostgresSqlStatement.COLUMN_INGREDIENT_QUANTITY) instanceof Integer)) {
            returnItem.setIngredientQuantity((Integer) entity.get(PostgresSqlStatement.COLUMN_INGREDIENT_QUANTITY));
        }

        return returnItem;
    }
}
