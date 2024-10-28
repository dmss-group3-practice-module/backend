package nus.iss.team3.backend.businessService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import nus.iss.team3.backend.businessService.auth.AuthService;
import nus.iss.team3.backend.entity.UserAccount;
import nus.iss.team3.backend.domainService.user.IUserAccountService;
import nus.iss.team3.backend.service.util.EncryptionUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class TestAuthService {
  @Spy @InjectMocks private AuthService authService;

  @Mock private IUserAccountService userAccountService;

  private static final String TEST_PASSWORD = "password";
  private static final String TEST_USERNAME = "testuser";
  private String hashedPassword;

  @BeforeEach
  public void setup() {
    hashedPassword = BCrypt.hashpw(TEST_PASSWORD, BCrypt.gensalt());
  }

  @Test
  public void authenticate_ValidCredentials_Success() {
    UserAccount user = new UserAccount();
    user.setName(TEST_USERNAME);

    when(userAccountService.authenticate(TEST_USERNAME, TEST_PASSWORD)).thenReturn(user);

    UserAccount result = authService.authenticate(TEST_USERNAME, TEST_PASSWORD);

    assertNotNull(result, "Authentication result should not be null");
    assertEquals(TEST_USERNAME, result.getName(), "Username should match");
    assertNull(result.getPassword(), "Password should be cleared");

    verify(userAccountService).authenticate(TEST_USERNAME, TEST_PASSWORD);
  }

  @Test
  public void authenticate_InvalidUsername_ReturnsNull() {
    when(userAccountService.authenticate("nonexistent", hashedPassword)).thenReturn(null);

    UserAccount result = authService.authenticate("nonexistent", TEST_PASSWORD);

    assertNull(result);
  }

  @Test
  public void authenticate_InvalidPassword_ReturnsNull() {
    UserAccount user = new UserAccount();
    user.setName(TEST_USERNAME);
    user.setPassword(hashedPassword);

    when(userAccountService.authenticate("nonexistent", hashedPassword)).thenReturn(user);

    UserAccount result = authService.authenticate(TEST_USERNAME, "wrongpassword");

    assertNull(result);
  }

  @Test
  public void authenticate_NullUsername_ThrowsIllegalArgumentException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          authService.authenticate(null, TEST_PASSWORD);
        });
    verify(userAccountService, never()).authenticate(any(), any());
  }

  @Test
  public void authenticate_NullPassword_ThrowsIllegalArgumentException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          authService.authenticate(TEST_USERNAME, null);
        });
    verify(userAccountService, never()).authenticate(any(), any());
  }

  @Test
  public void authenticate_EmptyUsername_ThrowsIllegalArgumentException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          authService.authenticate("", TEST_PASSWORD);
        });
    verify(userAccountService, never()).authenticate(any(), any());
  }

  @Test
  public void authenticate_EmptyPassword_ThrowsIllegalArgumentException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          authService.authenticate(TEST_USERNAME, "");
        });
    verify(userAccountService, never()).authenticate(any(), any());
  }

  @Test
  public void authenticate_DataAccessException_ReturnsNull() {
    when(userAccountService.authenticate(TEST_USERNAME, hashedPassword))
        .thenThrow(new RuntimeException("Database error"));

    UserAccount result = authService.authenticate(TEST_USERNAME, TEST_PASSWORD);

    assertNull(result);
  }

  @Test
  public void hashPassword_ValidInput_ReturnsHashedPassword() {
    String plainPassword = "testPassword";
    String hashedPassword = EncryptionUtilities.hashPassword(plainPassword);

    assertNotNull(hashedPassword);
    assertTrue(BCrypt.checkpw(plainPassword, hashedPassword));
  }
}
