package nus.iss.team3.backend.service;

import nus.iss.team3.backend.entity.Ingredient;

public class IngredientService implements IIngredientService{
    public boolean addIngredient(Ingredient ingredient);

    public boolean updateIngredient(Ingredient ingredient);

    public boolean deleteIngredientById(String ingredientName);

    public UserAccount getUserById(String userName);

    public List<UserAccount> getAllUser();
}
