package nus.iss.team3.backend.entity;

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
  public CookingStep() {
  }

  public CookingStep(Long id, Long recipeId, String description, String image) {
    this.id = id;
    this.recipeId = recipeId;
    this.description = description;
    this.image = image;
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
    return "CookingStep{" + "id=" + id + ", recipeId=" + recipeId + ", description='" + description
        + '\'' + ", image='" + image + '\'' + '}';
  }
}
