package nus.iss.team3.backend.domainService.notification;

import java.util.List;
import nus.iss.team3.backend.entity.Notification;

public interface INotificationService {

  boolean createNotification(Notification notification);

  List<Notification> getNotificationsForUser(int userId, int limit);

  int getUnreadNotificationCountForUser(int userId);

  boolean markNotificationAsRead(int notificationId, int userId);

  boolean markAllNotificationsAsReadForUser(int userId);
}
