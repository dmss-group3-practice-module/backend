package nus.iss.team3.backend.controller;

import nus.iss.team3.backend.ProfileConfig;
import nus.iss.team3.backend.businessService.ingredient.IIngredientBusinessService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/expiry")
@Profile(ProfileConfig.PROFILE_INGREDIENT)
public class ExpiryController {

  private static final Logger logger = LogManager.getLogger(ExpiryController.class);

  @Autowired private IIngredientBusinessService ingredientBusinessService;

  @Scheduled(cron = "0 0 0 * * ?")
  public void checkIngredientsExpiry() {
    logger.info("executing ingredients expiry check");
    ingredientBusinessService.checkIngredientsExpiry();
  }

  @PostMapping("/check")
  public ResponseEntity<?> manualCheck() {
    checkIngredientsExpiry();
    return ResponseEntity.ok().build();
  }
}
