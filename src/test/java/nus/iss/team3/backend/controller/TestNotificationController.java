package nus.iss.team3.backend.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import nus.iss.team3.backend.domainService.notification.NotificationService;
import nus.iss.team3.backend.entity.ENotificationType;
import nus.iss.team3.backend.entity.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NotificationController.class)
public class TestNotificationController {

  @Autowired private MockMvc mockMvc;
  @MockBean private NotificationService notificationService;
  private List<Notification> mockNotifications;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    Notification notification1 =
        createMockNotification(1, "Info Title", "Info Content", ENotificationType.INFO);
    Notification notification2 =
        createMockNotification(1, "Warning Title", "Warning Content", ENotificationType.WARNING);
    mockNotifications = Arrays.asList(notification1, notification2);

    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  private Notification createMockNotification(
      Integer userId, String title, String content, ENotificationType type) {
    Notification notification = new Notification(userId, title, content, type);
    notification.setId(1);
    notification.setIsRead(false);
    notification.setCreateDateTime(ZonedDateTime.now());
    return notification;
  }

  @Test
  void testCreateNotification_Success() throws Exception {
    Notification notification =
        createMockNotification(1, "Test Title", "Test Content", ENotificationType.INFO);
    when(notificationService.createNotification(any(Notification.class))).thenReturn(true);

    mockMvc
        .perform(
            post("/notification/1/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
        .andExpect(status().isCreated());
  }

  @Test
  void testCreateNotification_Failure() throws Exception {
    Notification notification =
        createMockNotification(1, "Test Title", "Test Content", ENotificationType.INFO);
    when(notificationService.createNotification(any(Notification.class))).thenReturn(false);

    mockMvc
        .perform(
            post("/notification/1/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Failed to create notification"));
  }

  @Test
  void testGetNotifications_Success() throws Exception {
    when(notificationService.getNotificationsForUser(1, 10)).thenReturn(mockNotifications);

    mockMvc
        .perform(get("/notification/1").param("limit", "10"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void testGetNotifications_Failure() throws Exception {
    when(notificationService.getNotificationsForUser(1, 10))
        .thenThrow(new RuntimeException("Database error"));

    mockMvc
        .perform(get("/notification/1").param("limit", "10"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Error retrieving notifications"));
  }

  @Test
  void testGetUnreadNotificationCount_Success() throws Exception {
    when(notificationService.getUnreadNotificationCountForUser(1)).thenReturn(5);

    mockMvc
        .perform(get("/notification/1/unread-count"))
        .andExpect(status().isOk())
        .andExpect(content().string("5"));
  }

  @Test
  void testGetUnreadNotificationCount_Failure() throws Exception {
    when(notificationService.getUnreadNotificationCountForUser(1))
        .thenThrow(new RuntimeException("Database error"));

    mockMvc
        .perform(get("/notification/1/unread-count"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Error retrieving unread notification count"));
  }

  @Test
  void testMarkNotificationAsRead_Success() throws Exception {
    when(notificationService.markNotificationAsRead(1, 1)).thenReturn(true);

    mockMvc
        .perform(put("/notification/1/mark-read").param("userId", "1"))
        .andExpect(status().isOk());
  }

  @Test
  void testMarkNotificationAsRead_NotFound() throws Exception {
    when(notificationService.markNotificationAsRead(1, 1)).thenReturn(false);

    mockMvc
        .perform(put("/notification/1/mark-read").param("userId", "1"))
        .andExpect(status().isNotFound())
        .andExpect(content().string("Notification not found or already read"));
  }

  @Test
  void testMarkAllNotificationsAsRead_Success() throws Exception {
    when(notificationService.markAllNotificationsAsReadForUser(1)).thenReturn(true);

    mockMvc.perform(put("/notification/1/mark-all-read")).andExpect(status().isOk());
  }

  @Test
  void testMarkAllNotificationsAsRead_NotFound() throws Exception {
    when(notificationService.markAllNotificationsAsReadForUser(1)).thenReturn(false);

    mockMvc
        .perform(put("/notification/1/mark-all-read"))
        .andExpect(status().isNotFound())
        .andExpect(content().string("No notifications found or all already read"));
  }
}
