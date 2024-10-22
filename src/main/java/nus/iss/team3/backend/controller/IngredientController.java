package nus.iss.team3.backend.controller;

import java.util.List;
import nus.iss.team3.backend.domainService.ingredient.IIngredientService;
import nus.iss.team3.backend.entity.UserIngredient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class to handle web call for ingredient related queries
 *
 * @author Liu Kun, Ren Jiarui
 */
@RestController
@RequestMapping("ingredient")
public class IngredientController {

  private static final Logger logger = LogManager.getLogger(IngredientController.class);

  @Autowired private IIngredientService ingredientService;

  @PostMapping("/add")
  public ResponseEntity<?> addIngredient(@RequestBody UserIngredient ingredient) {
    try {
      if (ingredientService.addIngredient(ingredient)) {
        logger.info("Creation of Ingredient: {} completed", ingredient.getName());
        return new ResponseEntity<>(true, HttpStatus.CREATED);
      }
      logger.info("Creation of Ingredient: {} failed", ingredient.getName());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (IllegalArgumentException e) {
      logger.error("Invalid status or role code", e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Error adding ingredient", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/update")
  public ResponseEntity<Boolean> updateIngredient(@RequestBody UserIngredient ingredient) {
    try {
      if (ingredientService.updateIngredient(ingredient)) {
        logger.info("Update of Ingredient: {} completed", ingredient.getName());
        return new ResponseEntity<>(true, HttpStatus.OK);
      }
      logger.info("Update of Ingredient: {} failed", ingredient.getName());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error updating ingredient", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Boolean> deleteIngredientById(@PathVariable int id) {
    try {
      if (ingredientService.deleteIngredientById(id)) {
        logger.info("Deletion of Ingredient: {} completed", id);
        return new ResponseEntity<>(true, HttpStatus.OK);
      }
      logger.info("Deletion of Ingredient: {} failed", id);
      return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error deleting ingredient", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/delete/user/{userId}")
  public ResponseEntity<Boolean> deleteIngredientByUserId(@PathVariable int userId) {
    try {
      if (ingredientService.deleteIngredientsByUser(userId)) {
        logger.info("Deletion of Ingredient By user: {} completed", userId);
        return new ResponseEntity<>(true, HttpStatus.OK);
      }
      logger.info("Deletion of Ingredient by user: {} failed", userId);
      return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Error deleting ingredient", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/get/{id}")
  public ResponseEntity<?> getIngredient(@PathVariable int id) {
    try {
      UserIngredient ingredient = ingredientService.getIngredientById(id);
      if (ingredient != null) {
        logger.info("Retrieved Ingredient: {}", id);
        return new ResponseEntity<UserIngredient>(ingredient, HttpStatus.OK);
      } else {
        logger.info("Ingredient not found: {}", id);
        return new ResponseEntity<Boolean>(false, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } catch (Exception e) {
      logger.error("Error getting ingredient", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/get/name/{name}")
  public ResponseEntity<?> getIngredientByName(@PathVariable String name) {
    try {
      List<UserIngredient> ingredientList = ingredientService.getIngredientsByName(name);
      if (ingredientList != null) {
        logger.info("Retrieved Ingredient: {}", name);
        return new ResponseEntity<List<UserIngredient>>(ingredientList, HttpStatus.OK);
      } else {
        logger.info("Ingredient not found: {}", name);
        return new ResponseEntity<Boolean>(false, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } catch (Exception e) {
      logger.error("Error getting ingredient", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/getAll/{userId}")
  public ResponseEntity<List<UserIngredient>> getIngredientsByUser(@PathVariable int userId) {
    try {
      List<UserIngredient> ingredients = ingredientService.getIngredientsByUser(userId);
      logger.info("Retrieved {} Ingredient", ingredients.size());
      return new ResponseEntity<>(ingredients, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Error getting all ingredients for user", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/expiring-list")
  public ResponseEntity<?> getExpiringIngredients(
      @RequestParam int userId, @RequestParam(defaultValue = "3") int days) {
    try {
      List<Ingredient> expiringIngredients = ingredientService.getExpiringIngredients(userId, days);
      logger.info(
          "Retrieved {} expiring ingredients for user {}", expiringIngredients.size(), userId);
      return new ResponseEntity<>(expiringIngredients, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Error getting expiring ingredients for user {}", userId, e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/trigger-expiry-check")
  public ResponseEntity<?> triggerExpiryCheck() {
    try {
      ingredientService.checkIngredientsExpiry();
      logger.info("Manual expiry check triggered successfully");
      return new ResponseEntity<>(true, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Error triggering expiry check", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
