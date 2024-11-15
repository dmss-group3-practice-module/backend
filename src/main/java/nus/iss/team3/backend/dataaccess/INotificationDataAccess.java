package nus.iss.team3.backend.dataaccess;

import java.util.List;
import nus.iss.team3.backend.entity.Notification;

public interface INotificationDataAccess {

  /** Retrieves the latest notifications for a specified user */
  List<Notification> getNotificationsForUser(Integer userId, int limit);

  /** Gets the count of unread notifications for a specified user */
  int getUnreadNotificationCountForUser(Integer userId);

  /** Marks a specific notification as read for a user */
  boolean markNotificationAsRead(Integer notificationId, Integer userId);

  /** Marks all notifications as read for a specified user */
  boolean markAllNotificationsAsReadForUser(Integer userId);

  Notification createNotification(Notification notification);
}
