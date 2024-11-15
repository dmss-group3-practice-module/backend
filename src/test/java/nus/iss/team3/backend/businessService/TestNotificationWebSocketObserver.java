package nus.iss.team3.backend.businessService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.URI;
import nus.iss.team3.backend.businessService.notification.NotificationWebSocketObserver;
import nus.iss.team3.backend.entity.ENotificationType;
import nus.iss.team3.backend.entity.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@ExtendWith(SpringExtension.class)
public class TestNotificationWebSocketObserver {

  @InjectMocks private NotificationWebSocketObserver notificationWebSocketObserver;

  @BeforeEach
  public void setup() {
    notificationWebSocketObserver.postConstruct();
  }

  // the function in this class is closely related, have to test as a whole...
  @Test
  public void normal_1instance() throws IOException {

    WebSocketSession session = generateSampleWebSocketSession();
    notificationWebSocketObserver.afterConnectionEstablished(session);

    notificationWebSocketObserver.sendMessage("check", 1, generateNotification());

    verify(session, times(1)).sendMessage(any(TextMessage.class));
  }

  @Test
  public void normal_1instance_Open_thenClose() throws IOException {

    WebSocketSession session = generateSampleWebSocketSession();
    notificationWebSocketObserver.afterConnectionEstablished(session);

    notificationWebSocketObserver.sendMessage("check", 1, generateNotification());

    verify(session, times(1)).sendMessage(any(TextMessage.class));

    notificationWebSocketObserver.afterConnectionClosed(session, CloseStatus.NORMAL);
    notificationWebSocketObserver.sendMessage("check", 1, generateNotification());

    verify(session, times(1)).sendMessage(any(TextMessage.class));
  }

  @Test
  public void normal_1instance_Open_thenClose_thenOpen() throws IOException {

    WebSocketSession session = generateSampleWebSocketSession();
    notificationWebSocketObserver.afterConnectionEstablished(session);

    notificationWebSocketObserver.sendMessage("check", 1, generateNotification());

    verify(session, times(1)).sendMessage(any(TextMessage.class));

    notificationWebSocketObserver.afterConnectionClosed(session, CloseStatus.NORMAL);
    notificationWebSocketObserver.sendMessage("check", 1, generateNotification());

    verify(session, times(1)).sendMessage(any(TextMessage.class));
    notificationWebSocketObserver.afterConnectionEstablished(session);
    notificationWebSocketObserver.sendMessage("check", 1, generateNotification());

    verify(session, times(2)).sendMessage(any(TextMessage.class));
  }

  @Test
  public void normal_1instance_Open_thenCloseGotError() throws IOException {

    WebSocketSession session = generateSampleWebSocketSession();
    notificationWebSocketObserver.afterConnectionEstablished(session);

    notificationWebSocketObserver.sendMessage("check", 1, generateNotification());

    verify(session, times(1)).sendMessage(any(TextMessage.class));

    URI sessionURI = mock(URI.class);
    when(sessionURI.getQuery()).thenReturn("we/localhost:1234/ws?userXX=1");
    when(session.getUri()).thenReturn(sessionURI);
    notificationWebSocketObserver.afterConnectionClosed(session, CloseStatus.NORMAL);
    notificationWebSocketObserver.sendMessage("check", 1, generateNotification());

    verify(session, times(2)).sendMessage(any(TextMessage.class));
  }

  @Test
  public void normal_1instance_butUserIdMissing() throws IOException {

    WebSocketSession session = generateSampleWebSocketSession();

    URI sessionURI = mock(URI.class);
    when(sessionURI.getQuery()).thenReturn("we/localhost:1234/ws?userXX=1");
    when(session.getUri()).thenReturn(sessionURI);
    notificationWebSocketObserver.afterConnectionEstablished(session);

    notificationWebSocketObserver.sendMessage("check", 1, generateNotification());

    verify(session, times(0)).sendMessage(any(TextMessage.class));
  }

  @Test
  public void normal_1instance_butUserIdNull() throws IOException {

    WebSocketSession session = generateSampleWebSocketSession();

    URI sessionURI = mock(URI.class);
    when(sessionURI.getQuery()).thenReturn(null);
    when(session.getUri()).thenReturn(sessionURI);
    notificationWebSocketObserver.afterConnectionEstablished(session);

    notificationWebSocketObserver.sendMessage("check", 1, generateNotification());

    verify(session, times(0)).sendMessage(any(TextMessage.class));
  }

  @Test
  public void normal_1instance_notificationNull() throws IOException {

    WebSocketSession session = generateSampleWebSocketSession();
    notificationWebSocketObserver.afterConnectionEstablished(session);

    notificationWebSocketObserver.sendMessage("check", 1, null);

    verify(session, times(1)).sendMessage(any(TextMessage.class));
  }

  @Test
  public void normal_0() throws IOException {

    WebSocketSession session = generateSampleWebSocketSession();

    notificationWebSocketObserver.sendMessage("check", 1, generateNotification());

    verify(session, times(0)).sendMessage(any(TextMessage.class));
  }

  @Test
  public void multipleSameInstance() throws IOException {

    WebSocketSession session = generateSampleWebSocketSession();
    notificationWebSocketObserver.afterConnectionEstablished(session);
    notificationWebSocketObserver.afterConnectionEstablished(session);

    notificationWebSocketObserver.sendMessage("check", 1, generateNotification());

    verify(session, times(1)).sendMessage(any(TextMessage.class));
  }

  @Test
  public void closedSession() throws IOException {

    WebSocketSession sessionNotOpen = generateSampleWebSocketSession();
    when(sessionNotOpen.isOpen()).thenReturn(false);
    WebSocketSession session = generateSampleWebSocketSession();
    notificationWebSocketObserver.afterConnectionEstablished(sessionNotOpen);

    notificationWebSocketObserver.sendMessage("check", 1, generateNotification());

    verify(session, times(0)).sendMessage(any(TextMessage.class));
  }

  @Test
  public void multipleDifferentInstance() throws IOException {

    WebSocketSession session1 = generateSampleWebSocketSession();
    WebSocketSession session2 = generateSampleWebSocketSession();
    notificationWebSocketObserver.registerUserSession(1, session1);
    notificationWebSocketObserver.registerUserSession(1, session2);

    notificationWebSocketObserver.sendMessage("check", 1, generateNotification());

    verify(session1, times(1)).sendMessage(any(TextMessage.class));
    verify(session2, times(1)).sendMessage(any(TextMessage.class));
  }

  private WebSocketSession generateSampleWebSocketSession() {
    WebSocketSession session = mock(WebSocketSession.class);
    when(session.getId()).thenReturn("1");
    URI sessionURI = mock(URI.class);
    when(sessionURI.getQuery()).thenReturn("we/localhost:1234/ws?userId=1");
    when(session.getUri()).thenReturn(sessionURI);
    when(session.isOpen()).thenReturn(true);
    return session;
  }

  private Notification generateNotification() {
    Notification returnNotification = new Notification();
    returnNotification.setId(1);
    returnNotification.setUserId(1);
    returnNotification.setType(ENotificationType.INFO);
    returnNotification.setTitle("test title");
    returnNotification.setContent("test content");
    return returnNotification;
  }
}
