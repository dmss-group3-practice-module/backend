package nus.iss.team3.backend.entity;

import java.util.Objects;

/**
 * CookingStep Class
 *
 * @author Mao Weining
 */
// Entity
public class CookingStep {

  private Long id;
  private Long recipeId;
  private String description;
  private String image;

  // Constructors
  public CookingStep() {}

  public CookingStep(Builder builder) {
    this.id = builder.id;
    this.recipeId = builder.recipeId;
    this.description = builder.description;
    this.image = builder.image;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Long id;
    private Long recipeId;
    private String description;
    private String image;

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder recipeId(Long recipeId) {
      this.recipeId = recipeId;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder image(String image) {
      this.image = image;
      return this;
    }

    public CookingStep build() {
      return new CookingStep(this);
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  @Override
  public String toString() {
    return "CookingStep{"
        + "id="
        + id
        + ", recipeId="
        + recipeId
        + ", description='"
        + description
        + '\''
        + ", image='"
        + image
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
    CookingStep cookingStep = (CookingStep) obj;
    return Objects.equals(id, cookingStep.id)
        && Objects.equals(recipeId, cookingStep.recipeId)
        && Objects.equals(description, cookingStep.description)
        && Objects.equals(image, cookingStep.image);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, recipeId, description, image);
  }
}
