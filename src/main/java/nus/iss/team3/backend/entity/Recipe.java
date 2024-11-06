package nus.iss.team3.backend.entity;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

/**
 * Recipe Class
 *
 * @author Mao Weining
 */
// Entity
public class Recipe {

  private Long id;
  private Long creatorId;
  private String name;
  private String image;
  private String description;
  private Integer cookingTimeInSec;
  private Integer difficultyLevel;
  private Double rating;

  private ERecipeStatus status;

  private Timestamp createDatetime;
  private Timestamp updateDatetime;
  private List<RecipeIngredient> ingredients;
  private List<CookingStep> cookingSteps;
  private String cuisine;

  private Recipe draftRecipe;

  // Constructor
  public Recipe() {}

  public Recipe(
      Long id,
      Long creatorId,
      String name,
      String image,
      String description,
      Integer cookingTimeInSec,
      Integer difficultyLevel,
      Double rating,
      ERecipeStatus status,
      Timestamp createDatetime,
      Timestamp updateDatetime,
      List<RecipeIngredient> ingredients,
      List<CookingStep> cookingSteps,
      String cuisine) {
    this.id = id;
    this.creatorId = creatorId;
    this.name = name;
    this.image = image;
    this.description = description;
    this.cookingTimeInSec = cookingTimeInSec;
    this.difficultyLevel = difficultyLevel;
    this.rating = rating;
    this.status = status;
    this.createDatetime = createDatetime;
    this.updateDatetime = updateDatetime;
    this.ingredients = ingredients;
    this.cookingSteps = cookingSteps;
    this.cuisine = cuisine;
  }

  // Getters and setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(Long creatorId) {
    this.creatorId = creatorId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getCookingTimeInSec() {
    return cookingTimeInSec;
  }

  public void setCookingTimeInSec(Integer cookingTimeInSec) {
    this.cookingTimeInSec = cookingTimeInSec;
  }

  public Integer getDifficultyLevel() {
    return difficultyLevel;
  }

  public void setDifficultyLevel(Integer difficultyLevel) {
    this.difficultyLevel = difficultyLevel;
  }

  public Double getRating() {
    return rating;
  }

  public void setRating(Double rating) {
    this.rating = rating;
  }

  public ERecipeStatus getStatus() {
    return status;
  }

  public void setStatus(ERecipeStatus status) {
    this.status = status;
  }

  public Timestamp getCreateDatetime() {
    return createDatetime;
  }

  public void setCreateDatetime(Timestamp createDatetime) {
    this.createDatetime = createDatetime;
  }

  public Timestamp getUpdateDatetime() {
    return updateDatetime;
  }

  public void setUpdateDatetime(Timestamp updateDatetime) {
    this.updateDatetime = updateDatetime;
  }

  public List<RecipeIngredient> getIngredients() {
    return ingredients;
  }

  public void setIngredients(List<RecipeIngredient> ingredients) {
    this.ingredients = ingredients;
  }

  public List<CookingStep> getCookingSteps() {
    return cookingSteps;
  }

  public void setCookingSteps(List<CookingStep> cookingSteps) {
    this.cookingSteps = cookingSteps;
  }

  public String getCuisine() {
    return cuisine;
  }

  public void setCuisine(String cuisine) {
    this.cuisine = cuisine;
  }

  public Recipe getDraftRecipe() {
    return draftRecipe;
  }

  public void setDraftRecipe(Recipe draftRecipe) {
    this.draftRecipe = draftRecipe;
  }

  @Override
  public String toString() {
    return "Recipe{"
        + "id="
        + id
        + ", creatorId="
        + creatorId
        + ", name='"
        + name
        + '\''
        + ", image='"
        + image
        + '\''
        + ", description='"
        + description
        + '\''
        + ", cookingTimeInSec="
        + cookingTimeInSec
        + ", difficultyLevel="
        + difficultyLevel
        + ", rating="
        + rating
        + ", status="
        + status
        + ", createDatetime="
        + createDatetime
        + ", updateDatetime="
        + updateDatetime
        + ", ingredients="
        + ingredients
        + ", cookingSteps="
        + cookingSteps
        + ", cuisine='"
        + cuisine
        + '\''
        + '}';
  }

  @Override
  public boolean equals(Object obj) {
    // Check if it's the same reference
    if (this == obj) {
      return true;
    }

    // Check for null or different classes
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    // Force cast to Recipe type
    Recipe recipe = (Recipe) obj;
    return Objects.equals(id, recipe.id)
        && Objects.equals(creatorId, recipe.creatorId)
        && Objects.equals(name, recipe.name)
        && Objects.equals(image, recipe.image)
        && Objects.equals(description, recipe.description)
        && Objects.equals(cookingTimeInSec, recipe.cookingTimeInSec)
        && Objects.equals(difficultyLevel, recipe.difficultyLevel)
        && Objects.equals(rating, recipe.rating)
        && status == recipe.status
        && Objects.equals(ingredients, recipe.ingredients)
        && Objects.equals(cookingSteps, recipe.cookingSteps)
        && Objects.equals(cuisine, recipe.cuisine);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        creatorId,
        name,
        image,
        description,
        cookingTimeInSec,
        difficultyLevel,
        rating,
        status,
        ingredients,
        cookingSteps,
        cuisine);
  }
}
