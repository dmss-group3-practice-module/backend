package nus.iss.team3.backend.dataaccess;

import static nus.iss.team3.backend.dataaccess.PostgresSqlStatement.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import nus.iss.team3.backend.dataaccess.postgres.PostgresDataAccess;
import nus.iss.team3.backend.entity.ENotificationType;
import nus.iss.team3.backend.entity.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TestNotificationDataAccess {

  @Mock private PostgresDataAccess postgresDataAccess;

  @InjectMocks private NotificationDataAccess notificationDataAccess;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void getNotificationsForUser_nullResult() {
    int userId = 1;
    int limit = 10;

    when(postgresDataAccess.queryStatement(eq(SQL_NOTIFICATION_GET_FOR_USER), any()))
        .thenReturn(null);

    List<Notification> result = notificationDataAccess.getNotificationsForUser(userId, limit);

    assertEquals(result.size(), 0);
    verify(postgresDataAccess, times(1)).queryStatement(eq(SQL_NOTIFICATION_GET_FOR_USER), any());
  }

  @Test
  public void getNotificationsForUser_emptyResult() {
    int userId = 1;
    int limit = 10;

    List<Map<String, Object>> returnList = new ArrayList<>();
    when(postgresDataAccess.queryStatement(eq(SQL_NOTIFICATION_GET_FOR_USER), any()))
        .thenReturn(returnList);

    List<Notification> result = notificationDataAccess.getNotificationsForUser(userId, limit);

    assertEquals(result.size(), 0);
    verify(postgresDataAccess, times(1)).queryStatement(eq(SQL_NOTIFICATION_GET_FOR_USER), any());
  }

  @Test
  public void getNotificationsForUser_validResult() {
    int userId = 1;
    int limit = 10;

    List<Map<String, Object>> returnList = new ArrayList<>();
    {
      Map<String, Object> map = new java.util.HashMap<>();
      map.put("id", 1);
      map.put("user_id", 1);
      map.put("title", "title");
      map.put("content", "content");
      map.put("type", ENotificationType.INFO.name());
      map.put("is_read", false);
      map.put("create_datetime", new Timestamp(2024, 1, 1, 0, 0, 0, 0));
      returnList.add(map);
    }
    when(postgresDataAccess.queryStatement(eq(SQL_NOTIFICATION_GET_FOR_USER), any()))
        .thenReturn(returnList);

    List<Notification> result = notificationDataAccess.getNotificationsForUser(userId, limit);

    assertEquals(result.size(), 1);
    verify(postgresDataAccess, times(1)).queryStatement(eq(SQL_NOTIFICATION_GET_FOR_USER), any());
  }

  @Test
  public void getUnreadNotificationCountForUser_nullResult() {
    int userId = 1;

    when(postgresDataAccess.queryStatement(eq(SQL_NOTIFICATION_GET_UNREAD_COUNT), any()))
        .thenReturn(null);

    int result = notificationDataAccess.getUnreadNotificationCountForUser(userId);

    assertEquals(0, result);
    verify(postgresDataAccess, times(1))
        .queryStatement(eq(SQL_NOTIFICATION_GET_UNREAD_COUNT), any());
  }

  @Test
  public void getUnreadNotificationCountForUser_emptyResult() {
    int userId = 1;

    when(postgresDataAccess.queryStatement(eq(SQL_NOTIFICATION_GET_UNREAD_COUNT), any()))
        .thenReturn(new ArrayList<>());

    int result = notificationDataAccess.getUnreadNotificationCountForUser(userId);

    assertEquals(0, result);
    verify(postgresDataAccess, times(1))
        .queryStatement(eq(SQL_NOTIFICATION_GET_UNREAD_COUNT), any());
  }

  @Test
  public void getUnreadNotificationCountForUser_validResult() {
    int userId = 1;

    List<Map<String, Object>> returnList = new ArrayList<>();
    {
      Map<String, Object> map = new java.util.HashMap<>();
      map.put("count", 99);
      returnList.add(map);
    }
    when(postgresDataAccess.queryStatement(eq(SQL_NOTIFICATION_GET_UNREAD_COUNT), any()))
        .thenReturn(returnList);

    int result = notificationDataAccess.getUnreadNotificationCountForUser(userId);

    assertEquals(99, result);
    verify(postgresDataAccess, times(1))
        .queryStatement(eq(SQL_NOTIFICATION_GET_UNREAD_COUNT), any());
  }

  @Test
  public void markNotificationAsRead_0row() {
    int notificationId = 1;
    int userId = 2;

    when(postgresDataAccess.upsertStatement(eq(SQL_NOTIFICATION_MARK_AS_READ), any()))
        .thenReturn(0);

    boolean result = notificationDataAccess.markNotificationAsRead(notificationId, userId);

    assertFalse(result);
    verify(postgresDataAccess, times(1)).upsertStatement(eq(SQL_NOTIFICATION_MARK_AS_READ), any());
  }

  @Test
  public void markNotificationAsRead_1row() {
    int notificationId = 1;
    int userId = 2;

    when(postgresDataAccess.upsertStatement(eq(SQL_NOTIFICATION_MARK_AS_READ), any()))
        .thenReturn(1);

    boolean result = notificationDataAccess.markNotificationAsRead(notificationId, userId);

    assertTrue(result);
    verify(postgresDataAccess, times(1)).upsertStatement(eq(SQL_NOTIFICATION_MARK_AS_READ), any());
  }

  @Test
  public void markNotificationAsRead_3row() {
    int notificationId = 1;
    int userId = 2;

    when(postgresDataAccess.upsertStatement(eq(SQL_NOTIFICATION_MARK_AS_READ), any()))
        .thenReturn(3);

    boolean result = notificationDataAccess.markNotificationAsRead(notificationId, userId);

    assertFalse(result);
    verify(postgresDataAccess, times(1)).upsertStatement(eq(SQL_NOTIFICATION_MARK_AS_READ), any());
  }

  @Test
  public void markAllNotificationsAsReadForUser_0Row() {
    int userId = 2;

    when(postgresDataAccess.upsertStatement(eq(SQL_NOTIFICATION_MARK_ALL_AS_READ_FOR_USER), any()))
        .thenReturn(0);

    boolean result = notificationDataAccess.markAllNotificationsAsReadForUser(userId);

    assertFalse(result);
    verify(postgresDataAccess, times(1))
        .upsertStatement(eq(SQL_NOTIFICATION_MARK_ALL_AS_READ_FOR_USER), any());
  }

  @Test
  public void markAllNotificationsAsReadForUser_1Row() {
    int userId = 2;

    when(postgresDataAccess.upsertStatement(eq(SQL_NOTIFICATION_MARK_ALL_AS_READ_FOR_USER), any()))
        .thenReturn(1);

    boolean result = notificationDataAccess.markAllNotificationsAsReadForUser(userId);

    assertTrue(result);
    verify(postgresDataAccess, times(1))
        .upsertStatement(eq(SQL_NOTIFICATION_MARK_ALL_AS_READ_FOR_USER), any());
  }

  @Test
  public void markAllNotificationsAsReadForUser_5Row() {
    int userId = 2;

    when(postgresDataAccess.upsertStatement(eq(SQL_NOTIFICATION_MARK_ALL_AS_READ_FOR_USER), any()))
        .thenReturn(5);

    boolean result = notificationDataAccess.markAllNotificationsAsReadForUser(userId);

    assertTrue(result);
    verify(postgresDataAccess, times(1))
        .upsertStatement(eq(SQL_NOTIFICATION_MARK_ALL_AS_READ_FOR_USER), any());
  }

  @Test
  public void createNotification_0Result() {
    Notification input = new Notification();
    input.setUserId(1);
    input.setTitle("Title");
    input.setContent("content");
    input.setType(ENotificationType.INFO);
    input.setIsRead(true);

    when(postgresDataAccess.upsertStatement(eq(SQL_NOTIFICATION_ADD), any())).thenReturn(0);

    boolean result = notificationDataAccess.createNotification(input);

    assertFalse(result);
    verify(postgresDataAccess, times(1)).upsertStatement(eq(SQL_NOTIFICATION_ADD), any());
  }

  @Test
  public void createNotification_1Result() {
    Notification input = new Notification();
    input.setUserId(1);
    input.setTitle("Title");
    input.setContent("content");
    input.setType(ENotificationType.INFO);
    input.setIsRead(true);

    when(postgresDataAccess.upsertStatement(eq(SQL_NOTIFICATION_ADD), any())).thenReturn(1);

    boolean result = notificationDataAccess.createNotification(input);

    assertTrue(result);
    verify(postgresDataAccess, times(1)).upsertStatement(eq(SQL_NOTIFICATION_ADD), any());
  }

  @Test
  public void createNotification_4Result() {
    Notification input = new Notification();
    input.setUserId(1);
    input.setTitle("Title");
    input.setContent("content");
    input.setType(ENotificationType.INFO);
    input.setIsRead(true);

    when(postgresDataAccess.upsertStatement(eq(SQL_NOTIFICATION_ADD), any())).thenReturn(4);

    boolean result = notificationDataAccess.createNotification(input);

    assertFalse(result);
    verify(postgresDataAccess, times(1)).upsertStatement(eq(SQL_NOTIFICATION_ADD), any());
  }
}
