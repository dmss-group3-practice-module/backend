package nus.iss.team3.backend.businessService.notification;

import nus.iss.team3.backend.domainService.notification.INotificationService;
import nus.iss.team3.backend.entity.Notification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationBusinessService implements INotificationBusinessService {

  private static final Logger logger = LogManager.getLogger(NotificationBusinessService.class);

  @Autowired private INotificationWebSocketObserver notificationObserver;
  @Autowired private INotificationService notificationService;

  /**
   * @param notification
   * @return
   */
  @Override
  public Notification createNotification(Notification notification) {
    Notification createdNotification = notificationService.createNotification(notification);
    if (createdNotification != null) {
      notifyObservers("create", notification.getUserId(), notification);
      return createdNotification;
    }
    return null;
  }

  /**
   * @param notificationId
   * @param userId
   * @return
   */
  @Override
  public boolean markNotificationAsRead(int notificationId, int userId) {
    if (notificationService.markNotificationAsRead(notificationId, userId)) {
      Notification temp = new Notification();
      temp.setId(notificationId);
      notifyObservers("markAsRead", userId, temp);
      return true;
    }
    return false;
  }

  /**
   * @param userId
   * @return
   */
  @Override
  public boolean markAllNotificationsAsReadForUser(int userId) {
    if (notificationService.markAllNotificationsAsReadForUser(userId)) {
      notifyObservers("markAllAsRead", userId, null);
      return true;
    }
    return false;
  }

  private void notifyObservers(String message, Integer userId, Notification notification) {
    notificationObserver.sendMessage(message, userId, notification);
  }
}
