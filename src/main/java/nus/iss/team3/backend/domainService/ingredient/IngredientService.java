package nus.iss.team3.backend.domainService.ingredient;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import nus.iss.team3.backend.ProfileConfig;
import nus.iss.team3.backend.dataaccess.IIngredientDataAccess;
import nus.iss.team3.backend.entity.UserIngredient;
import nus.iss.team3.backend.service.util.StringUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Service class with logic for handling ingredient related queries
 *
 * @author liukun, Ren Jiarui
 */
@Service
@Profile(ProfileConfig.PROFILE_INGREDIENT)
public class IngredientService implements IIngredientService {

  private static final Logger logger = LogManager.getLogger(IngredientService.class);

  @Autowired IIngredientDataAccess ingredientDataAccess;

  @PostConstruct
  public void postConstruct() {
    logger.info("Ingredient Service Logic initialized.");
  }

  @Override
  public boolean addIngredient(UserIngredient ingredient) {
    validateIngredient(ingredient, false);

    ZonedDateTime now = ZonedDateTime.now();
    ingredient.setCreateDateTime(now);
    ingredient.setUpdateDateTime(now);

    return ingredientDataAccess.addIngredient(ingredient);
  }

  @Override
  public boolean updateIngredient(UserIngredient ingredient) {
    validateIngredient(ingredient, true);
    UserIngredient existingIngredient = ingredientDataAccess.getIngredientById(ingredient.getId());
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
  public UserIngredient getIngredientById(Integer id) {
    UserIngredient ingredient = ingredientDataAccess.getIngredientById(id);
    if (ingredient == null) {
      logger.warn("ingredient not found for ID: {}", id);
    }
    return ingredient;
  }

  @Override
  public List<UserIngredient> getIngredientsByName(String name) {
    List<UserIngredient> ingredients = ingredientDataAccess.getIngredientsByName(name);
    if (ingredients == null) {
      logger.warn("ingredient not found for Name: {}", name);
    }
    return ingredients;
  }

  @Override
  public List<UserIngredient> getIngredientsByUser(Integer userId) {
    return ingredientDataAccess.getIngredientsByUser(userId);
  }

  @Override
  public boolean deleteIngredientsByUser(Integer userId) {
    return ingredientDataAccess.deleteIngredientsByUser(userId);
  }

  @Override
  public List<UserIngredient> getExpiringIngredients(Integer userId, int days) {
    List<UserIngredient> userIngredients = getIngredientsByUser(userId);
    if (userIngredients == null) {
      logger.warn("No ingredients found for user ID: {}", userId);
      return new ArrayList<>();
    }
    LocalDate today = LocalDate.now();
    LocalDate futureDate = today.plusDays(days);

    return userIngredients.stream()
        .filter(
            ingredient -> {
              LocalDate expiryDate =
                  ingredient
                      .getExpiryDate()
                      .toInstant()
                      .atZone(ZoneId.systemDefault())
                      .toLocalDate();
              return !expiryDate.isBefore(today) && !expiryDate.isAfter(futureDate);
            })
        .sorted(Comparator.comparing(UserIngredient::getExpiryDate))
        .collect(Collectors.toList());
  }

  @Override
  public List<UserIngredient> getExpiringIngredientsInRange() {
    logger.info("Fetching ingredients expiring in next 3 days");
    try {
      // Delegate to data access layer to perform the optimized query
      List<UserIngredient> ingredients = ingredientDataAccess.getExpiringIngredientsInRange();

      if (ingredients == null) {
        logger.warn("No expiring ingredients found in range");
        return new ArrayList<>();
      }

      // Sort by user ID and expiry date for consistent results
      return ingredients.stream()
          .sorted(
              Comparator.comparing(UserIngredient::getUserId)
                  .thenComparing(UserIngredient::getExpiryDate))
          .collect(Collectors.toList());
    } catch (Exception e) {
      logger.error("Error fetching expiring ingredients", e);
      return new ArrayList<>();
    }
  }

  /**
   * Check whether the input Ingredient contains acceptable values
   *
   * @return whether boolean on whether ingredient is valid
   */
  private void validateIngredient(UserIngredient ingredient, boolean isUpdate) {
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
