package nus.iss.team3.backend.domainService.notification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import nus.iss.team3.backend.dataaccess.INotificationDataAccess;
import nus.iss.team3.backend.entity.ENotificationType;
import nus.iss.team3.backend.entity.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TestNotificationService {

  @Mock private INotificationDataAccess notificationDataAccess;

  @InjectMocks private NotificationService notificationService;

  private List<Notification> mockNotifications;

  @BeforeEach
  void setUp() {
    mockNotifications = Arrays.asList(new Notification(), new Notification());
  }

  @Test
  void testCreateNotification_Success() {
    // 准备测试数据
    Notification notification = new Notification();
    notification.setUserId(1);
    notification.setTitle("Test Title");
    notification.setContent("Test Content");
    notification.setType(ENotificationType.INFO);

    when(notificationDataAccess.createNotification(notification)).thenReturn(true);

    // 执行测试
    boolean result = notificationService.createNotification(notification);

    // 验证结果
    assertTrue(result);
    assertFalse(notification.getIsRead()); // 验证默认设置为未读
    verify(notificationDataAccess).createNotification(notification);
  }

  @Test
  void testCreateNotification_WithIsReadTrue() {
    // 准备测试数据
    Notification notification = new Notification();
    notification.setUserId(1);
    notification.setTitle("Test Title");
    notification.setContent("Test Content");
    notification.setType(ENotificationType.INFO);
    notification.setIsRead(true);

    when(notificationDataAccess.createNotification(notification)).thenReturn(true);

    // 执行测试
    boolean result = notificationService.createNotification(notification);

    // 验证结果
    assertTrue(result);
    assertTrue(notification.getIsRead()); // 验证保持原有的已读状态
    verify(notificationDataAccess).createNotification(notification);
  }

  @Test
  void testCreateNotification_NullUserId() {
    Notification notification = new Notification();
    notification.setTitle("Test Title");
    notification.setContent("Test Content");

    assertThrows(
        IllegalArgumentException.class, () -> notificationService.createNotification(notification));

    verify(notificationDataAccess, never()).createNotification(any());
  }

  @Test
  void testCreateNotification_EmptyTitle() {
    Notification notification = new Notification();
    notification.setUserId(1);
    notification.setContent("Test Content");
    notification.setTitle("");

    assertThrows(
        IllegalArgumentException.class, () -> notificationService.createNotification(notification));

    verify(notificationDataAccess, never()).createNotification(any());
  }

  @Test
  void testCreateNotification_NullTitle() {
    Notification notification = new Notification();
    notification.setUserId(1);
    notification.setContent("Test Content");
    notification.setTitle(null);

    assertThrows(
        IllegalArgumentException.class, () -> notificationService.createNotification(notification));

    verify(notificationDataAccess, never()).createNotification(any());
  }

  @Test
  void testCreateNotification_EmptyContent() {
    Notification notification = new Notification();
    notification.setUserId(1);
    notification.setTitle("Test Title");
    notification.setContent("");

    assertThrows(
        IllegalArgumentException.class, () -> notificationService.createNotification(notification));

    verify(notificationDataAccess, never()).createNotification(any());
  }

  @Test
  void testCreateNotification_NullContent() {
    Notification notification = new Notification();
    notification.setUserId(1);
    notification.setTitle("Test Title");
    notification.setContent(null);

    assertThrows(
        IllegalArgumentException.class, () -> notificationService.createNotification(notification));

    verify(notificationDataAccess, never()).createNotification(any());
  }

  @Test
  void testGetNotificationsForUser_Success() {
    when(notificationDataAccess.getNotificationsForUser(1, 10)).thenReturn(mockNotifications);

    List<Notification> result = notificationService.getNotificationsForUser(1, 10);

    assertEquals(mockNotifications, result);
    verify(notificationDataAccess).getNotificationsForUser(1, 10);
  }

  @Test
  void testGetNotificationsForUser_EmptyList() {
    when(notificationDataAccess.getNotificationsForUser(1, 10)).thenReturn(Collections.emptyList());

    List<Notification> result = notificationService.getNotificationsForUser(1, 10);

    assertTrue(result.isEmpty());
    verify(notificationDataAccess).getNotificationsForUser(1, 10);
  }

  @Test
  void testGetUnreadNotificationCountForUser_Success() {
    when(notificationDataAccess.getUnreadNotificationCountForUser(1)).thenReturn(5);

    int count = notificationService.getUnreadNotificationCountForUser(1);

    assertEquals(5, count);
    verify(notificationDataAccess).getUnreadNotificationCountForUser(1);
  }

  @Test
  void testGetUnreadNotificationCountForUser_ZeroCount() {
    when(notificationDataAccess.getUnreadNotificationCountForUser(1)).thenReturn(0);

    int count = notificationService.getUnreadNotificationCountForUser(1);

    assertEquals(0, count);
    verify(notificationDataAccess).getUnreadNotificationCountForUser(1);
  }

  @Test
  void testMarkNotificationAsRead_Success() {
    when(notificationDataAccess.markNotificationAsRead(1, 1)).thenReturn(true);

    boolean result = notificationService.markNotificationAsRead(1, 1);

    assertTrue(result);
    verify(notificationDataAccess).markNotificationAsRead(1, 1);
  }

  @Test
  void testMarkNotificationAsRead_Failure_NotFound() {
    when(notificationDataAccess.markNotificationAsRead(1, 1)).thenReturn(false);

    boolean result = notificationService.markNotificationAsRead(1, 1);

    assertFalse(result);
    verify(notificationDataAccess).markNotificationAsRead(1, 1);
  }

  @Test
  void testMarkAllNotificationsAsReadForUser_Success() {
    when(notificationDataAccess.markAllNotificationsAsReadForUser(1)).thenReturn(true);

    boolean result = notificationService.markAllNotificationsAsReadForUser(1);

    assertTrue(result);
    verify(notificationDataAccess).markAllNotificationsAsReadForUser(1);
  }

  @Test
  void testMarkAllNotificationsAsReadForUser_Failure_NoNotificationsFound() {
    when(notificationDataAccess.markAllNotificationsAsReadForUser(1)).thenReturn(false);

    boolean result = notificationService.markAllNotificationsAsReadForUser(1);

    assertFalse(result);
    verify(notificationDataAccess).markAllNotificationsAsReadForUser(1);
  }
}
