package nus.iss.team3.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import nus.iss.team3.backend.dataaccess.IUserAccountDataAccess;
import nus.iss.team3.backend.entity.UserAccount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class TestAuthService {

  @InjectMocks private AuthService authService;

  @Mock private IUserAccountDataAccess userAccountDataAccess;

  @Test
  public void authenticate_ValidCredentials_Success() {
    UserAccount user = new UserAccount();
    user.setName("testuser");
    user.setPassword("password");

    when(userAccountDataAccess.getUserByNameForAuth("testuser")).thenReturn(user);

    UserAccount result = authService.authenticate("testuser", "password");

    assertNotNull(result);
    assertEquals("testuser", result.getName());
    assertNull(result.getPassword()); // Password should be cleared
  }

  @Test
  public void authenticate_InvalidUsername_ReturnsNull() {
    when(userAccountDataAccess.getUserByNameForAuth("nonexistent")).thenReturn(null);

    UserAccount result = authService.authenticate("nonexistent", "password");

    assertNull(result);
  }

  @Test
  public void authenticate_InvalidPassword_ReturnsNull() {
    UserAccount user = new UserAccount();
    user.setName("testuser");
    user.setPassword("correctpassword");

    when(userAccountDataAccess.getUserByNameForAuth("testuser")).thenReturn(user);

    UserAccount result = authService.authenticate("testuser", "wrongpassword");

    assertNull(result);
  }

  @Test
  public void authenticate_NullUsername_ReturnsNull() {
    UserAccount result = authService.authenticate(null, "password");

    assertNull(result);
    verify(userAccountDataAccess, never()).getUserByNameForAuth(any());
  }

  @Test
  public void authenticate_NullPassword_ReturnsNull() {
    UserAccount user = new UserAccount();
    user.setName("testuser");
    user.setPassword("password");

    when(userAccountDataAccess.getUserByNameForAuth("testuser")).thenReturn(user);

    UserAccount result = authService.authenticate("testuser", null);

    assertNull(result);
  }

  @Test
  public void authenticate_EmptyUsername_ReturnsNull() {
    UserAccount result = authService.authenticate("", "password");

    assertNull(result);
    verify(userAccountDataAccess, never()).getUserByNameForAuth(any());
  }

  @Test
  public void authenticate_EmptyPassword_ReturnsNull() {
    UserAccount user = new UserAccount();
    user.setName("testuser");
    user.setPassword("password");

    when(userAccountDataAccess.getUserByNameForAuth("testuser")).thenReturn(user);

    UserAccount result = authService.authenticate("testuser", "");

    assertNull(result);
  }

  @Test
  public void authenticate_DataAccessException_ReturnsNull() {
    when(userAccountDataAccess.getUserByNameForAuth("testuser"))
        .thenThrow(new RuntimeException("Database error"));

    UserAccount result = authService.authenticate("testuser", "password");

    assertNull(result);
  }
}
