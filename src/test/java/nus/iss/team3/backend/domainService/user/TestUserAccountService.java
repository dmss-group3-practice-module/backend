/* (C)2024 */
package nus.iss.team3.backend.domainService.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import nus.iss.team3.backend.dataaccess.IUserAccountDataAccess;
import nus.iss.team3.backend.entity.EUserAccountStatus;
import nus.iss.team3.backend.entity.EUserRole;
import nus.iss.team3.backend.entity.UserAccount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class TestUserAccountService {

  @InjectMocks private UserAccountService userAccountService;

  @Mock private IUserAccountDataAccess userAccountDataAccess;

  @Test
  public void addUser_validAccount_success() {
    UserAccount inputUserAccount = createValidUserAccount();

    when(userAccountDataAccess.getUserByName(anyString())).thenReturn(null);
    when(userAccountDataAccess.getUserByEmail(anyString())).thenReturn(null);
    when(userAccountDataAccess.addUser(any())).thenReturn(true);

    assertTrue(userAccountService.addUser(inputUserAccount));
  }

  @Test
  public void addUser_nullAccount() {
    UserAccount inputUserAccount = null;

    assertThrows(
        IllegalArgumentException.class, () -> userAccountService.addUser(inputUserAccount));
  }

  @Test
  public void addUser_duplicateUsername_failure() {
    UserAccount inputUserAccount = createValidUserAccount();

    UserAccount existingUser = new UserAccount();
    existingUser.setId(1); // Set a non-null ID
    when(userAccountDataAccess.getUserByName(anyString())).thenReturn(existingUser);
    when(userAccountDataAccess.getUserByEmail(anyString())).thenReturn(null);

    assertFalse(userAccountService.addUser(inputUserAccount));
  }

  @Test
  public void addUser_duplicateEmail_failure() {
    UserAccount inputUserAccount = createValidUserAccount();

    UserAccount existingUser = new UserAccount();
    existingUser.setId(1); // Set a non-null ID
    when(userAccountDataAccess.getUserByName(anyString())).thenReturn(null);
    when(userAccountDataAccess.getUserByEmail(anyString())).thenReturn(existingUser);

    assertFalse(userAccountService.addUser(inputUserAccount));
  }

  @Test
  public void addUser_invalidEmail_failure() {
    UserAccount inputUserAccount = createValidUserAccount();
    inputUserAccount.setEmail("invalid-email");
    assertThrows(
        IllegalArgumentException.class, () -> userAccountService.addUser(inputUserAccount));
  }

  @Test
  public void addUser_validAccount_nullName() {
    UserAccount inputUserAccount = new UserAccount();
    inputUserAccount.setName(null);

    assertThrows(
        IllegalArgumentException.class, () -> userAccountService.addUser(inputUserAccount));
  }

  @Test
  public void addUser_validAccount_emptyName() {
    UserAccount inputUserAccount = new UserAccount();
    inputUserAccount.setName("");

    assertThrows(
        IllegalArgumentException.class, () -> userAccountService.addUser(inputUserAccount));
  }

  @Test
  public void addUser_validAccount_validName_nullPassword() {
    UserAccount inputUserAccount = createValidUserAccount();
    inputUserAccount.setPassword(null);

    assertThrows(
        IllegalArgumentException.class, () -> userAccountService.addUser(inputUserAccount));
  }

  @Test
  public void addUser_validAccount_validName_emptyPassword() {
    UserAccount inputUserAccount = createValidUserAccount();
    inputUserAccount.setPassword("");

    assertThrows(
        IllegalArgumentException.class, () -> userAccountService.addUser(inputUserAccount));
  }

  @Test
  public void addUser_validAccount_validName_validPassword_nullEmail() {
    UserAccount inputUserAccount = createValidUserAccount();
    inputUserAccount.setEmail(null);

    assertThrows(
        IllegalArgumentException.class, () -> userAccountService.addUser(inputUserAccount));
  }

  @Test
  public void addUser_validAccount_validName_validPassword_emptyEmail() {
    UserAccount inputUserAccount = createValidUserAccount();
    inputUserAccount.setEmail("");

    assertThrows(
        IllegalArgumentException.class, () -> userAccountService.addUser(inputUserAccount));
  }

  @Test
  public void updateUser_validAccount_success() {
    UserAccount inputUserAccount = createValidUserAccount();
    inputUserAccount.setId(1);

    when(userAccountDataAccess.getUserById(1)).thenReturn(inputUserAccount);
    when(userAccountDataAccess.getUserByName(anyString())).thenReturn(null);
    when(userAccountDataAccess.getUserByEmail(anyString())).thenReturn(null);
    when(userAccountDataAccess.updateUser(any())).thenReturn(true);

    assertTrue(userAccountService.updateUser(inputUserAccount));
  }

  @Test
  public void updateUser_nullAccount() {
    UserAccount inputUserAccount = null;

    assertThrows(
        IllegalArgumentException.class, () -> userAccountService.updateUser(inputUserAccount));
  }

  @Test
  public void updateUser_missingId() {
    UserAccount inputUserAccount = createValidUserAccount();
    inputUserAccount.setId(null);

    assertThrows(
        IllegalArgumentException.class, () -> userAccountService.updateUser(inputUserAccount));
  }

  @Test
  public void updateUser_nonExistingAccount() {
    UserAccount inputUserAccount = createValidUserAccount();
    inputUserAccount.setId(1);

    when(userAccountDataAccess.getUserById(1)).thenReturn(null);

    assertFalse(userAccountService.updateUser(inputUserAccount));
  }

  @Test
  public void updateUser_duplicateUsername_failure() {
    UserAccount inputUserAccount = createValidUserAccount();
    inputUserAccount.setId(1);

    UserAccount existingUser = new UserAccount();
    existingUser.setId(2);

    when(userAccountDataAccess.getUserById(1)).thenReturn(inputUserAccount);
    when(userAccountDataAccess.getUserByName(anyString())).thenReturn(existingUser);

    assertFalse(userAccountService.updateUser(inputUserAccount));
  }

  @Test
  public void updateUser_duplicateEmail_failure() {
    UserAccount inputUserAccount = createValidUserAccount();
    inputUserAccount.setId(1);

    UserAccount existingUser = new UserAccount();
    existingUser.setId(2);

    when(userAccountDataAccess.getUserByEmail("valid@example.com")).thenReturn(inputUserAccount);
    when(userAccountDataAccess.getUserByEmail(anyString())).thenReturn(existingUser);

    assertFalse(userAccountService.updateUser(inputUserAccount));
  }

  @Test
  public void deleteUserById_nonExistingUser() {
    Integer userId = 1;
    when(userAccountDataAccess.getUserById(userId)).thenReturn(null);

    assertFalse(userAccountService.deleteUserById(userId));
  }

  @Test
  public void deleteUserById_success() {
    Integer userId = 1;

    when(userAccountDataAccess.deleteUserById(userId)).thenReturn(true);

    assertTrue(userAccountService.deleteUserById(userId));
  }

  @Test
  public void deleteUserById_failure() {
    Integer userId = 1;
    when(userAccountDataAccess.deleteUserById(userId)).thenReturn(false);
    assertFalse(userAccountService.deleteUserById(userId));
  }

  @Test
  public void deleteUserById_nullId() {
    assertThrows(IllegalArgumentException.class, () -> userAccountService.deleteUserById(null));
  }

  @Test
  public void getUserById_nonExistingUser() {
    Integer userId = 1;
    when(userAccountDataAccess.getUserById(userId)).thenReturn(null);

    assertNull(userAccountService.getUserById(userId));
  }

  @Test
  public void getUserById_existingUser() {
    Integer userId = 1;
    UserAccount existingAccount = new UserAccount();
    existingAccount.setId(userId);

    when(userAccountDataAccess.getUserById(userId)).thenReturn(existingAccount);

    assertEquals(existingAccount, userAccountService.getUserById(userId));
  }

  @Test
  public void getUserById_nullId() {
    assertThrows(IllegalArgumentException.class, () -> userAccountService.getUserById(null));
  }

  @Test
  public void getAllUser_emptyList() {
    when(userAccountDataAccess.getAllUsers()).thenReturn(Arrays.asList());

    List<UserAccount> result = userAccountService.getAllUsers();
    assertTrue(result.isEmpty());
  }

  @Test
  public void getAllUser_success() {
    UserAccount user1 = new UserAccount();
    UserAccount user2 = new UserAccount();
    List<UserAccount> userList = Arrays.asList(user1, user2);

    when(userAccountDataAccess.getAllUsers()).thenReturn(userList);

    List<UserAccount> result = userAccountService.getAllUsers();
    assertEquals(2, result.size());
    assertEquals(userList, result);
  }

  private UserAccount createValidUserAccount() {
    UserAccount userAccount = new UserAccount();
    userAccount.setName("valid");
    userAccount.setPassword("valid");
    userAccount.setDisplayName("Valid User");
    userAccount.setEmail("valid@example.com");
    userAccount.setStatus(EUserAccountStatus.ACTIVE);
    userAccount.setRole(EUserRole.USER);
    return userAccount;
  }
}
