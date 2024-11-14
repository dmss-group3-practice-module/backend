package nus.iss.team3.backend.domainService.notification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import nus.iss.team3.backend.domainService.webservice.IWebserviceCaller;
import nus.iss.team3.backend.entity.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class TestNotificationWebCaller {

  @InjectMocks private NotificationWebCaller notificationWebCaller;

  @Mock private IWebserviceCaller webServiceCaller;

  @BeforeEach
  public void setUp() {
    notificationWebCaller.postConstruct();
  }

  @Test
  public void getNotificationsForUser_Success() {
    int userId = 1;
    int limit = 10;
    String endingUrl = "/" + userId + "?limit=" + limit;
    List<Notification> notifications =
        List.of(generateSampleNotification(), generateSampleNotification());
    ResponseEntity<List<Notification>> responseEntity =
        new ResponseEntity<>(notifications, HttpStatus.OK);

    when(webServiceCaller.getCall(endsWith(endingUrl), any(ParameterizedTypeReference.class)))
        .thenReturn(responseEntity);

    List<Notification> result = notificationWebCaller.getNotificationsForUser(userId, limit);

    assertNotNull(result);
    assertEquals(2, result.size());
    verify(webServiceCaller, times(1))
        .getCall(endsWith(endingUrl), any(ParameterizedTypeReference.class));
  }

  @Test
  public void getNotificationsForUser_Failure() {
    int userId = 1;
    int limit = 10;
    String endingUrl = "/" + userId + "?limit=" + limit;
    List<Notification> notifications =
        List.of(generateSampleNotification(), generateSampleNotification());
    ResponseEntity<List<Notification>> responseEntity =
        new ResponseEntity<>(notifications, HttpStatus.BAD_REQUEST);

    when(webServiceCaller.getCall(endsWith(endingUrl), any(ParameterizedTypeReference.class)))
        .thenReturn(responseEntity);

    List<Notification> result = notificationWebCaller.getNotificationsForUser(userId, limit);

    assertNull(result);
    verify(webServiceCaller, times(1))
        .getCall(endsWith(endingUrl), any(ParameterizedTypeReference.class));
  }

  @Test
  public void getUnreadNotificationCountForUser_Success() {
    int userId = 1;
    String endingUrl = "/" + userId + "/unread-count";
    ResponseEntity<Integer> responseEntity = new ResponseEntity<>(2, HttpStatus.OK);

    when(webServiceCaller.getCall(endsWith(endingUrl), eq(Integer.class)))
        .thenReturn(responseEntity);

    int result = notificationWebCaller.getUnreadNotificationCountForUser(userId);

    assertEquals(2, result);
    verify(webServiceCaller, times(1)).getCall(endsWith(endingUrl), eq(Integer.class));
  }

  @Test
  public void getUnreadNotificationCountForUser_Success_emptyBody() {
    int userId = 1;
    String endingUrl = "/" + userId + "/unread-count";
    ResponseEntity<Integer> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

    when(webServiceCaller.getCall(endsWith(endingUrl), eq(Integer.class)))
        .thenReturn(responseEntity);

    int result = notificationWebCaller.getUnreadNotificationCountForUser(userId);

    assertEquals(0, result);
    verify(webServiceCaller, times(1)).getCall(endsWith(endingUrl), eq(Integer.class));
  }

  @Test
  public void getUnreadNotificationCountForUser_Failure() {
    int userId = 1;
    String endingUrl = "/" + userId + "/unread-count";
    ResponseEntity<Integer> responseEntity = new ResponseEntity<>(3, HttpStatus.BAD_REQUEST);

    when(webServiceCaller.getCall(endsWith(endingUrl), eq(Integer.class)))
        .thenReturn(responseEntity);

    int result = notificationWebCaller.getUnreadNotificationCountForUser(userId);

    assertEquals(0, result);
    verify(webServiceCaller, times(1)).getCall(endsWith(endingUrl), eq(Integer.class));
  }

  @Test
  public void markNotificationAsRead_Success() {
    int notificationId = 23;
    int userId = 1;
    String endingUrl = "/" + notificationId + "/mark-read?userId=" + userId;
    ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(true, HttpStatus.OK);

    when(webServiceCaller.putCall(endsWith(endingUrl), any(), eq(Boolean.class)))
        .thenReturn(responseEntity);
    boolean result = notificationWebCaller.markNotificationAsRead(notificationId, userId);

    assertTrue(result);
    verify(webServiceCaller, times(1)).putCall(endsWith(endingUrl), any(), eq(Boolean.class));
  }

  @Test
  public void markNotificationAsRead_Success_nullBody() {
    int notificationId = 23;
    int userId = 1;
    String endingUrl = "/" + notificationId + "/mark-read?userId=" + userId;
    ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

    when(webServiceCaller.putCall(endsWith(endingUrl), any(), eq(Boolean.class)))
        .thenReturn(responseEntity);
    boolean result = notificationWebCaller.markNotificationAsRead(notificationId, userId);

    assertFalse(result);
    verify(webServiceCaller, times(1)).putCall(endsWith(endingUrl), any(), eq(Boolean.class));
  }

  @Test
  public void markNotificationAsRead_Failure() {
    int notificationId = 23;
    int userId = 1;
    String endingUrl = "/" + notificationId + "/mark-read?userId=" + userId;
    ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);

    when(webServiceCaller.putCall(endsWith(endingUrl), any(), eq(Boolean.class)))
        .thenReturn(responseEntity);

    boolean result = notificationWebCaller.markNotificationAsRead(notificationId, userId);

    assertFalse(result);
    verify(webServiceCaller, times(1)).putCall(endsWith(endingUrl), any(), eq(Boolean.class));
  }

  @Test
  public void markAllNotificationsAsReadForUser_Success() {
    int userId = 1;
    String endingUrl = "/" + userId + "/mark-all-read";
    ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(true, HttpStatus.OK);

    when(webServiceCaller.putCall(endsWith(endingUrl), any(), eq(Boolean.class)))
        .thenReturn(responseEntity);
    boolean result = notificationWebCaller.markAllNotificationsAsReadForUser(userId);

    assertTrue(result);
    verify(webServiceCaller, times(1)).putCall(endsWith(endingUrl), any(), eq(Boolean.class));
  }

  @Test
  public void markAllNotificationsAsReadForUser_Success_nullBody() {
    int userId = 1;
    String endingUrl = "/" + userId + "/mark-all-read";
    ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

    when(webServiceCaller.putCall(endsWith(endingUrl), any(), eq(Boolean.class)))
        .thenReturn(responseEntity);
    boolean result = notificationWebCaller.markAllNotificationsAsReadForUser(userId);

    assertFalse(result);
    verify(webServiceCaller, times(1)).putCall(endsWith(endingUrl), any(), eq(Boolean.class));
  }

  @Test
  public void markAllNotificationsAsReadForUser_Failure() {
    int userId = 1;
    String endingUrl = "/" + userId + "/mark-all-read";
    ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);

    when(webServiceCaller.putCall(endsWith(endingUrl), any(), eq(Boolean.class)))
        .thenReturn(responseEntity);

    boolean result = notificationWebCaller.markAllNotificationsAsReadForUser(userId);

    assertFalse(result);
    verify(webServiceCaller, times(1)).putCall(endsWith(endingUrl), any(), eq(Boolean.class));
  }

  @Test
  public void createNotification_Success() {
    Notification notification = new Notification();
    notification.setUserId(1);
    String endingUrl = "/" + notification.getUserId() + "/create";
    ResponseEntity<Notification> responseEntity = new ResponseEntity<>(notification, HttpStatus.OK);

    when(webServiceCaller.postCall(
            endsWith(endingUrl), any(Notification.class), eq(Notification.class)))
        .thenReturn(responseEntity);
    Notification result = notificationWebCaller.createNotification(notification);

    assertNotNull(result);
    verify(webServiceCaller, times(1))
        .postCall(endsWith(endingUrl), any(Notification.class), eq(Notification.class));
  }

  @Test
  public void createNotification_Success_nullBody() {
    Notification notification = new Notification();
    notification.setUserId(1);
    String endingUrl = "/" + notification.getUserId() + "/create";
    ResponseEntity<Notification> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

    when(webServiceCaller.postCall(
            endsWith(endingUrl), any(Notification.class), eq(Notification.class)))
        .thenReturn(responseEntity);
    Notification result = notificationWebCaller.createNotification(notification);

    assertNull(result);
    verify(webServiceCaller, times(1))
        .postCall(endsWith(endingUrl), any(Notification.class), eq(Notification.class));
  }

  @Test
  public void createNotification_Failure() {
    Notification notification = new Notification();
    notification.setUserId(1);
    String endingUrl = "/" + notification.getUserId() + "/create";
    ResponseEntity<Notification> responseEntity =
        new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

    when(webServiceCaller.postCall(
            endsWith(endingUrl), any(Notification.class), eq(Notification.class)))
        .thenReturn(responseEntity);

    Notification result = notificationWebCaller.createNotification(notification);

    assertNull(result);
    verify(webServiceCaller, times(1))
        .postCall(endsWith(endingUrl), any(Notification.class), eq(Notification.class));
  }

  private Notification generateSampleNotification() {
    return new Notification(1, "title", "content", null);
  }
}
