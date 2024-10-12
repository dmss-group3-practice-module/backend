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
    validateIngredient(ingredient, false);

    ZonedDateTime now = ZonedDateTime.now();
    ingredient.setCreateDateTime(now);
    ingredient.setUpdateDateTime(now);

    return ingredientDataAccess.addIngredient(ingredient);
  }

  @Override
  public boolean updateIngredient(Ingredient ingredient) {
    validateIngredient(ingredient, true);
    System.out.println("ingredient: " + ingredient);

    Ingredient existingIngredient = ingredientDataAccess.getIngredientById(ingredient.getId());
    System.out.println("existingIngredient: " + existingIngredient);
    if (existingIngredient == null) {
      throw new IllegalArgumentException("Missing ingredient for updateUser {}");
    }

    ingredient.setUpdateDateTime(ZonedDateTime.now());
    return ingredientDataAccess.updateIngredient(ingredient);
  }

  @Override
  public boolean deleteIngredientById(Integer id) {
    if (ingredientDataAccess.getIngredientById(id) == null) {
      logger.info("deleteIngredient failed, due to missing ingredient for {}", id);
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
  private void validateIngredient(Ingredient ingredient, boolean isUpdate) {
    if (ingredient == null) {
      throw new IllegalArgumentException("Ingredient cannot be null");
    }
    if (ingredient.getUserId() <= 0) {
      throw new IllegalArgumentException("Ingredient userId must be valid");
    }
    if (isUpdate && ingredient.getId() <= 0) {
      throw new IllegalArgumentException("Ingredient id must be valid");
    }
    if (StringUtilities.isStringNullOrBlank(ingredient.getName())) {
      throw new IllegalArgumentException("Ingredient name cannot be empty or blank");
    }
    if (StringUtilities.isStringNullOrBlank(ingredient.getUom())) {
      throw new IllegalArgumentException("Ingredient uom cannot be empty or blank");
    }
    if (ingredient.getQuantity() <= 0) {
      throw new IllegalArgumentException("Ingredient quantity must be greater than 0");
    }
    if (ingredient.getExpiryDate() == null) {
      throw new IllegalArgumentException("Ingredient expiry date cannot be null");
    }
  }
}
