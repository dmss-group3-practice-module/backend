package nus.iss.team3.backend.businessService.notification;

import nus.iss.team3.backend.entity.Notification;

public interface INotificationWebSocketObserver {

  void sendMessage(String action, int userId, Notification notification);
}
