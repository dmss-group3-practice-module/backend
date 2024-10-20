package nus.iss.team3.backend.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import nus.iss.team3.backend.entity.Notification;
import nus.iss.team3.backend.service.NotificationService;
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

  @BeforeEach
  void setUp() {
    mockNotifications = Arrays.asList(new Notification(), new Notification());
  }

  @Test
  void testGetNotifications_Success() throws Exception {
    when(notificationService.getNotificationsForUser(1, 10)).thenReturn(mockNotifications);

    mockMvc
        .perform(get("/notifications").param("userId", "1").param("limit", "10"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    verify(notificationService).getNotificationsForUser(1, 10);
  }

  @Test
  void testGetNotifications_Failure_InvalidUserId() throws Exception {
    mockMvc
        .perform(get("/notifications").param("userId", "invalid").param("limit", "10"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testGetUnreadNotificationCount_Success() throws Exception {
    when(notificationService.getUnreadNotificationCountForUser(1)).thenReturn(5);

    mockMvc
        .perform(get("/notifications/unread-count").param("userId", "1"))
        .andExpect(status().isOk())
        .andExpect(content().string("5"));

    verify(notificationService).getUnreadNotificationCountForUser(1);
  }

  @Test
  void testMarkNotificationAsRead_Success() throws Exception {
    when(notificationService.markNotificationAsRead(1, 1)).thenReturn(true);

    mockMvc
        .perform(put("/notifications/1/mark-read").param("userId", "1"))
        .andExpect(status().isOk());

    verify(notificationService).markNotificationAsRead(1, 1);
  }

  @Test
  void testMarkNotificationAsRead_Failure_NotFound() throws Exception {
    when(notificationService.markNotificationAsRead(1, 1)).thenReturn(false);

    mockMvc
        .perform(put("/notifications/1/mark-read").param("userId", "1"))
        .andExpect(status().isNotFound());

    verify(notificationService).markNotificationAsRead(1, 1);
  }

  @Test
  void testMarkAllNotificationsAsRead_Success() throws Exception {
    when(notificationService.markAllNotificationsAsReadForUser(1)).thenReturn(true);

    mockMvc
        .perform(put("/notifications/mark-all-read").param("userId", "1"))
        .andExpect(status().isOk());

    verify(notificationService).markAllNotificationsAsReadForUser(1);
  }

  @Test
  void testMarkAllNotificationsAsRead_Failure_NoNotificationsFound() throws Exception {
    when(notificationService.markAllNotificationsAsReadForUser(1)).thenReturn(false);

    mockMvc
        .perform(put("/notifications/mark-all-read").param("userId", "1"))
        .andExpect(status().isNotFound());

    verify(notificationService).markAllNotificationsAsReadForUser(1);
  }
}
