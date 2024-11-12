/* (C)2024 */
package nus.iss.team3.backend.dataaccess;

import static nus.iss.team3.backend.dataaccess.PostgresSqlStatement.*;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nus.iss.team3.backend.dataaccess.postgres.IPostgresDataAccess;
import nus.iss.team3.backend.entity.EUserRole;
import nus.iss.team3.backend.entity.EUserStatus;
import nus.iss.team3.backend.entity.UserAccount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repository class to connect to postgres for user account Data
 *
 * @author Desmond Tan Zhi Heng, REN JIARUI
 */
@Repository
public class UserAccountDataAccess implements IUserAccountDataAccess {

  private static final Logger logger = LogManager.getLogger(UserAccountDataAccess.class);

  @Autowired IPostgresDataAccess postgresDataAccess;

  @Autowired private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public UserAccount authenticateUser(String name, String password) {

    try {
      return verifyUserCredentials(PostgresSqlStatement.SQL_AUTHENTICATE_USER, name, password);
    } catch (Exception e) {
      logger.error("Authentication failed", e);
      return null;
    }
  }

  private UserAccount verifyUserCredentials(String sql, String name, String password) {

    Map<String, Object> sqlInput = new HashMap<>();
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_NAME, name);

    List<Map<String, Object>> temp = postgresDataAccess.queryStatement(sql, sqlInput);
    if (temp == null || temp.size() != 1) {
      logger.error("Error with account, please review account : {}", name);
      return null;
    }
    UserAccount user = mapRowToUserAccount(temp.getFirst());

