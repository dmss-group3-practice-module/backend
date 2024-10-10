/* (C)2024 */
package nus.iss.team3.backend.service;

import java.time.ZonedDateTime;
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

  @Autowired IIngredientDataAccess ingredientDataAccess;

  @Override
  public boolean addIngredient(Ingredient ingredient) {
    if (!validateIngredient(ingredient)) {
      logger.info(
          "addIngredient failed, due to validation failed for ingredient {}",
          (ingredient == null ? "null object" : ingredient.getName()));
      return false;
    }

    if (ingredientDataAccess.getIngredientById(ingredient.getIngredientId()) != null) {
      logger.info(
          "addIngredient failed, due to existing ingredient for Id {}",
          ingredient.getIngredientId());
      return false;
    }

    ingredient.setCreateDateTime(ZonedDateTime.now());
    ingredient.setUpdateDateTime(ZonedDateTime.now());

    return ingredientDataAccess.addIngredient(ingredient);
  }

  @Override
  public boolean updateIngredient(Ingredient ingredient) {
    // logger.info("iid is {}", ingredient.getIngredientId());
    if (!validateIngredient(ingredient)) {
      logger.info(
          "updateIngredient failed, due to validation failed for ingredient {}",
          (ingredient == null ? "null object" : ingredient.getName()));
      return false;
    }

    if (ingredient.getIngredientId() < 0) {
      logger.info(
          "updateUser failed, due to missing Id account for {}", ingredient.getIngredientId());
      return false;
    }

    Ingredient existingIngredient =
        ingredientDataAccess.getIngredientById(ingredient.getIngredientId());
    if (existingIngredient == null) {
      logger.info("updateUser failed, due to missing account for {}", ingredient.getIngredientId());
      return false;
    }

    ingredient.setUpdateDateTime(ZonedDateTime.now());
    return ingredientDataAccess.updateIngredient(ingredient);
  }

  @Override
  public boolean deleteIngredientById(Integer id) {
    if (ingredientDataAccess.getIngredientById(id) == null) {
      logger.info("deleteUser failed, due to missing account for {}", id);
      return false;
    }
    return ingredientDataAccess.deleteIngredientById(id);
  }

  @Override
  public Ingredient getIngredientById(Integer id) {
    logger.info("looking for {}", id);
    return ingredientDataAccess.getIngredientById(id);
  }

  @Override
  public List<Ingredient> getIngredientsByUser(Integer userId) {
    return ingredientDataAccess.getIngredientsByUser(userId);
  }

  @Override
  public boolean deleteIngredientsByUser(Integer userId) {
    return ingredientDataAccess.deleteIngredientsByUser(userId);
  }

  /**
   * Check whether the input Ingredient contains acceptable values
   *
   * @return whether boolean on whether ingredient is valid
   */
  private boolean validateIngredient(Ingredient ingredient) {
    return ingredient != null
        && ingredient.getIngredientId() >= 0
        && !StringUtilities.isStringNullOrBlank(ingredient.getName())
        && !StringUtilities.isStringNullOrBlank(ingredient.getUom())
        && !Double.isNaN(ingredient.getQuantity())
        && ingredient.getExpiryDate() != null;
  }
}
