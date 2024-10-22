package nus.iss.team3.backend.service.notification;

import java.util.List;
import nus.iss.team3.backend.entity.Notification;

public interface INotificationService {

  List<Notification> getNotificationsForUser(Integer userId, int limit);

  int getUnreadNotificationCountForUser(Integer userId);

  boolean markNotificationAsRead(Integer notificationId, Integer userId);

  boolean markAllNotificationsAsReadForUser(Integer userId);
}
