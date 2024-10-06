/* (C)2024 */
package nus.iss.team3.backend.service;

import java.util.List;
import nus.iss.team3.backend.dataaccess.IIngredientDataAccess;
import nus.iss.team3.backend.entity.Ingredient;
import nus.iss.team3.backend.util.StringUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class with logic for handling ingredient related queries
 *
 * @author liukun
 */
@Service
public class IngredientService implements IIngredientService {

    private static final Logger logger = LogManager.getLogger(IngredientService.class);

    @Autowired
    IIngredientDataAccess ingredientDataAccess;

    @Override
    public boolean addIngredient(Ingredient ingredient) {
        if (validateIngredient(ingredient)) {
            logger.info("addIngredient failed, due to validation failed for ingredient {}",
                    (ingredient == null ? "null object" : ingredient.getIngredientName()));
            return false;
        }
        if (ingredientDataAccess.getIngredientById(ingredient.getIngredientId()) != null) {
            logger.info("addIngredient failed, due to existing ingredient for Id {}", ingredient.getIngredientId());
            return false;
        }
        return ingredientDataAccess.addIngredient(ingredient);

    }

    @Override
    public boolean updateIngredient(Ingredient ingredient) {
        if (validateIngredient(ingredient)) {
            logger.info(
                    "updateIngredient failed, due to validation failed for ingredient {}",
                    (ingredient == null ? "null object" : ingredient.getIngredientName()));
            return false;
        }
        if (StringUtilities.isStringNullOrBlank(ingredient.getIngredientId())) {
            logger.info("updateUser failed, due to missing Id account for {}", ingredient.getIngredientId());
            return false;
        }

        Ingredient otherIngredient = ingredientDataAccess.getIngredientById(ingredient.getIngredientId());

        if (otherIngredient == null) {
            logger.info("updateUser failed, due to missing account for {}", ingredient.getIngredientId());
            return false;
        }
        return ingredientDataAccess.updateIngredient(ingredient);
    }

    @Override
    public boolean deleteIngredientById(String ingredientId) {
        return ingredientDataAccess.deleteIngredientById(ingredientId);
    }

    @Override
    public Ingredient getIngredientById(String ingredientId) {
        logger.info("looking for {}", ingredientId);
        return ingredientDataAccess.getIngredientById(ingredientId);
    }

    @Override
    public List<Ingredient> getIngredientsByUser(String userId) {
        return ingredientDataAccess.getIngredientsByUser(userId);
    }

    @Override
    public boolean deleteIngredientsByUser(String userId) {
        return ingredientDataAccess.deleteIngredientsByUser(userId);
    }

    @Override
    public List<Ingredient> getAllIngredients() {
        return ingredientDataAccess.getAllIngredients();
    }

    /**
     * Check whether the input Ingredient contians value for it to be accepted
     *
     * @return whether the string is null or blank.
     */
    private boolean validateIngredient(Ingredient ingredient) {
        return ingredient == null
                || StringUtilities.isStringNullOrBlank(ingredient.getIngredientId())
                || StringUtilities.isStringNullOrBlank(ingredient.getIngredientName())
                || ingredient.getIngredientStatus() == null
                || ingredient.getIngredientExpiryDate() == null;
    }
}
