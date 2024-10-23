package nus.iss.team3.backend.controller;

import java.util.List;
import nus.iss.team3.backend.entity.Notification;
import nus.iss.team3.backend.service.notification.NotificationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("notification")
public class NotificationController {

  private static final Logger logger = LogManager.getLogger(NotificationController.class);

  @Autowired private NotificationService notificationService;

  @GetMapping
  public ResponseEntity<?> getNotifications(
      @RequestParam int userId, @RequestParam(defaultValue = "10") int limit) {
    try {
      logger.info(
          "Received request to get notifications for user ID: {} with limit: {}", userId, limit);
      List<Notification> notifications = notificationService.getNotificationsForUser(userId, limit);
      return ResponseEntity.ok(notifications);
    } catch (Exception e) {
      logger.error("Error getting notifications for user ID: {}", userId, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error retrieving notifications");
    }
  }

  @GetMapping("/unread-count")
  public ResponseEntity<?> getUnreadNotificationCount(@RequestParam int userId) {
    try {
      logger.info("Received request to get unread notification count for user ID: {}", userId);
      int count = notificationService.getUnreadNotificationCountForUser(userId);
      return ResponseEntity.ok(count);
    } catch (Exception e) {
      logger.error("Error getting unread notification count for user ID: {}", userId, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error retrieving unread notification count");
    }
  }

  @PutMapping("/{notificationId}/mark-read")
  public ResponseEntity<?> markNotificationAsRead(
      @PathVariable int notificationId, @RequestParam int userId) {
    try {
      logger.info(
          "Received request to mark notification ID: {} as read for user ID: {}",
          notificationId,
          userId);
      boolean success = notificationService.markNotificationAsRead(notificationId, userId);
      if (success) {
        return ResponseEntity.ok().build();
      } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("Notification not found or already read");
      }
    } catch (Exception e) {
      logger.error(
          "Error marking notification as read: {} for user ID: {}", notificationId, userId, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error marking notification as read");
    }
  }

  @PutMapping("/mark-all-read")
  public ResponseEntity<?> markAllNotificationsAsRead(@RequestParam int userId) {
    try {
      logger.info("Received request to mark all notifications as read for user ID: {}", userId);
      boolean success = notificationService.markAllNotificationsAsReadForUser(userId);
      if (success) {
        return ResponseEntity.ok().build();
      } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("No notifications found or all already read");
      }
    } catch (Exception e) {
      logger.error("Error marking all notifications as read for user ID: {}", userId, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error marking all notifications as read");
    }
  }
}
