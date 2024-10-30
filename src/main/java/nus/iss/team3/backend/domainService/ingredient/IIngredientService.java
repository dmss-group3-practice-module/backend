/* (C)2024 */
package nus.iss.team3.backend.domainService.ingredient;

import java.util.List;
import nus.iss.team3.backend.entity.UserIngredient;

/**
 * Interface for userAccountService, contains logic involving userAccounts.
 *
 * @author Liu Kun
 */
public interface IIngredientService {
  boolean addIngredient(UserIngredient ingredient);

  boolean updateIngredient(UserIngredient ingredient);

  boolean deleteIngredientById(Integer id);

  UserIngredient getIngredientById(Integer id);

  List<UserIngredient> getIngredientsByName(String name);

  List<UserIngredient> getIngredientsByUser(Integer userId);

  boolean deleteIngredientsByUser(Integer userId);

  List<UserIngredient> getExpiringIngredients(Integer userId, int days);

  List<UserIngredient> getExpiringIngredientsInRange();
}
