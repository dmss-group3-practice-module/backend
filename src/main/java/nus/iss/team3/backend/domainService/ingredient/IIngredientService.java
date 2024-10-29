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
  public boolean addIngredient(UserIngredient ingredient);

  public boolean updateIngredient(UserIngredient ingredient);

  public boolean deleteIngredientById(Integer id);

  public UserIngredient getIngredientById(Integer id);

  public List<UserIngredient> getIngredientsByName(String name);

  public List<UserIngredient> getIngredientsByUser(Integer userId);

  public boolean deleteIngredientsByUser(Integer userId);
}
