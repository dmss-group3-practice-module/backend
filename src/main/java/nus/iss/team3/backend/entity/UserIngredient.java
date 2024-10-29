/* (C)2024 */
package nus.iss.team3.backend.entity;

import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Contains records that is related to a Ingredient, - id : must be unique
 *
 * @author Liu Kun
 */
// @Entity
public class UserIngredient {
  private int id;
  private String name;
  private int userId;
  private String uom;
  private Double quantity;
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
    this.name = name;
    this.userId = userId;
    this.uom = uom;
    this.quantity = quantity;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public String getUom() {
    return uom;
  }

  public void setUom(String uom) {
    this.uom = uom;
  }

  public Double getQuantity() {
    return quantity;
  }

  public void setQuantity(Double quantity) {
    this.quantity = quantity;
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
