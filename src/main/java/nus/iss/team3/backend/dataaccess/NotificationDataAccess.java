package nus.iss.team3.backend.dataaccess;

import static nus.iss.team3.backend.dataaccess.PostgresSqlStatement.*;

import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nus.iss.team3.backend.dataaccess.postgres.IPostgresDataAccess;
import nus.iss.team3.backend.entity.ENotificationType;
import nus.iss.team3.backend.entity.Notification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationDataAccess implements INotificationDataAccess {

  private static final Logger logger = LogManager.getLogger(NotificationDataAccess.class);

  @Autowired IPostgresDataAccess postgresDataAccess;

  @Autowired private JdbcTemplate jdbcTemplate;

  // view notification list
  @Override
  public List<Notification> getNotificationsForUser(Integer userId, int limit) {
    try {
      Map<String, Object> sqlInput = new HashMap<>();
      sqlInput.put(INPUT_NOTIFICATION_USER_ID, userId);
      sqlInput.put("limit", limit);
      List<Map<String, Object>> result =
          postgresDataAccess.queryStatement(SQL_NOTIFICATION_GET_FOR_USER, sqlInput);
      if (result == null) {
        logger.warn("No notifications found for user ID: {}", userId);
        return Collections.emptyList();
      }
      return result.stream()
          .map(this::translateDBRecordToNotification)
          .collect(Collectors.toList());
    } catch (DataAccessException e) {
      logger.error("Database error while getting notifications for user ID: {}", userId, e);
      throw new RuntimeException("Failed to retrieve notifications", e);
    }
  }

  @Override
  public int getUnreadNotificationCountForUser(Integer userId) {
    try {
      Map<String, Object> sqlInput = new HashMap<>();
      sqlInput.put(INPUT_NOTIFICATION_USER_ID, userId);
      List<Map<String, Object>> result =
          postgresDataAccess.queryStatement(SQL_NOTIFICATION_GET_UNREAD_COUNT, sqlInput);
      return result.isEmpty() ? 0 : ((Number) result.getFirst().get("count")).intValue();
    } catch (DataAccessException e) {
      logger.error("Error getting unread notification count for user ID: {}", userId, e);
      return 0;
    }
  }

  @Override
  public boolean markNotificationAsRead(Integer notificationId, Integer userId) {
    try {
      Map<String, Object> sqlInput = new HashMap<>();
      sqlInput.put(INPUT_NOTIFICATION_ID, notificationId);
      sqlInput.put(INPUT_NOTIFICATION_USER_ID, userId);
      int rowUpdated = postgresDataAccess.upsertStatement(SQL_NOTIFICATION_MARK_AS_READ, sqlInput);
      return rowUpdated == 1;
    } catch (DataAccessException e) {
      logger.error(
          "Error marking notification as read: {} for user ID: {}", notificationId, userId, e);
      return false;
    }
  }

  @Override
  public boolean markAllNotificationsAsReadForUser(Integer userId) {
    try {
      Map<String, Object> sqlInput = new HashMap<>();
      sqlInput.put(INPUT_NOTIFICATION_USER_ID, userId);
      int rowsUpdated =
          postgresDataAccess.upsertStatement(SQL_NOTIFICATION_MARK_ALL_AS_READ_FOR_USER, sqlInput);
      return rowsUpdated > 0;
    } catch (DataAccessException e) {
      logger.error("Error marking all notifications as read for user ID: {}", userId, e);
      return false;
    }
  }

  @Override
  public boolean createNotification(Notification notification) {
    try {
      Map<String, Object> sqlInput = new HashMap<>();
      sqlInput.put(INPUT_NOTIFICATION_USER_ID, notification.getUserId());
      sqlInput.put(INPUT_NOTIFICATION_TITLE, notification.getTitle());
      sqlInput.put(INPUT_NOTIFICATION_CONTENT, notification.getContent());
      sqlInput.put(INPUT_NOTIFICATION_TYPE, notification.getType().toString());
      sqlInput.put(INPUT_NOTIFICATION_IS_READ, notification.getIsRead());

      int result = postgresDataAccess.upsertStatement(SQL_NOTIFICATION_ADD, sqlInput);
      return result == 1;
    } catch (DataAccessException e) {
      logger.error("Error creating notification for user ID: {}", notification.getUserId(), e);
      return false;
    }
  }

  private Notification translateDBRecordToNotification(Map<String, Object> entity) {
    Notification notification = new Notification();

    if (entity.containsKey(COLUMN_NOTIFICATION_ID)
        && entity.get(COLUMN_NOTIFICATION_ID) instanceof Integer) {
      notification.setId((Integer) entity.get(COLUMN_NOTIFICATION_ID));
    }

    if (entity.containsKey(COLUMN_NOTIFICATION_USER_ID)
        && entity.get(COLUMN_NOTIFICATION_USER_ID) instanceof Integer) {
      notification.setUserId((Integer) entity.get(COLUMN_NOTIFICATION_USER_ID));
    }

    if (entity.containsKey(COLUMN_NOTIFICATION_TITLE)
        && entity.get(COLUMN_NOTIFICATION_TITLE) instanceof String) {
      notification.setTitle((String) entity.get(COLUMN_NOTIFICATION_TITLE));
    }

    if (entity.containsKey(COLUMN_NOTIFICATION_CONTENT)
        && entity.get(COLUMN_NOTIFICATION_CONTENT) instanceof String) {
      notification.setContent((String) entity.get(COLUMN_NOTIFICATION_CONTENT));
    }

    if (entity.containsKey(COLUMN_NOTIFICATION_TYPE)
        && entity.get(COLUMN_NOTIFICATION_TYPE) instanceof String) {
      notification.setType(
          ENotificationType.valueOf((String) entity.get(COLUMN_NOTIFICATION_TYPE)));
    }

    if (entity.containsKey(COLUMN_NOTIFICATION_IS_READ)
        && entity.get(COLUMN_NOTIFICATION_IS_READ) instanceof Boolean) {
      notification.setIsRead((Boolean) entity.get(COLUMN_NOTIFICATION_IS_READ));
    }

    if (entity.containsKey(COLUMN_NOTIFICATION_CREATE_DATETIME)
        && entity.get(COLUMN_NOTIFICATION_CREATE_DATETIME) instanceof java.sql.Timestamp) {
      notification.setCreateDateTime(
          ((java.sql.Timestamp) entity.get(COLUMN_NOTIFICATION_CREATE_DATETIME))
              .toInstant()
              .atZone(ZoneId.systemDefault()));
    }

    return notification;
  }
}
