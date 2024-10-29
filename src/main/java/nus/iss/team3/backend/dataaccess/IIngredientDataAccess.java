package nus.iss.team3.backend.dataaccess;

import java.util.List;
import nus.iss.team3.backend.entity.UserIngredient;

/**
 * Contains the postgres connection required content
 *
 * @author Liu Kun
 */
public interface IIngredientDataAccess {
  public boolean addIngredient(UserIngredient ingredient);

  public boolean updateIngredient(UserIngredient ingredient);

  public UserIngredient getIngredientById(int ingredientId);

  public boolean deleteIngredientById(int ingredientId);

  public List<UserIngredient> getIngredientsByName(String name);

  public List<UserIngredient> getIngredientsByUser(int userId);

  public boolean deleteIngredientsByUser(int userId);
}
