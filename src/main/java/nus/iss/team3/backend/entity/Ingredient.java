package nus.iss.team3.backend.entity;

/**
 * Contains records related to a base Ingredient
 *
 * @author Pinardy
 */
public class Ingredient {
  public String name;
  public Double quantity;
  public String uom;

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
}
