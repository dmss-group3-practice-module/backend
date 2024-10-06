/* (C)2024 */
package nus.iss.team3.backend.service;

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
  public void addUser_nullAccount() {
    UserAccount inputUserAccount = null;
    boolean addUserResult = false;

    when(userAccountDataAccess.addUser(any())).thenReturn(addUserResult);

    assertEquals(addUserResult, userAccountService.addUser(inputUserAccount));
  }

  @Test
  public void addUser_validAccount_nullName() {
    UserAccount inputUserAccount = new UserAccount();
    inputUserAccount.setName(null);
    boolean addUserResult = false;

    when(userAccountDataAccess.addUser(any())).thenReturn(addUserResult);

    assertEquals(addUserResult, userAccountService.addUser(inputUserAccount));
  }

  @Test
  public void addUser_validAccount_emptyName() {
    UserAccount inputUserAccount = new UserAccount();
    inputUserAccount.setName("");
    boolean addUserResult = false;

    when(userAccountDataAccess.addUser(any())).thenReturn(addUserResult);

    assertEquals(addUserResult, userAccountService.addUser(inputUserAccount));
  }

  @Test
  public void addUser_validAccount_validName_nullPassword() {
    UserAccount inputUserAccount = new UserAccount();
    inputUserAccount.setName("valid");
    inputUserAccount.setPassword(null);
    boolean addUserResult = false;

    when(userAccountDataAccess.addUser(any())).thenReturn(addUserResult);

    assertEquals(addUserResult, userAccountService.addUser(inputUserAccount));
  }

  @Test
  public void addUser_validAccount_validName_emptyPassword() {
    UserAccount inputUserAccount = new UserAccount();
    inputUserAccount.setName("valid");
    inputUserAccount.setPassword("");
    boolean addUserResult = false;

    when(userAccountDataAccess.addUser(any())).thenReturn(addUserResult);

    assertEquals(addUserResult, userAccountService.addUser(inputUserAccount));
  }

  @Test
  public void addUser_validAccount_validName_validPassword_nullEmail() {
    UserAccount inputUserAccount = new UserAccount();
    inputUserAccount.setName("valid");
    inputUserAccount.setPassword("valid");
    inputUserAccount.setEmail(null);
    boolean addUserResult = false;

    when(userAccountDataAccess.addUser(any())).thenReturn(addUserResult);

    assertEquals(addUserResult, userAccountService.addUser(inputUserAccount));
  }

  @Test
  public void addUser_validAccount_validName_validPassword_emptyEmail() {
    UserAccount inputUserAccount = new UserAccount();
    inputUserAccount.setName("valid");
    inputUserAccount.setPassword("valid");
    inputUserAccount.setEmail("");
    boolean addUserResult = false;

    when(userAccountDataAccess.addUser(any())).thenReturn(addUserResult);

    assertEquals(addUserResult, userAccountService.addUser(inputUserAccount));
  }

  @Test
  public void addUser_validAccount_allFieldsValid() {
    UserAccount inputUserAccount = new UserAccount();
    inputUserAccount.setName("valid");
    inputUserAccount.setPassword("valid");
    inputUserAccount.setDisplayName("Valid User");
    inputUserAccount.setEmail("valid@example.com");
    inputUserAccount.setStatus(EUserAccountStatus.ACTIVE);
    inputUserAccount.setRole(EUserRole.USER);
    boolean addUserResult = true;

    when(userAccountDataAccess.getUserByEmail(anyString())).thenReturn(null);
    when(userAccountDataAccess.addUser(any())).thenReturn(addUserResult);

    assertEquals(addUserResult, userAccountService.addUser(inputUserAccount));
  }

  @Test
  public void addUser_validAccount_duplicateEmail() {
    UserAccount inputUserAccount = new UserAccount();
    inputUserAccount.setName("valid");
    inputUserAccount.setPassword("valid");
    inputUserAccount.setDisplayName("Valid User");
    inputUserAccount.setEmail("existing@example.com");
    inputUserAccount.setStatus(EUserAccountStatus.ACTIVE);
    inputUserAccount.setRole(EUserRole.USER);

    UserAccount existingUserAccount = new UserAccount();
    existingUserAccount.setEmail("existing@example.com");

    when(userAccountDataAccess.getUserByEmail("existing@example.com"))
        .thenReturn(existingUserAccount);

    assertFalse(userAccountService.addUser(inputUserAccount));
  }

  @Test
  public void updateUser_nullAccount() {
    UserAccount inputUserAccount = null;
    assertFalse(userAccountService.updateUser(inputUserAccount));
  }

  @Test
  public void updateUser_missingId() {
    UserAccount inputUserAccount = new UserAccount();
    inputUserAccount.setName("valid");
    inputUserAccount.setPassword("valid");
    inputUserAccount.setDisplayName("Valid User");
    inputUserAccount.setEmail("valid@example.com");
    inputUserAccount.setStatus(EUserAccountStatus.ACTIVE);
    inputUserAccount.setRole(EUserRole.USER);

    assertFalse(userAccountService.updateUser(inputUserAccount));
  }

  @Test
  public void updateUser_nonExistingAccount() {
    UserAccount inputUserAccount = new UserAccount();
    inputUserAccount.setId(1);
    inputUserAccount.setName("valid");
    inputUserAccount.setPassword("valid");
    inputUserAccount.setDisplayName("Valid User");
    inputUserAccount.setEmail("valid@example.com");
    inputUserAccount.setStatus(EUserAccountStatus.ACTIVE);
    inputUserAccount.setRole(EUserRole.USER);

    when(userAccountDataAccess.getUserById(1)).thenReturn(null);

    assertFalse(userAccountService.updateUser(inputUserAccount));
  }

  @Test
  public void updateUser_duplicateEmail() {
    UserAccount inputUserAccount = new UserAccount();
    inputUserAccount.setId(1);
    inputUserAccount.setName("valid");
    inputUserAccount.setPassword("valid");
    inputUserAccount.setDisplayName("Valid User");
    inputUserAccount.setEmail("existing@example.com");
    inputUserAccount.setStatus(EUserAccountStatus.ACTIVE);
    inputUserAccount.setRole(EUserRole.USER);

    UserAccount existingAccount = new UserAccount();
    existingAccount.setId(1);

    UserAccount accountWithSameEmail = new UserAccount();
    accountWithSameEmail.setId(2);
    accountWithSameEmail.setEmail("existing@example.com");

    when(userAccountDataAccess.getUserById(1)).thenReturn(existingAccount);
    when(userAccountDataAccess.getUserByEmail("existing@example.com"))
        .thenReturn(accountWithSameEmail);

    assertFalse(userAccountService.updateUser(inputUserAccount));
  }

  @Test
  public void updateUser_success() {
    UserAccount inputUserAccount = new UserAccount();
    inputUserAccount.setId(1);
    inputUserAccount.setName("valid");
    inputUserAccount.setPassword("valid");
    inputUserAccount.setDisplayName("Valid User");
    inputUserAccount.setEmail("valid@example.com");
    inputUserAccount.setStatus(EUserAccountStatus.ACTIVE);
    inputUserAccount.setRole(EUserRole.USER);

    when(userAccountDataAccess.getUserById(1)).thenReturn(inputUserAccount);
    when(userAccountDataAccess.getUserByEmail("valid@example.com")).thenReturn(inputUserAccount);
    when(userAccountDataAccess.updateUser(any())).thenReturn(true);

    assertTrue(userAccountService.updateUser(inputUserAccount));
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
    UserAccount existingAccount = new UserAccount();
    existingAccount.setId(userId);

    when(userAccountDataAccess.getUserById(userId)).thenReturn(existingAccount);
    when(userAccountDataAccess.deleteUserById(userId)).thenReturn(true);

    assertTrue(userAccountService.deleteUserById(userId));
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
  public void getAllUser_emptyList() {
    when(userAccountDataAccess.getAllUsers()).thenReturn(Arrays.asList());

    List<UserAccount> result = userAccountService.getAllUser();
    assertTrue(result.isEmpty());
  }

  @Test
  public void getAllUser_nonEmptyList() {
    UserAccount user1 = new UserAccount();
    UserAccount user2 = new UserAccount();
    List<UserAccount> userList = Arrays.asList(user1, user2);

    when(userAccountDataAccess.getAllUsers()).thenReturn(userList);

    List<UserAccount> result = userAccountService.getAllUser();
    assertEquals(2, result.size());
    assertEquals(userList, result);
  }
}
