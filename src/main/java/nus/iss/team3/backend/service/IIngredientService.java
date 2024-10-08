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

  public boolean deleteIngredientById(Integer id);

  public Ingredient getIngredientById(Integer id);

  public List<Ingredient> getIngredientsByUser(Integer userId);

  public boolean deleteIngredientsByUser(Integer userId);
}