    if (user != null && BCrypt.checkpw(password, user.getPassword())) {
      user.setPassword(null);
      return user;
    }
    return null;
  }

  /**
   * @param userAccount user account to be added
   * @return whether adding the account was successful or not
   */
  @Override
  public boolean addUser(UserAccount userAccount) {

    Map<String, Object> sqlInput = createUserAccountMap(userAccount);

    try {
      int rowUpdated =
          postgresDataAccess.upsertStatement(PostgresSqlStatement.SQL_USER_ACCOUNT_ADD, sqlInput);
      return rowUpdated == 1;
    } catch (DataAccessException e) {
      logger.error("Error adding user: {}", userAccount.getName(), e);
      return false;
    }
  }

  /**
   * @param id userId of account to be deleted
   * @return whether the deletion of the account was successful
   */
  @Override
  public boolean deleteUserById(Integer id) {

    Map<String, Object> sqlInput = new HashMap<>();
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_ID, id);

    try {
      int rowUpdated =
          postgresDataAccess.upsertStatement(
              PostgresSqlStatement.SQL_USER_ACCOUNT_DELETE, sqlInput);
      return rowUpdated == 1;
    } catch (DataAccessException e) {
      logger.error("Error deleting user with ID: {}", id, e);
      return false;
    }
  }

  /**
   * @param userAccount user account to be added
   * @return whether updating the account was successful or not
   */
  @Override
  public boolean updateUser(UserAccount userAccount) {

    Map<String, Object> sqlInput = createUserAccountMap(userAccount);
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_ID, userAccount.getId());

    try {
      int rowUpdated =
          postgresDataAccess.upsertStatement(
              PostgresSqlStatement.SQL_USER_ACCOUNT_UPDATE, sqlInput);
      return rowUpdated == 1;
    } catch (DataAccessException e) {
      logger.error("Error updating user: {}", userAccount.getName(), e);
      return false;
    }
  }

  public boolean updateUserStatus(Integer userId, EUserStatus status) {
    Map<String, Object> sqlInput = new HashMap<>();
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_ID, userId);
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_STATUS, status.getCode());
    try {
      int result =
          postgresDataAccess.upsertStatement(
              PostgresSqlStatement.SQL_USER_ACCOUNT_UPDATE_STATUS, sqlInput);
      logger.info("Successfully updated status to {} for user {}", status, userId);
      return result == 1;
    } catch (Exception e) {
      logger.error("Error updating user status for user {}: {}", userId, e.getMessage(), e);
      return false;
    }
  }

  @Override
  public UserAccount getUserById(Integer id) {
    try {
      Map<String, Object> sqlInput = new HashMap<>();
      sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_ID, id);
      List<Map<String, Object>> result =
          postgresDataAccess.queryStatement(
              PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_ID, sqlInput);
      return (result == null || result.isEmpty()) ? null : mapRowToUserAccount(result.getFirst());
    } catch (DataAccessException e) {
      logger.error("Error getting user by ID: {}", id, e);
      return null;
    }
  }

  @Override
  public UserAccount getUserByName(String name) {
    try {
      Map<String, Object> sqlInput = new HashMap<>();
      sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_NAME, name);
      List<Map<String, Object>> result =
          postgresDataAccess.queryStatement(
              PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_NAME, sqlInput);
      return (result == null || result.isEmpty()) ? null : mapRowToUserAccount(result.getFirst());
    } catch (DataAccessException e) {
      logger.error("Error getting user by name: {}", name, e);
      return null;
    }
  }

  @Override
  public UserAccount getUserByEmail(String email) {
    try {
      Map<String, Object> sqlInput = new HashMap<>();
      sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_EMAIL, email);
      List<Map<String, Object>> result =
          postgresDataAccess.queryStatement(
              PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_EMAIL, sqlInput);
      return (result == null || result.isEmpty()) ? null : mapRowToUserAccount(result.getFirst());
    } catch (DataAccessException e) {
      logger.error("Error getting user by email: {}", email, e);
      return null;
    }
  }

  /**
   * @return the whole list of user in the systems
   */
  @Override
  public List<UserAccount> getAllUsers() {
    String sql = PostgresSqlStatement.SQL_USER_ACCOUNT_GET_ALL;
    try {

      List<Map<String, Object>> results =
          postgresDataAccess.queryStatement(sql, Collections.emptyMap());
      if (results == null || results.isEmpty()) {
        logger.warn("Query returned null. Returning empty list.");
        return Collections.emptyList();
      }
      List<UserAccount> users = results.stream().map(this::mapRowToUserAccount).toList();

      logger.info("Found {} users", users.size());
      return users;
    } catch (DataAccessException e) {
      logger.error("Error getting all users", e);
      return Collections.emptyList();
    }
  }

  private Map<String, Object> createUserAccountMap(UserAccount userAccount) {
    Map<String, Object> sqlInput = new HashMap<>();
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_NAME, userAccount.getName());
    sqlInput.put(
        PostgresSqlStatement.INPUT_USER_ACCOUNT_PASSWORD,
        userAccount.getPassword()); // Consider hashing password
    sqlInput.put(
        PostgresSqlStatement.INPUT_USER_ACCOUNT_DISPLAY_NAME, userAccount.getDisplayName());
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_EMAIL, userAccount.getEmail());
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_STATUS, userAccount.getStatus().getCode());
    sqlInput.put(PostgresSqlStatement.INPUT_USER_ACCOUNT_ROLE, userAccount.getRole().getCode());
    return sqlInput;
  }

  private UserAccount mapRowToUserAccount(Map<String, Object> row) {
    if (row == null) {
      return null;
    }
    try {
      UserAccount userAccount = new UserAccount();

      userAccount.setId(
          row.get(COLUMN_USER_ACCOUNT_ID) != null
              ? ((Number) row.get(COLUMN_USER_ACCOUNT_ID)).intValue()
              : null);
      userAccount.setName(
          row.get(COLUMN_USER_ACCOUNT_NAME) != null
              ? ((String) row.get(COLUMN_USER_ACCOUNT_NAME))
              : null);
      userAccount.setPassword(
          row.get(COLUMN_USER_ACCOUNT_PASSWORD) != null
              ? ((String) row.get(COLUMN_USER_ACCOUNT_PASSWORD))
              : null);
      userAccount.setDisplayName(
          row.get(COLUMN_USER_ACCOUNT_DISPLAY_NAME) != null
              ? ((String) row.get(COLUMN_USER_ACCOUNT_DISPLAY_NAME))
              : null);
      userAccount.setEmail(
          row.get(COLUMN_USER_ACCOUNT_EMAIL) != null
              ? (String) row.get(COLUMN_USER_ACCOUNT_EMAIL)
              : null);
      userAccount.setStatus(
          row.get(COLUMN_USER_ACCOUNT_STATUS) != null
              ? EUserStatus.valueOfCode(((Number) row.get(COLUMN_USER_ACCOUNT_STATUS)).intValue())
              : null);
      userAccount.setRole(
          row.get(COLUMN_USER_ACCOUNT_ROLE) != null
              ? EUserRole.valueOfCode(((Number) row.get(COLUMN_USER_ACCOUNT_ROLE)).intValue())
              : null);
      userAccount.setCreateDateTime(
          row.get(COLUMN_USER_ACCOUNT_CREATE_DATETIME) != null
              ? ((Timestamp) row.get(COLUMN_USER_ACCOUNT_CREATE_DATETIME))
                  .toInstant()
                  .atZone(ZoneId.systemDefault())
              : null);
      userAccount.setUpdateDateTime(
          row.get(COLUMN_USER_ACCOUNT_UPDATE_DATETIME) != null
              ? ((Timestamp) row.get(COLUMN_USER_ACCOUNT_UPDATE_DATETIME))
                  .toInstant()
                  .atZone(ZoneId.systemDefault())
              : null);

      logger.debug("RecipeReview object mapping completed: ID={}", userAccount.getId());
      return userAccount;
    } catch (Exception e) {
      logger.error("Error mapping row to UserAccount", e);
      return null;
    }
  }
}
