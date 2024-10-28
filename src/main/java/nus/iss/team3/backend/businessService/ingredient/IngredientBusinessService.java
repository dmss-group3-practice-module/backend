package nus.iss.team3.backend.businessService.ingredient;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import nus.iss.team3.backend.domainService.ingredient.IIngredientService;
import nus.iss.team3.backend.domainService.notification.INotificationService;
import nus.iss.team3.backend.domainService.user.IUserAccountService;
import nus.iss.team3.backend.entity.ENotificationType;
import nus.iss.team3.backend.entity.Ingredient;
import nus.iss.team3.backend.entity.Notification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IngredientBusinessService implements IIngredientBusinessService {

  private static final Logger logger = LogManager.getLogger(IngredientBusinessService.class);
  @Autowired private IUserAccountService userAccountService;
  @Autowired private IIngredientService ingredientService;
  @Autowired private INotificationService notificationService;

  @Override
  public void checkIngredientsExpiry() {
    logger.info("Starting daily ingredient expiry check");
    List<Integer> userIds = userAccountService.getAllUserIds();
    LocalDate today = LocalDate.now();

    for (Integer userId : userIds) {
      List<Ingredient> userIngredients = ingredientService.getIngredientsByUser(userId);

      List<Notification> recentNotifications =
          notificationService.getNotificationsForUser(userId, 50);

      for (Ingredient ingredient : userIngredients) {
        LocalDate expiryDate =
            ingredient.getExpiryDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        long daysUntilExpiry = today.until(expiryDate).getDays();

        // 只处理3天和1天的提醒
        if (daysUntilExpiry != 3 && daysUntilExpiry != 1) {
          continue;
        }

        // 检查是否已经发送过该天数的提醒
        boolean alreadyNotified =
            hasExistingNotification(recentNotifications, ingredient.getId(), daysUntilExpiry);

        if (!alreadyNotified) {
          String title =
              daysUntilExpiry == 3 ? "Ingredient Expiry Notice" : "Ingredient Expiry Alert";
          sendExpiryNotification(userId, ingredient, daysUntilExpiry, title);
        }
      }
    }
  }

  private boolean hasExistingNotification(
      List<Notification> notifications, int ingredientId, long daysUntilExpiry) {
    String searchText = String.format("will expire in %d day", daysUntilExpiry);

    return notifications.stream()
        .filter(n -> n.getContent().contains(searchText))
        .anyMatch(
            n ->
                n.getContent()
                    .contains(
                        String.format(
                            "ingredient '%s' will",
                            ingredientService.getIngredientById(ingredientId).getName())));
  }

  private void sendExpiryNotification(
      int userId, Ingredient ingredient, long daysUntilExpiry, String title) {
    Notification notification = new Notification();
    notification.setUserId(userId);
    notification.setTitle(title);
    notification.setContent(
        String.format(
            "Your ingredient '%s' will expire in %d day(s), quantity: %.1f%s, please use it before it expires.",
            ingredient.getName(), daysUntilExpiry, ingredient.getQuantity(), ingredient.getUom()));
    notification.setType(ENotificationType.INFO);
    notification.setIsRead(false);

    try {
      notificationService.createNotification(notification);
      logger.info(
          "Created expiry notification for user {} about ingredient {}",
          userId,
          ingredient.getName());
    } catch (Exception e) {
      logger.error("Failed to create expiry notification for user {}", userId, e);
    }
  }
}
