package nus.iss.team3.backend.entity;

import java.util.Objects;

/**
 * Recipe Ingredient Class
 *
 * @author Mao Weining
 */
// Entity
public class RecipeIngredient {

  private Long id;
  private Long recipeId;
  private String name;
  private Double quantity;
  private String uom;

  // Constructors
  public RecipeIngredient() {}

  public RecipeIngredient(Long id, Long recipeId, String name, Double quantity, String uom) {
    this.id = id;
    this.recipeId = recipeId;
    this.name = name;
    this.quantity = quantity;
    this.uom = uom;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Double getQuantity() {
    return quantity;
  }

  public void setQuantity(Double quantity) {
    this.quantity = quantity;
  }

  public String getUom() {
    return uom;
  }

  public void setUom(String uom) {
    this.uom = uom;
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
