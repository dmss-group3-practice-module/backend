package nus.iss.team3.backend.dataaccess;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nus.iss.team3.backend.dataaccess.postgres.PostgresDataAccess;
import nus.iss.team3.backend.entity.EUserRole;
import nus.iss.team3.backend.entity.EUserStatus;
import nus.iss.team3.backend.entity.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TestUserAccountDataAccess {

  @Mock private PostgresDataAccess postgresDataAccess;

  @InjectMocks private UserAccountDataAccess userAccessDataAccess;

  String salt = "";

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    salt = BCrypt.gensalt(10);
  }

  @Test
  public void authenticateUser_noAccount() {
    String inputName = "user1";
    String inputPassword = BCrypt.hashpw("password", salt);
    List<Map<String, Object>> userAccountList = null;
    when(postgresDataAccess.queryStatement(eq(PostgresSqlStatement.SQL_AUTHENTICATE_USER), any()))
        .thenReturn(userAccountList);

    UserAccount result = userAccessDataAccess.authenticateUser(inputName, inputPassword);

    assertNull(result);
    verify(postgresDataAccess, times(1))
        .queryStatement(eq(PostgresSqlStatement.SQL_AUTHENTICATE_USER), any());
  }

  @Test
  public void authenticateUser_account_wrongPW() {
    String inputName = "user1";
    String inputPassword = "password";
    List<Map<String, Object>> userAccountList = new ArrayList<>();
    {
      Map<String, Object> userAccount = new HashMap<>();
      userAccount.put(PostgresSqlStatement.COLUMN_USER_ACCOUNT_NAME, "user1");
      userAccount.put(
          PostgresSqlStatement.COLUMN_USER_ACCOUNT_PASSWORD, BCrypt.hashpw("password2", salt));

      userAccountList.add(userAccount);
    }
    when(postgresDataAccess.queryStatement(eq(PostgresSqlStatement.SQL_AUTHENTICATE_USER), any()))
        .thenReturn(userAccountList);

    UserAccount result = userAccessDataAccess.authenticateUser(inputName, inputPassword);

    assertNull(result);
    verify(postgresDataAccess, times(1))
        .queryStatement(eq(PostgresSqlStatement.SQL_AUTHENTICATE_USER), any());
  }

  @Test
  public void authenticateUser_account_nullFirstItem() {
    String inputName = "user1";
    String inputPassword = "password";
    List<Map<String, Object>> userAccountList = new ArrayList<>();
    {
      userAccountList.add(null);
    }
    when(postgresDataAccess.queryStatement(eq(PostgresSqlStatement.SQL_AUTHENTICATE_USER), any()))
        .thenReturn(userAccountList);

    UserAccount result = userAccessDataAccess.authenticateUser(inputName, inputPassword);

    assertNull(result);
    verify(postgresDataAccess, times(1))
        .queryStatement(eq(PostgresSqlStatement.SQL_AUTHENTICATE_USER), any());
  }

  @Test
  public void authenticateUser_account_toomanyItems() {
    String inputName = "user1";
    String inputPassword = "password";
    List<Map<String, Object>> userAccountList = new ArrayList<>();
    {
      Map<String, Object> userAccount = new HashMap<>();
      userAccount.put(PostgresSqlStatement.COLUMN_USER_ACCOUNT_NAME, "user1");
      userAccount.put(
          PostgresSqlStatement.COLUMN_USER_ACCOUNT_PASSWORD, BCrypt.hashpw("password", salt));

      userAccountList.add(userAccount);
    }
    {
      Map<String, Object> userAccount = new HashMap<>();
      userAccount.put(PostgresSqlStatement.COLUMN_USER_ACCOUNT_NAME, "user1");
      userAccount.put(
          PostgresSqlStatement.COLUMN_USER_ACCOUNT_PASSWORD, BCrypt.hashpw("password", salt));

      userAccountList.add(userAccount);
    }
    when(postgresDataAccess.queryStatement(eq(PostgresSqlStatement.SQL_AUTHENTICATE_USER), any()))
        .thenReturn(userAccountList);

    UserAccount result = userAccessDataAccess.authenticateUser(inputName, inputPassword);

    assertNull(result);
    verify(postgresDataAccess, times(1))
        .queryStatement(eq(PostgresSqlStatement.SQL_AUTHENTICATE_USER), any());
  }

  @Test
  public void authenticateUser_account_correct() {
    String inputName = "user1";
    String inputPassword = "password";
    List<Map<String, Object>> userAccountList = new ArrayList<>();
    {
      Map<String, Object> userAccount = new HashMap<>();
      userAccount.put(PostgresSqlStatement.COLUMN_USER_ACCOUNT_NAME, "user1");
      userAccount.put(
          PostgresSqlStatement.COLUMN_USER_ACCOUNT_PASSWORD, BCrypt.hashpw("password", salt));

      userAccountList.add(userAccount);
    }
    when(postgresDataAccess.queryStatement(eq(PostgresSqlStatement.SQL_AUTHENTICATE_USER), any()))
        .thenReturn(userAccountList);

    UserAccount result = userAccessDataAccess.authenticateUser(inputName, inputPassword);

    assertEquals("user1", result.getName());
    assertNull(result.getPassword());
    verify(postgresDataAccess, times(1))
        .queryStatement(eq(PostgresSqlStatement.SQL_AUTHENTICATE_USER), any());
  }

  @Test
  public void addUser_success() {
    UserAccount userAccount = createValidUserAccount();

    when(postgresDataAccess.upsertStatement(eq(PostgresSqlStatement.SQL_USER_ACCOUNT_ADD), any()))
        .thenReturn(1);

    boolean result = userAccessDataAccess.addUser(userAccount);

    assertTrue(result);
    verify(postgresDataAccess, times(1))
        .upsertStatement(eq(PostgresSqlStatement.SQL_USER_ACCOUNT_ADD), any());
  }

  @Test
  public void addUser_failure() {
    UserAccount userAccount = createValidUserAccount();

    when(postgresDataAccess.upsertStatement(eq(PostgresSqlStatement.SQL_USER_ACCOUNT_ADD), any()))
        .thenReturn(0);

    boolean result = userAccessDataAccess.addUser(userAccount);

    assertFalse(result);
    verify(postgresDataAccess, times(1))
        .upsertStatement(eq(PostgresSqlStatement.SQL_USER_ACCOUNT_ADD), any());
  }

  @Test
  public void deleteUserById_success() {
    int userId = 1;

    when(postgresDataAccess.upsertStatement(
            eq(PostgresSqlStatement.SQL_USER_ACCOUNT_DELETE), any()))
        .thenReturn(1);

    boolean result = userAccessDataAccess.deleteUserById(userId);

    assertTrue(result);
    verify(postgresDataAccess, times(1))
        .upsertStatement(eq(PostgresSqlStatement.SQL_USER_ACCOUNT_DELETE), any());
  }

  @Test
  public void deleteUserById_failure() {
    int userId = 1;

    when(postgresDataAccess.upsertStatement(
            eq(PostgresSqlStatement.SQL_USER_ACCOUNT_DELETE), any()))
        .thenReturn(0);

    boolean result = userAccessDataAccess.deleteUserById(userId);

    assertFalse(result);
    verify(postgresDataAccess, times(1))
        .upsertStatement(eq(PostgresSqlStatement.SQL_USER_ACCOUNT_DELETE), any());
  }

  @Test
  public void updateUser_success() {
    UserAccount userAccount = createValidUserAccount();

    when(postgresDataAccess.upsertStatement(
            eq(PostgresSqlStatement.SQL_USER_ACCOUNT_UPDATE), any()))
        .thenReturn(1);

    boolean result = userAccessDataAccess.updateUser(userAccount);

    assertTrue(result);
    verify(postgresDataAccess, times(1))
        .upsertStatement(eq(PostgresSqlStatement.SQL_USER_ACCOUNT_UPDATE), any());
  }

  @Test
  public void updateUser_failure() {
    UserAccount userAccount = createValidUserAccount();

    when(postgresDataAccess.upsertStatement(
            eq(PostgresSqlStatement.SQL_USER_ACCOUNT_UPDATE), any()))
        .thenReturn(0);

    boolean result = userAccessDataAccess.updateUser(userAccount);

    assertFalse(result);
    verify(postgresDataAccess, times(1))
        .upsertStatement(eq(PostgresSqlStatement.SQL_USER_ACCOUNT_UPDATE), any());
  }

  @Test
  public void updateUserStatus_success() {
    int inputUserId = 1;
    EUserStatus inputStatus = EUserStatus.ACTIVE;

    when(postgresDataAccess.upsertStatement(
            eq(PostgresSqlStatement.SQL_USER_ACCOUNT_UPDATE_STATUS), any()))
        .thenReturn(1);

    boolean result = userAccessDataAccess.updateUserStatus(inputUserId, inputStatus);

    assertTrue(result);
    verify(postgresDataAccess, times(1))
        .upsertStatement(eq(PostgresSqlStatement.SQL_USER_ACCOUNT_UPDATE_STATUS), any());
  }

  @Test
  public void updateUserStatus_failure() {
    int inputUserId = 1;
    EUserStatus inputStatus = EUserStatus.ACTIVE;

    when(postgresDataAccess.upsertStatement(
            eq(PostgresSqlStatement.SQL_USER_ACCOUNT_UPDATE_STATUS), any()))
        .thenReturn(0);

    boolean result = userAccessDataAccess.updateUserStatus(inputUserId, inputStatus);

    assertFalse(result);
    verify(postgresDataAccess, times(1))
        .upsertStatement(eq(PostgresSqlStatement.SQL_USER_ACCOUNT_UPDATE_STATUS), any());
  }

  @Test
  public void getUserById_nullReturn() {
    int inputUserId = 1;

    List<Map<String, Object>> entityReturned = null;
    when(postgresDataAccess.queryStatement(
            eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_ID), any()))
        .thenReturn(entityReturned);

    UserAccount result = userAccessDataAccess.getUserById(inputUserId);

    assertNull(result);
    verify(postgresDataAccess, times(1))
        .queryStatement(eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_ID), any());
  }

  @Test
  public void getUserById_failure() {
    int inputUserId = 1;

    List<Map<String, Object>> entityReturned = new ArrayList<>();
    when(postgresDataAccess.queryStatement(
            eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_ID), any()))
        .thenReturn(entityReturned);

    UserAccount result = userAccessDataAccess.getUserById(inputUserId);

    assertNull(result);
    verify(postgresDataAccess, times(1))
        .queryStatement(eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_ID), any());
  }

  @Test
  public void getUserById_gotValue() {
    int inputUserId = 1;

    List<Map<String, Object>> entityReturned = new ArrayList<>();
    {
      Map<String, Object> entity = new HashMap<>();
      entity.put("id", 1);
      entity.put("name", "TestUser");
      entity.put("display_name", "user name");
      entity.put("email", "Email@email.com");
      entity.put("status", EUserStatus.ACTIVE.getCode());
      entity.put("role", EUserRole.USER.getCode());
      entity.put("create_datetime", new Timestamp(2024, 12, 30, 1, 1, 1, 1));
      entity.put("update_datetime", new Timestamp(2024, 12, 30, 1, 1, 1, 1));
      entityReturned.add(entity);
    }
    when(postgresDataAccess.queryStatement(
            eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_ID), any()))
        .thenReturn(entityReturned);

    UserAccount result = userAccessDataAccess.getUserById(inputUserId);

    assertNotNull(result);
    verify(postgresDataAccess, times(1))
        .queryStatement(eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_ID), any());
  }

  @Test
  public void getUserByName_nullReturn() {
    String inputName = "username";

    List<Map<String, Object>> entityReturned = null;
    when(postgresDataAccess.queryStatement(
            eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_NAME), any()))
        .thenReturn(entityReturned);

    UserAccount result = userAccessDataAccess.getUserByName(inputName);

    assertNull(result);
    verify(postgresDataAccess, times(1))
        .queryStatement(eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_NAME), any());
  }

  @Test
  public void getUserByName_failure() {
    String inputName = "username";

    List<Map<String, Object>> entityReturned = new ArrayList<>();
    when(postgresDataAccess.queryStatement(
            eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_NAME), any()))
        .thenReturn(entityReturned);

    UserAccount result = userAccessDataAccess.getUserByName(inputName);

    assertNull(result);
    verify(postgresDataAccess, times(1))
        .queryStatement(eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_NAME), any());
  }

  @Test
  public void getUserByName_gotValue() {
    String inputName = "username";

    List<Map<String, Object>> entityReturned = new ArrayList<>();
    {
      Map<String, Object> entity = new HashMap<>();
      entity.put("id", 1);
      entity.put("name", "TestUser");
      entity.put("display_name", "user name");
      entity.put("email", "Email@email.com");
      entity.put("status", EUserStatus.ACTIVE.getCode());
      entity.put("role", EUserRole.USER.getCode());
      entity.put("create_datetime", new Timestamp(2024, 12, 30, 1, 1, 1, 1));
      entity.put("update_datetime", new Timestamp(2024, 12, 30, 1, 1, 1, 1));
      entityReturned.add(entity);
    }
    when(postgresDataAccess.queryStatement(
            eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_NAME), any()))
        .thenReturn(entityReturned);

    UserAccount result = userAccessDataAccess.getUserByName(inputName);

    assertNotNull(result);
    verify(postgresDataAccess, times(1))
        .queryStatement(eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_NAME), any());
  }

  @Test
  public void getUserByEmail_nullReturn() {
    String inputEmail = "email@input.com";

    List<Map<String, Object>> entityReturned = null;
    when(postgresDataAccess.queryStatement(
            eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_EMAIL), any()))
        .thenReturn(entityReturned);

    UserAccount result = userAccessDataAccess.getUserByEmail(inputEmail);

    assertNull(result);
    verify(postgresDataAccess, times(1))
        .queryStatement(eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_EMAIL), any());
  }

  @Test
  public void getUserByEmail_failure() {
    String inputEmail = "email@input.com";

    List<Map<String, Object>> entityReturned = new ArrayList<>();
    when(postgresDataAccess.queryStatement(
            eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_EMAIL), any()))
        .thenReturn(entityReturned);

    UserAccount result = userAccessDataAccess.getUserByEmail(inputEmail);

    assertNull(result);
    verify(postgresDataAccess, times(1))
        .queryStatement(eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_EMAIL), any());
  }

  @Test
  public void getUserByEmail_gotValue() {
    String inputEmail = "email@input.com";

    List<Map<String, Object>> entityReturned = new ArrayList<>();
    {
      Map<String, Object> entity = new HashMap<>();
      entity.put("id", 1);
      entity.put("name", "TestUser");
      entity.put("display_name", "user name");
      entity.put("email", "Email@email.com");
      entity.put("status", EUserStatus.ACTIVE.getCode());
      entity.put("role", EUserRole.USER.getCode());
      entity.put("create_datetime", new Timestamp(2024, 12, 30, 1, 1, 1, 1));
      entity.put("update_datetime", new Timestamp(2024, 12, 30, 1, 1, 1, 1));
      entityReturned.add(entity);
    }
    when(postgresDataAccess.queryStatement(
            eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_EMAIL), any()))
        .thenReturn(entityReturned);

    UserAccount result = userAccessDataAccess.getUserByEmail(inputEmail);

    assertNotNull(result);
    verify(postgresDataAccess, times(1))
        .queryStatement(eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_EMAIL), any());
  }

  @Test
  public void getUserByEmail_gotValue_testValue() {
    String inputEmail = "email@input.com";

    List<Map<String, Object>> entityReturned = new ArrayList<>();
    {
      Map<String, Object> entity = new HashMap<>();
      entity.put("id", 1);
      entity.put("name", "TestUser");
      entity.put("display_name", "user name");
      entity.put("email", "Email@email.com");
      entity.put("status", EUserStatus.ACTIVE.getCode());
      entity.put("role", EUserRole.USER.getCode());
      entity.put("create_datetime2", new Timestamp(2024, 12, 30, 1, 1, 1, 1));
      entity.put("update_datetime2", new Timestamp(2024, 12, 30, 1, 1, 1, 1));
      entityReturned.add(entity);
    }
    when(postgresDataAccess.queryStatement(
            eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_EMAIL), any()))
        .thenReturn(entityReturned);

    UserAccount result = userAccessDataAccess.getUserByEmail(inputEmail);

    assertNotNull(result);
    verify(postgresDataAccess, times(1))
        .queryStatement(eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_BY_EMAIL), any());
  }

  @Test
  public void getAllUsers_nullList() {
    List<Map<String, Object>> userAccountList = null;
    when(postgresDataAccess.queryStatement(
            eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_ALL), any()))
        .thenReturn(userAccountList);

    List<UserAccount> result = userAccessDataAccess.getAllUsers();

    assertEquals(result.size(), 0);
    verify(postgresDataAccess, times(1))
        .queryStatement(eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_ALL), any());
  }

  @Test
  public void getAllUsers_emptyList() {
    List<Map<String, Object>> userAccountList = new ArrayList<>();
    when(postgresDataAccess.queryStatement(
            eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_ALL), any()))
        .thenReturn(userAccountList);

    List<UserAccount> result = userAccessDataAccess.getAllUsers();

    assertEquals(result.size(), 0);
    verify(postgresDataAccess, times(1))
        .queryStatement(eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_ALL), any());
  }

  @Test
  public void getAllUsers_populatedList() {

    List<Map<String, Object>> userAccountList = new ArrayList<>();
    {
      Map<String, Object> userAccount = new HashMap<>();
      userAccount.put("id", 1);
      userAccount.put("name", "Test User");
      userAccount.put("display_name", "Test Display Name");
      userAccount.put("email", "email");
      userAccountList.add(userAccount);
    }
    when(postgresDataAccess.queryStatement(
            eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_ALL), any()))
        .thenReturn(userAccountList);

    List<UserAccount> result = userAccessDataAccess.getAllUsers();

    assertEquals(result.size(), 1);
    verify(postgresDataAccess, times(1))
        .queryStatement(eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_ALL), any());
  }

  @Test
  public void getAllUsers_populated2List() {

    List<Map<String, Object>> userAccountList = new ArrayList<>();
    {
      Map<String, Object> userAccount = new HashMap<>();
      userAccount.put("id", 1);
      userAccount.put("name", "Test User");
      userAccount.put("display_name", "Test Display Name");
      userAccount.put("email", "email");
      userAccountList.add(userAccount);
    }
    {
      Map<String, Object> userAccount = new HashMap<>();
      userAccount.put("id", 1);
      userAccount.put("name", "Test User");
      userAccount.put("display_name", "Test Display Name");
      userAccount.put("email", "email");
      userAccountList.add(userAccount);
    }
    {
      Map<String, Object> userAccount = new HashMap<>();
      userAccount.put("id", 1);
      userAccount.put("name", "Test User");
      userAccount.put("display_name", "Test Display Name");
      userAccount.put("email", "email");
      userAccountList.add(userAccount);
    }
    when(postgresDataAccess.queryStatement(
            eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_ALL), any()))
        .thenReturn(userAccountList);

    List<UserAccount> result = userAccessDataAccess.getAllUsers();

    assertEquals(result.size(), 3);
    verify(postgresDataAccess, times(1))
        .queryStatement(eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_ALL), any());
  }

  @Test
  public void getAllUsers_populatedList_nullName() {

    List<Map<String, Object>> userAccountList = new ArrayList<>();
    {
      Map<String, Object> userAccount = new HashMap<>();
      userAccount.put("id", 1);
      userAccount.put("name", null);
      userAccount.put("display_name", "Test Display Name");
      userAccount.put("email", "email");
      userAccountList.add(userAccount);
    }
    when(postgresDataAccess.queryStatement(
            eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_ALL), any()))
        .thenReturn(userAccountList);

    List<UserAccount> result = userAccessDataAccess.getAllUsers();

    assertEquals(result.size(), 1);
    verify(postgresDataAccess, times(1))
        .queryStatement(eq(PostgresSqlStatement.SQL_USER_ACCOUNT_GET_ALL), any());
  }

  private UserAccount createValidUserAccount() {
    UserAccount userAccount = new UserAccount();
    userAccount.setName("Test User");
    userAccount.setPassword("password");
    userAccount.setDisplayName("Test Display Name");
    userAccount.setEmail("test@example.com");
    userAccount.setStatus(EUserStatus.ACTIVE);
    userAccount.setRole(EUserRole.USER);
    return userAccount;
  }
}
