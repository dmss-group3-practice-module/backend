package nus.iss.team3.backend.businessService.notification;

import nus.iss.team3.backend.entity.Notification;

public interface INotificationBusinessService {

  Notification createNotification(Notification notification);

  boolean markNotificationAsRead(int notificationId, int userId);

  boolean markAllNotificationsAsReadForUser(int userId);
}
