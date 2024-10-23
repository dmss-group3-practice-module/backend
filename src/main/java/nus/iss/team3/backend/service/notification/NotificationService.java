package nus.iss.team3.backend.service.notification;

import java.util.List;
import nus.iss.team3.backend.dataaccess.INotificationDataAccess;
import nus.iss.team3.backend.entity.Notification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService implements INotificationService {

  private static final Logger logger = LogManager.getLogger(NotificationService.class);

  @Autowired private INotificationDataAccess notificationDataAccess;

  @Override
  public List<Notification> getNotificationsForUser(Integer userId, int limit) {
    logger.info("Fetching notifications for user ID: {} with limit: {}", userId, limit);
    return notificationDataAccess.getNotificationsForUser(userId, limit);
  }

  @Override
  public int getUnreadNotificationCountForUser(Integer userId) {
    logger.info("Getting unread notification count for user ID: {}", userId);
    return notificationDataAccess.getUnreadNotificationCountForUser(userId);
  }

  @Override
  public boolean markNotificationAsRead(Integer notificationId, Integer userId) {
    logger.info("Marking notification ID: {} as read for user ID: {}", notificationId, userId);
    return notificationDataAccess.markNotificationAsRead(notificationId, userId);
  }

  @Override
  public boolean markAllNotificationsAsReadForUser(Integer userId) {
    logger.info("Marking all notifications as read for user ID: {}", userId);
    return notificationDataAccess.markAllNotificationsAsReadForUser(userId);
  }
}
