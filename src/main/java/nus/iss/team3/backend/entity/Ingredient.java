package nus.iss.team3.backend.entity;

import java.util.Date;

public class Ingredient {
    private Long itemId;
    private String itemName;
    private String owner;
    private Integer quantity;
    private Date expiryDate;

    public Long getItemId() {
        return itemId;
    }
    // 不需要setItemId吧？

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getOwner() {
        return owner;
    }

    // 不需要setOwner吧？

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public String toString() {
        return "Ingredient{"
                + "itemName='"
                + itemName
                + '\''
                + ", quantity='"
                + quantity
                + '\''
                + ", expiryDate='"
                + expiryDate
                + '\''
                + '}';
    }
}
