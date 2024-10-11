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

    if (ingredientDataAccess.getIngredientById(ingredient.getId()) != null) {
      logger.info("addIngredient failed, due to existing ingredient for Id {}", ingredient.getId());
      return false;
    }

    ingredient.setCreateDateTime(ZonedDateTime.now());
    ingredient.setUpdateDateTime(ZonedDateTime.now());

    return ingredientDataAccess.addIngredient(ingredient);
  }

  @Override
  public boolean updateIngredient(Ingredient ingredient) {
    if (!validateIngredient(ingredient)) {
      logger.info("validation for updateIngredient failed for ingredient {}", ingredient.getName());
      return false;
    }

    if (ingredient.getId() < 0) {
      logger.info("missing id for updateUser for ingredientId {}", ingredient.getId());
      return false;
    }

    Ingredient existingIngredient = ingredientDataAccess.getIngredientById(ingredient.getId());
    if (existingIngredient == null) {
      logger.info("missing ingredient for updateUser for ingredientId {}", ingredient.getId());
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
        && ingredient.getId() >= 0
        && !StringUtilities.isStringNullOrBlank(ingredient.getName())
        && !StringUtilities.isStringNullOrBlank(ingredient.getUom())
        && !Double.isNaN(ingredient.getQuantity())
        && ingredient.getExpiryDate() != null;
  }
}
