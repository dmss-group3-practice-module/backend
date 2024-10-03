package nus.iss.team3.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class to handle web call for ingredient related queries
 *
 * @author Liu Kun
 */

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {
    
    @Autowired
    private IIngredientService ingredientService;

    // 添加配料
    @PostMapping
    public ResponseEntity<Ingredient> addIngredient(@RequestBody Ingredient ingredient) {
        Ingredient newIngredient = ingredientService.addIngredient(ingredient);
        return new ResponseEntity<>(newIngredient, HttpStatus.CREATED);
    }

    // 删除配料
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 更新配料
    @PutMapping("/{id}")
    public ResponseEntity<Ingredient> updateIngredient(@PathVariable Long id, @RequestBody Ingredient ingredient) {
        Ingredient updatedIngredient = ingredientService.updateIngredient(id, ingredient);
        return new ResponseEntity<>(updatedIngredient, HttpStatus.OK);
    }

    // 获取单个配料
    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredient(@PathVariable Long id) {
        Ingredient ingredient = ingredientService.getIngredient(id);
        return new ResponseEntity<>(ingredient, HttpStatus.OK);
    }

    // 获取所有配料
    @GetMapping
    public ResponseEntity<List<Ingredient>> getAllIngredients() {
        List<Ingredient> ingredients = ingredientService.getAllIngredients();
        return new ResponseEntity<>(ingredients, HttpStatus.OK);
    }
}
