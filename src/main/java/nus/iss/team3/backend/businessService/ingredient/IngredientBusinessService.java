package nus.iss.team3.backend.businessService.ingredient;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nus.iss.team3.backend.domainService.ingredient.IIngredientService;
import nus.iss.team3.backend.domainService.notification.INotificationService;
import nus.iss.team3.backend.entity.ENotificationType;
import nus.iss.team3.backend.entity.Notification;
import nus.iss.team3.backend.entity.UserIngredient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IngredientBusinessService implements IIngredientBusinessService {

  private static final Logger logger = LogManager.getLogger(IngredientBusinessService.class);

  @Autowired private IIngredientService ingredientService;

  @Autowired private INotificationService notificationService;

  @Override
  @Transactional
  public void checkIngredientsExpiry() {
    logger.info("Starting daily ingredient expiry check");

    try {
      // Get all ingredients expiring in next 3 days in one query
      List<UserIngredient> expiringIngredients = ingredientService.getExpiringIngredientsInRange();

      if (expiringIngredients.isEmpty()) {
        logger.info("No ingredients found expiring in the next 3 days");
        return;
      }

      logger.info("Found {} ingredients expiring soon", expiringIngredients.size());

      // Group ingredients by user and days until expiry
      Map<Integer, Map<Integer, List<UserIngredient>>> userIngredientMap =
          expiringIngredients.stream()
              .collect(
                  Collectors.groupingBy(
                      UserIngredient::getUserId,
                      Collectors.groupingBy(
                          ingredient -> {
                            LocalDate expiryDate =
                                ingredient
                                    .getExpiryDate()
                                    .toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();
                            return (int) LocalDate.now().until(expiryDate).getDays();
                          })));

      // Process notifications for each user
      userIngredientMap.forEach(
          (userId, daysMap) -> {
            try {
              processUserExpiringIngredients(userId, daysMap);
            } catch (Exception e) {
              logger.error("Error processing expiring ingredients for user {}", userId, e);
            }
          });

      logger.info("Completed daily ingredient expiry check");

    } catch (Exception e) {
      logger.error("Error during ingredient expiry check", e);
      throw new RuntimeException("Failed to complete ingredient expiry check", e);
    }
  }

  private void processUserExpiringIngredients(
      int userId, Map<Integer, List<UserIngredient>> daysMap) {
    // Get recent notifications once per user
    List<Notification> recentNotifications =
        notificationService.getNotificationsForUser(userId, 50);

    logger.debug("Processing expiring ingredients for user {}", userId);

    // Process each expiry day group (3 days and 1 day)
    daysMap.forEach(
        (daysUntilExpiry, ingredients) -> {
          // Only process 3-day and 1-day notifications
          if (daysUntilExpiry != 3 && daysUntilExpiry != 1) {
            return;
          }

          // Group ingredients that haven't been notified yet
          List<UserIngredient> unnotifiedIngredients =
              ingredients.stream()
                  .filter(
                      ingredient ->
                          !hasExistingNotification(
                              recentNotifications, ingredient.getId(), daysUntilExpiry))
                  .collect(Collectors.toList());

          if (!unnotifiedIngredients.isEmpty()) {
            String title =
                daysUntilExpiry == 3 ? "Ingredient Expiry Notice" : "Ingredient Expiry Alert";

            // Create consolidated notification for multiple ingredients
            sendConsolidatedExpiryNotification(
                userId, unnotifiedIngredients, daysUntilExpiry, title);
          }
        });
  }

  private boolean hasExistingNotification(
      List<Notification> notifications, int ingredientId, long daysUntilExpiry) {
    if (notifications == null || notifications.isEmpty()) {
      return false;
    }

    String searchText = String.format("will expire in %d day", daysUntilExpiry);
    String ingredientName = ingredientService.getIngredientById(ingredientId).getName();

    return notifications.stream()
        .filter(n -> n.getContent().contains(searchText))
        .anyMatch(
            n -> n.getContent().contains(String.format("ingredient '%s' will", ingredientName)));
  }

  private void sendConsolidatedExpiryNotification(
      int userId, List<UserIngredient> ingredients, long daysUntilExpiry, String title) {

    StringBuilder content = new StringBuilder();
    if (ingredients.size() == 1) {
      UserIngredient ingredient = ingredients.get(0);
      content.append(
          String.format(
              "Your ingredient '%s' will expire in %d day(s), quantity: %.1f%s, please use it before it expires.",
              ingredient.getName(),
              daysUntilExpiry,
              ingredient.getQuantity(),
              ingredient.getUom()));
    } else {
      content.append(
          String.format(
              "You have %d ingredients expiring in %d day(s):\n",
              ingredients.size(), daysUntilExpiry));

      for (UserIngredient ingredient : ingredients) {
        content.append(
            String.format(
                "- %s (%.1f%s)\n",
                ingredient.getName(), ingredient.getQuantity(), ingredient.getUom()));
      }
      content.append("Please use them before they expire.");
    }

    Notification notification = new Notification();
    notification.setUserId(userId);
    notification.setTitle(title);
    notification.setContent(content.toString());
    notification.setType(ENotificationType.INFO);
    notification.setIsRead(false);

    try {
      notificationService.createNotification(notification);
      logger.info(
          "Created consolidated expiry notification for user {} with {} ingredients",
          userId,
          ingredients.size());
    } catch (Exception e) {
      logger.error("Failed to create expiry notification for user {}", userId, e);
      throw new RuntimeException("Failed to create notification", e);
    }
  }
}
