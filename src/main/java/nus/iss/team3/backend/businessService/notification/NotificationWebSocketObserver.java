package nus.iss.team3.backend.businessService.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import jakarta.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import nus.iss.team3.backend.entity.Notification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/** manage all websocket stuffs, */
@Service
public class NotificationWebSocketObserver extends TextWebSocketHandler
    implements INotificationWebSocketObserver {
  private static final Logger logger = LogManager.getLogger(NotificationWebSocketObserver.class);
  private final Map<Integer, List<WebSocketSession>> userSessions = new ConcurrentHashMap<>();
  private final ObjectMapper objectMapper = new ObjectMapper();

  @PostConstruct
  public void postConstruct() {
    JavaTimeModule javaTimeModule = new JavaTimeModule();
    javaTimeModule.addSerializer(
        ZonedDateTime.class, new ZonedDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    objectMapper.registerModule(javaTimeModule);
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  public void registerUserSession(Integer userId, WebSocketSession session) {

    if (!userSessions.containsKey(userId)) {
      userSessions.put(userId, new ArrayList<>());
    }
    if (!userSessions.get(userId).contains(session)) {
      userSessions.get(userId).add(session);
    }
  }

  @Override
  public void sendMessage(String action, int userId, Notification notification) {

    List<WebSocketSession> sessions = userSessions.get(userId);
    if (sessions == null || sessions.isEmpty()) {
      return;
    }
    for (WebSocketSession session : sessions) {
      if (session != null && session.isOpen()) {
        try {

          Map<String, Object> message =
              Map.of("action", action, "payload", (notification != null ? notification : "null"));
          session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));

        } catch (Exception e) {
          logger.error("Error processing notification ", e);
        }
      }
    }
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {

    Integer userId = extractUserIdFromSession(session);
    if (userId != null) {
      registerUserSession(userId, session);
      logger.info("WebSocket connection established for user: {}", userId);
    }
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

    logger.info("WebSocket connection closed for session: {}", session.getId());
    Integer userId = extractUserIdFromSession(session);
    if (userId != null) {
      userSessions.remove(userId);
      logger.info("WebSocket connection closed for user: {}", userId);
    }
  }

  //  @Override
  //  protected void handleTextMessage(WebSocketSession session, TextMessage message) {
  //    logger.error("Received from frontend ???: " + message.getPayload());
  //    //    session.sendMessage(message);
  //  }
  private Integer extractUserIdFromSession(WebSocketSession session) {
    String query = session.getUri().getQuery();
    if (query != null && query.contains("userId=")) {
      String userId = query.split("userId=")[1].split("&")[0];
      return Integer.parseInt(userId);
    }
    return null;
  }
}
