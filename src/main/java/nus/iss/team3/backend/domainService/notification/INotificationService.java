package nus.iss.team3.backend.domainService.notification;

import java.util.List;
import nus.iss.team3.backend.entity.Notification;

public interface INotificationService {

  boolean createNotification(Notification notification);

  List<Notification> getNotificationsForUser(Integer userId, int limit);

  int getUnreadNotificationCountForUser(Integer userId);

  boolean markNotificationAsRead(Integer notificationId, Integer userId);

  boolean markAllNotificationsAsReadForUser(Integer userId);
}
