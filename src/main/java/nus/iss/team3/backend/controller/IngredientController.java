package nus.iss.team3.backend.controller;

import java.util.List;
import nus.iss.team3.backend.entity.Ingredient;
import nus.iss.team3.backend.entity.UserAccount;
import nus.iss.team3.backend.service.IIngredientService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/**
 * Controller class to handle web call for ingredient related queries
 *
 * @author Liu Kun
 */
@RestController
@RequestMapping("ingredients")
public class IngredientController {

    private static final Logger logger = LogManager.getLogger(IngredientController.class);

    @Autowired
    private IIngredientService ingredientService;

    @GetMapping("/")
    public String ingredientPage() {
        return "ingredient page enabled";
    }

    @PostMapping("/add")
    public boolean addIngredient(@RequestBody Ingredient ingredient) {
        if (ingredientService.addIngredient(ingredient)) {
            logger.info("Creation of Ingredient: {} completed", ingredient.getIngredientName());
            return true;
        }
        logger.info("Creation of Ingredient: {} failed", ingredient.getIngredientName());
        return false;
    }

    @PostMapping("/update")
    public boolean updateIngredient(@RequestBody Ingredient ingredient) {
        if (ingredientService.updateIngredient(ingredient)) {
        logger.info("Update of User Account: {} completed", ingredient.getIngredientName());
    return true;
    }
        logger.info("Update of User Account: {} failed", ingredient.getIngredientName());

        return false;
    }

    @PostMapping("/delete")
    public boolean deleteIngredient(@RequestBody String ingredientId) {
      if (ingredientService.deleteIngredientById(ingredientId)) {
        logger.info("Deletion of Ingredient: {} completed", ingredientId);
        return true;
      }
      logger.info("Deletion of Ingredient: {} failed", ingredientId);
  
      return false;
    }

    @PostMapping("/get")
    public Ingredient getIngredient(@RequestBody String ingredientId) {
        return ingredientService.getIngredientById(ingredientId);
    }

    @GetMapping("/getAll")
    public List<Ingredient> getAllIngredients() {
        return ingredientService.getAllIngredients();
    }
}
    
