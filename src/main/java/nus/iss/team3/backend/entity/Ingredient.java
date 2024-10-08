/* (C)2024 */
package nus.iss.team3.backend.entity;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Contains records that is related to a Ingredient, - ingredientId : must be unique,
 *
 * @author Liu Kun
 */
// @Entity
public class Ingredient {
  private int id;
  private String name;
  private int userId;
  private String uom;
  private Double quantity;
  private Date expiryDate;
  private Timestamp createTime;
  private Timestamp updateTime;

  public Ingredient() {}

  public Ingredient(
      int id,
      String name,
      int userId,
      String uom,
      Double quantity,
      Date expiryDate,
      Timestamp createTime,
      Timestamp updateTime) {
    this.id = id;
    this.name = name;
    this.userId = userId;
    this.uom = uom;
    this.quantity = quantity;
    this.expiryDate = expiryDate;
    this.createTime = createTime;
    this.updateTime = updateTime;
  }

  public int getIngredientId() {
    return id;
  }

  public void setIngredientId(int id) {
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

  public Timestamp getCreateTime() {
    return createTime;
  }

  public Timestamp getUpdateTime() {
    return updateTime;
  }

  @Override
  public String toString() {
    return "Ingredient{"
        + "id='"
        + id
        + '\''
        + "name='"
        + name
        + '\''
        + ", quantity='"
        + quantity
        + '\''
        + ", user='"
        + userId
        + '\''
        + ", uom='"
        + uom
        + '\''
        + ", expiryDate='"
        + expiryDate
        + '\''
        + ", createTime='"
        + createTime
        + '\''
        + ", updateTime='"
        + updateTime
        + '\''
        + '}';
  }
}
