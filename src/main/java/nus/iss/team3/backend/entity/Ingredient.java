/* (C)2024 */
package nus.iss.team3.backend.entity;

import java.util.Date;

/**
 * Contains records that is related to a Ingredient, - ingredientId : must be
 * unique,
 *
 * @author Liu Kun
 */
// @Entity
public class Ingredient {
    private String ingredientId;
    private String ingredientName;
    private String userId;
    private Integer ingredientQuantity;
    private Boolean ingredientStatus;
    private Date ingredientExpiryDate;

    public Ingredient() {
    }

    public Ingredient(String ingredientId, String ingredientName, String userId, Integer quantity,
            Boolean ingredientStatus, Date expiryDate) {
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.userId = userId;
        this.ingredientQuantity = ingredientQuantity;
        this.ingredientStatus = ingredientStatus;
        this.ingredientExpiryDate = ingredientExpiryDate;
    }

    public String getIngredientId() {
        return ingredientId;
    }
    // 不需要setItemId吧？

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public String getIngredientOwner() {
        return userId;
    }

    // 不需要setOwner吧？

    public Integer getIngredientQuantity() {
        return ingredientQuantity;
    }

    public void setIngredientQuantity(Integer ingredientQuantity) {
        this.ingredientQuantity = ingredientQuantity;
    }

    public Boolean getIngredientStatus() {
        return ingredientStatus;
    }

    public void setIngredientStatus(Boolean ingredientStatus) {
        this.ingredientStatus = ingredientStatus;
    }

    public Date getIngredientExpiryDate() {
        return ingredientExpiryDate;
    }

    public void setIngredientExpiryDate(Date ingredientExpiryDate) {
        this.ingredientExpiryDate = ingredientExpiryDate;
    }

    @Override
    public String toString() {
        return "Ingredient{"
                + "ingredientName='"
                + ingredientName
                + '\''
                + ", quantity='"
                + ingredientQuantity
                + '\''
                + ", expiryDate='"
                + ingredientExpiryDate
                + '\''
                + ", ingredientStatus='"
                + ingredientStatus
                + '\''
                + '}';
    }
}
