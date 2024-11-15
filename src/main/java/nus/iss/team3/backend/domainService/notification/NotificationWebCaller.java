package nus.iss.team3.backend.domainService.notification;

import jakarta.annotation.PostConstruct;
import java.util.List;
import nus.iss.team3.backend.ProfileConfig;
import nus.iss.team3.backend.domainService.webservice.IWebserviceCaller;
import nus.iss.team3.backend.entity.Notification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Web caller implementation for notification service
 *
 * @author Ren Jiarui
 */
@Service
@Profile(ProfileConfig.PROFILE_NOT + ProfileConfig.PROFILE_NOTIFICATION)
public class NotificationWebCaller implements INotificationService {
  private static final Logger logger = LogManager.getLogger(NotificationWebCaller.class);

  @Autowired private IWebserviceCaller webServiceCaller;

  @Value("${service.url.notification.address}")
  private String serviceUrl;

  @Value("${service.url.notification.port}")
  private String servicePort;

  @PostConstruct
  public void postConstruct() {
    logger.info("Notification Service Web Caller initialized.");
  }

  private String getUrl(String path) {
    return "http://" + serviceUrl + ":" + servicePort + "/notification" + path;
  }

  @Override
  public List<Notification> getNotificationsForUser(int userId, int limit) {
    String url = getUrl("/" + userId + "?limit=" + limit);
    ParameterizedTypeReference<List<Notification>> typeRef = new ParameterizedTypeReference<>() {};
    ResponseEntity<List<Notification>> response = webServiceCaller.getCall(url, typeRef);

    if (response.getStatusCode().is2xxSuccessful()) {
      return response.getBody();
    } else {
      logger.error(
          "Failed to retrieve notifications for user {}. Status code: {}",
          userId,
          response.getStatusCode());
      return null;
    }
  }

  @Override
  public int getUnreadNotificationCountForUser(int userId) {
    String url = getUrl("/" + userId + "/unread-count");
    ResponseEntity<Integer> response = webServiceCaller.getCall(url, Integer.class);

    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
      return response.getBody();
    } else {
      logger.error(
          "Failed to get unread count for user {}. Status code: {}",
          userId,
          response.getStatusCode());
      return 0;
    }
  }

  @Override
  public boolean markNotificationAsRead(int notificationId, int userId) {
    String url = getUrl("/" + notificationId + "/mark-read?userId=" + userId);
    ResponseEntity<Boolean> response = webServiceCaller.putCall(url, null, Boolean.class);

    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
      return response.getBody();
    }
    logger.error(
        "Failed to mark notification {} as read for user {}. Status code: {}",
        notificationId,
        userId,
        response.getStatusCode());
    return false;
  }

  @Override
  public boolean markAllNotificationsAsReadForUser(int userId) {
    String url = getUrl("/" + userId + "/mark-all-read");
    ResponseEntity<Boolean> response = webServiceCaller.putCall(url, null, Boolean.class);

    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
      return response.getBody();
    }
    logger.error(
        "Failed to mark all notifications as read for user {}. Status code: {}",
        userId,
        response.getStatusCode());
    return false;
  }

  @Override
  public Notification createNotification(Notification notification) {
    String url = getUrl("/" + notification.getUserId() + "/create");
    ResponseEntity<Notification> response =
        webServiceCaller.postCall(url, notification, Notification.class);

    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
      return response.getBody();
    }
    logger.error(
        "Failed to create notification for user {}. Status code: {}",
        notification.getUserId(),
        response.getStatusCode());
    return null;
  }
}
