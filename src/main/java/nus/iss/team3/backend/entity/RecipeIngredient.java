package nus.iss.team3.backend.entity;

import java.util.Objects;

/**
 * Recipe Ingredient Class
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
    this.name = name;
    this.quantity = quantity;
    this.uom = uom;
  }

  public RecipeIngredient(Builder builder) {
    this.id = builder.id;
    this.recipeId = builder.recipeId;
    this.name = builder.name;
    this.quantity = builder.quantity;
    this.uom = builder.uom;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Long id;
    private Long recipeId;
    private String name;
    private Double quantity;
    private String uom;

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder recipeId(Long recipeId) {
      this.recipeId = recipeId;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder quantity(Double quantity) {
      this.quantity = quantity;
      return this;
    }

    public Builder uom(String uom) {
      this.uom = uom;
      return this;
    }

    public RecipeIngredient build() {
      return new RecipeIngredient(this);
    }
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
