package nus.iss.team3.backend.dataaccess;

import java.util.List;

import nus.iss.team3.backend.entity.UserIngredient;

/**
 * Contains the postgres connection required content
 *
 * @author Liu Kun, Ren Jiarui
 */
public interface IIngredientDataAccess {
  boolean addIngredient(UserIngredient ingredient);

  boolean updateIngredient(UserIngredient ingredient);

  UserIngredient getIngredientById(int ingredientId);

  boolean deleteIngredientById(int ingredientId);

  List<UserIngredient> getIngredientsByName(String name);

  List<UserIngredient> getIngredientsByUser(int userId);

  boolean deleteIngredientsByUser(int userId);
}
