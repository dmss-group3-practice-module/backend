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
    // Create mock notifications with different types
    Notification notification1 =
        createMockNotification(1, "Info Title", "Info Content", ENotificationType.INFO);
    Notification notification2 =
        createMockNotification(1, "Warning Title", "Warning Content", ENotificationType.WARNING);
    mockNotifications = Arrays.asList(notification1, notification2);

    // Configure ObjectMapper for ZonedDateTime
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  private Notification createMockNotification(
      Integer userId, String title, String content, ENotificationType type) {
    Notification notification = new Notification(userId, title, content, type);
    notification.setId(1); // Set a mock ID
    notification.setIsRead(false);
    notification.setCreateDateTime(ZonedDateTime.now());
    return notification;
  }

  @Test
  void testCreateNotification_Success_AllTypes() throws Exception {

    when(notificationService.createNotification(any(Notification.class))).thenReturn(true);

    // Test creating notifications with each type
    for (ENotificationType type : ENotificationType.values()) {
      Notification notification =
          createMockNotification(1, type.getValue() + " Title", type.getValue() + " Content", type);

      mockMvc
          .perform(
              post("/notification/create")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(notification)))
          .andExpect(status().isOk());
    }

    verify(notificationService, times(ENotificationType.values().length))
        .createNotification(any(Notification.class));
  }

  @Test
  void testCreateNotification_Failure_MissingRequiredFields() throws Exception {
    // Test missing userId
    String missingUserId =
        """
                {
                    "title": "Test Title",
                    "content": "Test Content",
                    "type": "INFO"
                }
                """;

    mockMvc
        .perform(
            post("/notification/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(missingUserId))
        .andExpect(status().isInternalServerError());

    // Test missing title
    String missingTitle =
        """
                {
                    "userId": 1,
                    "content": "Test Content",
                    "type": "INFO"
                }
                """;

    mockMvc
        .perform(
            post("/notification/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(missingTitle))
        .andExpect(status().isInternalServerError());
  }

  @Test
  void testCreateNotification_Failure_InvalidType() throws Exception {
    // Create a notification with invalid type (will be handled by Jackson)
    String invalidNotification =
        """
            {
                "userId": 1,
                "title": "Test Title",
                "content": "Test Content",
                "type": "INVALID_TYPE"
            }
            """;

    mockMvc
        .perform(
            post("/notification/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidNotification))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testGetNotifications_Success() throws Exception {
    when(notificationService.getNotificationsForUser(1, 10)).thenReturn(mockNotifications);

    mockMvc
        .perform(get("/notification").param("userId", "1").param("limit", "10"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    verify(notificationService).getNotificationsForUser(1, 10);
  }

  @Test
  void testGetNotifications_Success_DefaultLimit() throws Exception {
    when(notificationService.getNotificationsForUser(1, 10)).thenReturn(mockNotifications);

    mockMvc
        .perform(get("/notification").param("userId", "1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    verify(notificationService).getNotificationsForUser(1, 10);
  }

  @Test
  void testGetNotifications_Failure_InvalidUserId() throws Exception {
    mockMvc
        .perform(get("/notification").param("userId", "invalid").param("limit", "10"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testGetNotifications_Failure_ServiceError() throws Exception {
    when(notificationService.getNotificationsForUser(1, 10))
        .thenThrow(new RuntimeException("Database error"));

    mockMvc
        .perform(get("/notification").param("userId", "1").param("limit", "10"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Error retrieving notifications"));
  }

  @Test
  void testGetUnreadNotificationCount_Success() throws Exception {
    when(notificationService.getUnreadNotificationCountForUser(1)).thenReturn(5);

    mockMvc
        .perform(get("/notification/unread-count").param("userId", "1"))
        .andExpect(status().isOk())
        .andExpect(content().string("5"));

    verify(notificationService).getUnreadNotificationCountForUser(1);
  }

  @Test
  void testGetUnreadNotificationCount_Failure_ServiceError() throws Exception {
    when(notificationService.getUnreadNotificationCountForUser(1))
        .thenThrow(new RuntimeException("Database error"));

    mockMvc
        .perform(get("/notification/unread-count").param("userId", "1"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Error retrieving unread notification count"));
  }

  @Test
  void testMarkNotificationAsRead_Success() throws Exception {
    when(notificationService.markNotificationAsRead(1, 1)).thenReturn(true);

    mockMvc
        .perform(put("/notification/1/mark-read").param("userId", "1"))
        .andExpect(status().isOk());

    verify(notificationService).markNotificationAsRead(1, 1);
  }

  @Test
  void testMarkNotificationAsRead_Failure_NotFound() throws Exception {
    when(notificationService.markNotificationAsRead(1, 1)).thenReturn(false);

    mockMvc
        .perform(put("/notification/1/mark-read").param("userId", "1"))
        .andExpect(status().isNotFound());

    verify(notificationService).markNotificationAsRead(1, 1);
  }

  @Test
  void testMarkAllNotificationsAsRead_Success() throws Exception {
    when(notificationService.markAllNotificationsAsReadForUser(1)).thenReturn(true);

    mockMvc
        .perform(put("/notification/mark-all-read").param("userId", "1"))
        .andExpect(status().isOk());

    verify(notificationService).markAllNotificationsAsReadForUser(1);
  }

  @Test
  void testMarkAllNotificationsAsRead_Failure_NoNotificationsFound() throws Exception {
    when(notificationService.markAllNotificationsAsReadForUser(1)).thenReturn(false);

    mockMvc
        .perform(put("/notification/mark-all-read").param("userId", "1"))
        .andExpect(status().isNotFound());

    verify(notificationService).markAllNotificationsAsReadForUser(1);
  }
}
