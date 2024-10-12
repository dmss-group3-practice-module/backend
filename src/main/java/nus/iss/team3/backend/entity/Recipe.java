package nus.iss.team3.backend.entity;

import java.sql.Timestamp;
import java.util.List;

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
  private Integer status;
  private Timestamp createDatetime;
  private Timestamp updateDatetime;
  private List<Ingredient> ingredients;
  private List<CookingStep> cookingSteps;
  private String cuisine;

  // Constructor
  public Recipe() {
  }

  public Recipe(Long id, Long creatorId, String name, String image, String description,
      Integer cookingTimeInSec, Integer difficultyLevel, Double rating, Integer status,
      Timestamp createDatetime, Timestamp updateDatetime, List<Ingredient> ingredients,
      List<CookingStep> cookingSteps, String cuisine) {
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

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
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

  public List<Ingredient> getIngredients() {
    return ingredients;
  }

  public void setIngredients(List<Ingredient> ingredients) {
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

  @Override
  public String toString() {
    return "Recipe{" + "id=" + id + ", creatorId=" + creatorId + ", name='" + name + '\''
        + ", image='" + image + '\'' + ", description='" + description + '\''
        + ", cookingTimeInSec=" + cookingTimeInSec + ", difficultyLevel=" + difficultyLevel
        + ", rating=" + rating + ", status=" + status + ", createDatetime=" + createDatetime
        + ", updateDatetime=" + updateDatetime + ", ingredients=" + ingredients + ", cookingSteps="
        + cookingSteps + ", cuisine='" + cuisine + '\'' + '}';
  }
}
