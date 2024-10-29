/* (C)2024 */
package nus.iss.team3.backend.entity;

import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Contains records that is related to a user's Ingredient. - id : must be unique
 *
 * @author Liu Kun, Pinardy
 */
public class UserIngredient extends Ingredient {
  private int id;
  private int userId;
  private Date expiryDate;
  private ZonedDateTime createDateTime;
  private ZonedDateTime updateDateTime;

  public UserIngredient() {}

  public UserIngredient(
      int id,
      String name,
      int userId,
      String uom,
      Double quantity,
      Date expiryDate,
      ZonedDateTime createDateTime,
      ZonedDateTime updateDateTime) {
    this.id = id;
    this.userId = userId;
    this.expiryDate = expiryDate;
    this.createDateTime = createDateTime;
    this.updateDateTime = updateDateTime;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public Date getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(Date expiryDate) {
    this.expiryDate = expiryDate;
  }

  public ZonedDateTime getCreateDateTime() {
    return createDateTime;
  }

  public void setCreateDateTime(ZonedDateTime createDateTime) {
    this.createDateTime = createDateTime;
  }

  public ZonedDateTime getUpdateDateTime() {
    return updateDateTime;
  }

  public void setUpdateDateTime(ZonedDateTime updateDateTime) {
    this.updateDateTime = updateDateTime;
  }

  @Override
  public String toString() {
    return "Ingredient{"
        + "id='"
        + id
        + '\''
        + ", name='"
        + name
        + '\''
        + ", quantity='"
        + quantity
        + '\''
        + ", userId='"
        + userId
        + '\''
        + ", uom='"
        + uom
        + '\''
        + ", expiryDate='"
        + expiryDate
        + '\''
        + ", createDateTime='"
        + createDateTime
        + '\''
        + ", updateDateTime='"
        + updateDateTime
        + '\''
        + '}';
  }
}
