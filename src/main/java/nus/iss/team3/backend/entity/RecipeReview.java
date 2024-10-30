package nus.iss.team3.backend.entity;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * Recipe Review Class
 *
 * @author Mao Weining
 */
// Entity
public class RecipeReview {

  private Long id;
  private Long recipeId;
  private Long creatorId; // This creator refers to comments' creator
  private Double rating;
  private Timestamp createDatetime;
  private Timestamp updateDatetime;
  private String comments;

  // Constructors
  public RecipeReview() {}

  public RecipeReview(
      Long id,
      Long recipeId,
      Long creatorId,
      Double rating,
      Timestamp createDatetime,
      Timestamp updateDatetime,
      String comments) {
    this.id = id;
    this.recipeId = recipeId;
    this.creatorId = creatorId;
    this.rating = rating;
    this.createDatetime = createDatetime;
    this.updateDatetime = updateDatetime;
    this.comments = comments;
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

  public Long getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(Long creatorId) {
    this.creatorId = creatorId;
  }

  public Double getRating() {
    return rating;
  }

  public void setRating(Double rating) {
    this.rating = rating;
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

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  @Override
  public String toString() {
    return "RecipeReview{"
        + "id="
        + id
        + ", recipeId="
        + recipeId
        + ", creatorId="
        + creatorId
        + ", rating="
        + rating
        + ", createDatetime="
        + createDatetime
        + ", updateDatetime="
        + updateDatetime
        + ", comments='"
        + comments
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
    RecipeReview that = (RecipeReview) obj;
    return Objects.equals(id, that.id)
        && Objects.equals(recipeId, that.recipeId)
        && Objects.equals(creatorId, that.creatorId)
        && Objects.equals(rating, that.rating)
        && Objects.equals(comments, that.comments);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, recipeId, creatorId, rating, comments);
  }
}
