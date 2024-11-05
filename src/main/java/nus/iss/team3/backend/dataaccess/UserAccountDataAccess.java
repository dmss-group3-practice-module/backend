/* (C)2024 */
package nus.iss.team3.backend.dataaccess;

import static nus.iss.team3.backend.dataaccess.PostgresSqlStatement.*;

import java.sql.ResultSet;
import java.sql.SQLException;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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

  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public UserAccount authenticateUser(String name, String password) {
    String sql =
        "SELECT * FROM "
            + PostgresSqlStatement.TABLE_USER_ACCOUNT
            + " WHERE "
            + PostgresSqlStatement.COLUMN_USER_ACCOUNT_NAME
            + " = ?";
    try {
      return verifyUserCredentials(sql, name, password);
    } catch (Exception e) {
      logger.error("Authentication failed", e);
      return null;
    }
  }

  private UserAccount verifyUserCredentials(String sql, String name, String password) {
    UserAccount user =
        jdbcTemplate.queryForObject(sql, new Object[] {name}, new UserAccountRowMapper(true));
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
      int rowUpdated = postgresDataAccess.upsertStatement(SQL_USER_ACCOUNT_ADD, sqlInput);
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
    sqlInput.put(INPUT_USER_ACCOUNT_ID, id);

    try {
      int rowUpdated = postgresDataAccess.upsertStatement(SQL_USER_ACCOUNT_DELETE, sqlInput);
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
    sqlInput.put(INPUT_USER_ACCOUNT_ID, userAccount.getId());

    try {
      int rowUpdated = postgresDataAccess.upsertStatement(SQL_USER_ACCOUNT_UPDATE, sqlInput);
      return rowUpdated == 1;
    } catch (DataAccessException e) {
      logger.error("Error updating user: {}", userAccount.getName(), e);
      return false;
    }
  }

  public boolean updateUserStatus(Integer userId, EUserStatus status) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue(INPUT_USER_ACCOUNT_ID, userId);
    parameters.addValue(INPUT_USER_ACCOUNT_STATUS, status.getCode());

    try {
      namedParameterJdbcTemplate.update(SQL_USER_ACCOUNT_UPDATE_STATUS, parameters);
      logger.info("Successfully updated status to {} for user {}", status, userId);
      return true;
    } catch (Exception e) {
      logger.error("Error updating user status for user {}: {}", userId, e.getMessage(), e);
      return false;
    }
  }

  @Override
  public UserAccount getUserById(Integer id) {
    try {
      Map<String, Object> sqlInput = new HashMap<>();
      sqlInput.put(INPUT_USER_ACCOUNT_ID, id);
      List<Map<String, Object>> result =
          postgresDataAccess.queryStatement(SQL_USER_ACCOUNT_GET_BY_ID, sqlInput);
      return result.isEmpty() ? null : translateDBRecordToUserAccount(result.get(0));
    } catch (DataAccessException e) {
      logger.error("Error getting user by ID: {}", id, e);
      return null;
    }
  }

  @Override
  public UserAccount getUserByName(String name) {
    try {
      Map<String, Object> sqlInput = new HashMap<>();
      sqlInput.put(INPUT_USER_ACCOUNT_NAME, name);
      List<Map<String, Object>> result =
          postgresDataAccess.queryStatement(SQL_USER_ACCOUNT_GET_BY_NAME, sqlInput);
      return result.isEmpty() ? null : translateDBRecordToUserAccount(result.get(0));
    } catch (DataAccessException e) {
      logger.error("Error getting user by name: {}", name, e);
      return null;
    }
  }

  @Override
  public UserAccount getUserByEmail(String email) {
    try {
      Map<String, Object> sqlInput = new HashMap<>();
      sqlInput.put(INPUT_USER_ACCOUNT_EMAIL, email);
      List<Map<String, Object>> result =
          postgresDataAccess.queryStatement(SQL_USER_ACCOUNT_GET_BY_EMAIL, sqlInput);
      return result.isEmpty() ? null : translateDBRecordToUserAccount(result.get(0));
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
    String sql = SQL_USER_ACCOUNT_GET_ALL;
    try {
      List<UserAccount> users = jdbcTemplate.query(sql, new UserAccountRowMapper(false));
      logger.info("Found {} users", users.size());
      return users;
    } catch (DataAccessException e) {
      logger.error("Error getting all users", e);
      return Collections.emptyList();
    }
  }

  private UserAccount translateDBRecordToUserAccount(Map<String, Object> entity) {
    UserAccount returnItem = new UserAccount();

    returnItem.setId((Integer) entity.get(COLUMN_USER_ACCOUNT_ID));
    returnItem.setName((String) entity.get(COLUMN_USER_ACCOUNT_NAME));
    // Password is not set here as it's not returned from the database
    // returnItem.setPassword((String) entity.get(COLUMN_USER_ACCOUNT_PASSWORD));
    returnItem.setDisplayName((String) entity.get(COLUMN_USER_ACCOUNT_DISPLAY_NAME));
    returnItem.setEmail((String) entity.get(COLUMN_USER_ACCOUNT_EMAIL));
    returnItem.setStatus(EUserStatus.valueOfCode((Integer) entity.get(COLUMN_USER_ACCOUNT_STATUS)));
    returnItem.setRole(EUserRole.valueOfCode((Integer) entity.get(COLUMN_USER_ACCOUNT_ROLE)));

    if (entity.get(COLUMN_USER_ACCOUNT_CREATE_DATETIME) instanceof java.sql.Timestamp timestamp) {
      returnItem.setCreateDateTime(timestamp.toInstant().atZone(ZoneId.systemDefault()));
    }

    if (entity.get(COLUMN_USER_ACCOUNT_UPDATE_DATETIME) instanceof java.sql.Timestamp timestamp) {
      returnItem.setUpdateDateTime(timestamp.toInstant().atZone(ZoneId.systemDefault()));
    }

    return returnItem;
  }

  private Map<String, Object> createUserAccountMap(UserAccount userAccount) {
    Map<String, Object> sqlInput = new HashMap<>();
    sqlInput.put(INPUT_USER_ACCOUNT_NAME, userAccount.getName());
    sqlInput.put(
        INPUT_USER_ACCOUNT_PASSWORD, userAccount.getPassword()); // Consider hashing password
    sqlInput.put(INPUT_USER_ACCOUNT_DISPLAY_NAME, userAccount.getDisplayName());
    sqlInput.put(INPUT_USER_ACCOUNT_EMAIL, userAccount.getEmail());
    sqlInput.put(INPUT_USER_ACCOUNT_STATUS, userAccount.getStatus().getCode());
    sqlInput.put(INPUT_USER_ACCOUNT_ROLE, userAccount.getRole().getCode());
    return sqlInput;
  }

  private class UserAccountRowMapper implements RowMapper<UserAccount> {
    private final boolean includePassword;

    public UserAccountRowMapper(boolean includePassword) {
      this.includePassword = includePassword;
    }

    @Override
    public UserAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
      UserAccount user = new UserAccount();
      user.setId(rs.getInt(COLUMN_USER_ACCOUNT_ID));
      user.setName(rs.getString(COLUMN_USER_ACCOUNT_NAME));
      if (includePassword) {
        user.setPassword(rs.getString(COLUMN_USER_ACCOUNT_PASSWORD));
      }
      user.setDisplayName(rs.getString(COLUMN_USER_ACCOUNT_DISPLAY_NAME));
      user.setEmail(rs.getString(COLUMN_USER_ACCOUNT_EMAIL));
      user.setStatus(EUserStatus.valueOfCode(rs.getInt(COLUMN_USER_ACCOUNT_STATUS)));
      user.setRole(EUserRole.valueOfCode(rs.getInt(COLUMN_USER_ACCOUNT_ROLE)));
      user.setCreateDateTime(
          rs.getTimestamp(COLUMN_USER_ACCOUNT_CREATE_DATETIME)
              .toInstant()
              .atZone(ZoneId.systemDefault()));
      user.setUpdateDateTime(
          rs.getTimestamp(COLUMN_USER_ACCOUNT_UPDATE_DATETIME)
              .toInstant()
              .atZone(ZoneId.systemDefault()));
      return user;
    }
  }
}
