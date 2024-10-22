package nus.iss.team3.backend.domainService.notification;

import java.util.List;
import nus.iss.team3.backend.dataaccess.INotificationDataAccess;
import nus.iss.team3.backend.entity.Notification;
import nus.iss.team3.backend.util.StringUtilities;
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

  @Override
  public boolean createNotification(Notification notification) {
    // 基本验证
    if (notification.getUserId() == null
        || StringUtilities.isStringNullOrBlank(notification.getTitle())
        || StringUtilities.isStringNullOrBlank(notification.getContent())) {
      logger.error("Invalid notification data: {}", notification);
      throw new IllegalArgumentException("Invalid notification data");
    }

    // 如果没有设置已读状态，默认为未读
    if (notification.getIsRead() == null) {
      notification.setIsRead(false);
    }

    logger.info("Creating notification for user ID: {}", notification.getUserId());
    return notificationDataAccess.createNotification(notification);
  }
}
