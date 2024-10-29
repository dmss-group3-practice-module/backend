/* (C)2024 */
package nus.iss.team3.backend.service;

import java.util.List;
import nus.iss.team3.backend.entity.UserIngredient;

/**
 * Interface for userAccountService, contains logic involving userAccounts.
 *
 * @author Liu Kun, Ren Jiarui
 */
public interface IIngredientService {
  boolean addIngredient(UserIngredient ingredient);

  boolean updateIngredient(UserIngredient ingredient);

  boolean deleteIngredientById(Integer id);

  UserIngredient getIngredientById(Integer id);

  List<UserIngredient> getIngredientsByUser(Integer userId);

  boolean deleteIngredientsByUser(Integer userId);

  List<UserIngredient> getExpiringIngredients(Integer userId, int days);

  void checkIngredientsExpiry();
}
