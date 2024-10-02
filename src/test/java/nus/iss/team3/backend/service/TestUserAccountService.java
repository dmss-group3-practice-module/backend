/* (C)2024 */
package nus.iss.team3.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import nus.iss.team3.backend.dataaccess.IUserAccountDataAccess;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class TestUserAccountService {

  @InjectMocks private UserAccountService userAccountService;

  @Mock private IUserAccountDataAccess userAccountDataAccess;

  /*@Test
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
    inputUserAccount.setRole(UserAccount.EUserRole.USER);
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
    inputUserAccount.setRole(UserAccount.EUserRole.USER);

    UserAccount existingUserAccount = new UserAccount();
    existingUserAccount.setEmail("existing@example.com");

    when(userAccountDataAccess.getUserByEmail("existing@example.com"))
        .thenReturn(existingUserAccount);

    assertFalse(userAccountService.addUser(inputUserAccount));
  }*/
}
