package nus.iss.team3.backend.entity;

import java.util.Objects;

/**
 * Ingredient Class
 *
 * @author Mao Weining
 */
public class RecipeIngredient extends Ingredient {
  private Long id;
  private Long recipeId;

  // Constructors
  public RecipeIngredient() {}

  public RecipeIngredient(Long id, Long recipeId, String name, Double quantity, String uom) {
    this.id = id;
    this.recipeId = recipeId;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getRecipeId() {
    return recipeId;
  }

  public void setRecipeId(Long recipeId) {
    this.recipeId = recipeId;
  }

  @Override
  public String toString() {
    return "Ingredient{"
        + "id="
        + id
        + ", recipeId="
        + recipeId
        + ", name='"
        + name
        + '\''
        + ", quantity="
        + quantity
        + ", uom='"
        + uom
        + '\''
        + '}';
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    RecipeIngredient ingredient = (RecipeIngredient) obj;
    return Objects.equals(id, ingredient.id)
        && Objects.equals(recipeId, ingredient.recipeId)
        && Objects.equals(name, ingredient.name)
        && Objects.equals(quantity, ingredient.quantity)
        && Objects.equals(uom, ingredient.uom);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, recipeId, name, quantity, uom);
  }
}
