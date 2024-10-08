/* (C)2024 */
package nus.iss.team3.backend.service;

import java.util.List;
import nus.iss.team3.backend.entity.Ingredient;

/**
 * Interface for userAccountService, contains logic involving userAccounts.
 *
 * @author Liu Kun
 */
public interface IIngredientService {
  public boolean addIngredient(Ingredient ingredient);

  public boolean updateIngredient(Ingredient ingredient);

  public boolean deleteIngredientById(String ingredientId);

  public Ingredient getIngredientById(String ingredientId);

  public List<Ingredient> getIngredientsByUser(String userId);

  public boolean deleteIngredientsByUser(String userId);
}
