package nus.iss.team3.backend.businessService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import nus.iss.team3.backend.businessService.ingredient.IngredientBusinessService;
import nus.iss.team3.backend.domainService.ingredient.IIngredientService;
import nus.iss.team3.backend.domainService.notification.NotificationService;
import nus.iss.team3.backend.domainService.user.UserAccountService;
import nus.iss.team3.backend.entity.ENotificationType;
import nus.iss.team3.backend.entity.Ingredient;
import nus.iss.team3.backend.entity.Notification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class TestBusinessIngredientService {

  @InjectMocks private IngredientBusinessService ingredientBusinessService;

  @Mock private IIngredientService ingredientService;

  @Mock private NotificationService notificationService;

  @Mock private UserAccountService userAccountService;

  @Test
  public void checkIngredientsExpiry() {
    Integer userId = 1;

    Ingredient expiringIn3Days = new Ingredient();
    expiringIn3Days.setId(1);
    expiringIn3Days.setName("Fish");
    expiringIn3Days.setQuantity(1.0);
    expiringIn3Days.setUom("kg");
    expiringIn3Days.setExpiryDate(
        Date.from(LocalDate.now().plusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant()));

    Ingredient expiringIn1Day = new Ingredient();
    expiringIn1Day.setId(2);
    expiringIn1Day.setName("Meat");
    expiringIn1Day.setQuantity(0.5);
    expiringIn1Day.setUom("kg");
    expiringIn1Day.setExpiryDate(
        Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));

    List<Ingredient> userIngredients = Arrays.asList(expiringIn3Days, expiringIn1Day);
    List<Integer> userIds = Arrays.asList(userId);

    when(userAccountService.getAllUserIds()).thenReturn(userIds);
    when(ingredientService.getIngredientsByUser(userId)).thenReturn(userIngredients);
    when(notificationService.getNotificationsForUser(eq(userId), anyInt()))
        .thenReturn(Arrays.asList());

    ingredientBusinessService.checkIngredientsExpiry();

    verify(notificationService, times(2))
        .createNotification(
            argThat(
                notification ->
                    notification.getUserId() == userId
                        && (notification.getTitle().equals("Ingredient Expiry Notice")
                            || notification.getTitle().equals("Ingredient Expiry Alert"))
                        && notification.getType() == ENotificationType.INFO
                        && !notification.getIsRead()));
  }

  @Test
  public void checkIngredientsExpiryWithExistingNotifications() {
    Integer userId = 1;

    Ingredient expiringIn3Days = new Ingredient();
    expiringIn3Days.setId(1);
    expiringIn3Days.setName("Fish");
    expiringIn3Days.setQuantity(1.0);
    expiringIn3Days.setUom("kg");
    expiringIn3Days.setExpiryDate(
        Date.from(LocalDate.now().plusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant()));

    // Create an existing notification
    Notification existingNotification = new Notification();
    existingNotification.setContent(
        String.format(
            "Your ingredient '%s' will expire in %d day(s), quantity: %.1f%s, please use it before it expires.",
            expiringIn3Days.getName(), 3, expiringIn3Days.getQuantity(), expiringIn3Days.getUom()));

    List<Ingredient> userIngredients = Arrays.asList(expiringIn3Days);
    List<Integer> userIds = Arrays.asList(userId);

    when(userAccountService.getAllUserIds()).thenReturn(userIds);
    when(ingredientService.getIngredientsByUser(userId)).thenReturn(userIngredients);
    when(notificationService.getNotificationsForUser(eq(userId), anyInt()))
        .thenReturn(Arrays.asList(existingNotification));
    when(ingredientService.getIngredientById(1)).thenReturn(expiringIn3Days);

    ingredientBusinessService.checkIngredientsExpiry();

    verify(notificationService, never()).createNotification(any());
  }
}
