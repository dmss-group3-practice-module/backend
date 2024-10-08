/* (C)2024 */
package nus.iss.team3.backend.dataaccess;

import java.util.List;
import nus.iss.team3.backend.entity.Ingredient;

/**
 * Contains the postgres connection required content
 *
 * @author Liu Kun
 */
public interface IIngredientDataAccess {
  public boolean addIngredient(Ingredient ingredient);

  public boolean updateIngredient(Ingredient ingredient);

  public Ingredient getIngredientById(int ingredientId);

  public boolean deleteIngredientById(int ingredientId);

  public List<Ingredient> getIngredientsByUser(int userId);

  public boolean deleteIngredientsByUser(int userId);
}
