package nus.iss.team3.backend.domainService.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import nus.iss.team3.backend.dataaccess.UserAccountDataAccess;
import nus.iss.team3.backend.entity.EUserRole;
import nus.iss.team3.backend.entity.EUserStatus;
import nus.iss.team3.backend.entity.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TestUserStatusService {

  @Mock private UserAccountDataAccess userAccountDataAccess;

  @InjectMocks private UserStatusService userStatusService;

  private UserAccount normalUser;
  private UserAccount adminUser;
  private UserAccount bannedUser;

  @BeforeEach
  void setUp() {
    // Set up a normal user
    normalUser = new UserAccount();
    normalUser.setId(1);
    normalUser.setName("Normal User");
    normalUser.setRole(EUserRole.USER);
    normalUser.setStatus(EUserStatus.ACTIVE);

    // Set up an admin user
    adminUser = new UserAccount();
    adminUser.setId(2);
    adminUser.setName("Admin User");
    adminUser.setRole(EUserRole.ADMIN);
    adminUser.setStatus(EUserStatus.ACTIVE);

    // Set up a banned user
    bannedUser = new UserAccount();
    bannedUser.setId(3);
    bannedUser.setName("Banned User");
    bannedUser.setRole(EUserRole.USER);
    bannedUser.setStatus(EUserStatus.BANNED);
  }

  @Test
  void testBanUser_Success() {
    // Arrange
    when(userAccountDataAccess.getUserById(1)).thenReturn(normalUser);
    when(userAccountDataAccess.updateUserStatus(1, EUserStatus.BANNED)).thenReturn(true);

    // Act & Assert
    assertDoesNotThrow(() -> userStatusService.banUser(1));

    // Verify
    verify(userAccountDataAccess).getUserById(1);
    verify(userAccountDataAccess).updateUserStatus(1, EUserStatus.BANNED);
  }

  @Test
  void testBanUser_AdminUser() {
    // Arrange
    when(userAccountDataAccess.getUserById(2)).thenReturn(adminUser);

    // Act & Assert
    IllegalStateException exception =
        assertThrows(IllegalStateException.class, () -> userStatusService.banUser(2));
    assertEquals("Cannot modify admin user status", exception.getMessage());

    // Verify
    verify(userAccountDataAccess).getUserById(2);
    verify(userAccountDataAccess, never()).updateUserStatus(anyInt(), any(EUserStatus.class));
  }

  @Test
  void testBanUser_UserNotFound() {
    // Arrange
    when(userAccountDataAccess.getUserById(999)).thenReturn(null);

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> userStatusService.banUser(999));
    assertEquals("User not found", exception.getMessage());

    // Verify
    verify(userAccountDataAccess).getUserById(999);
    verify(userAccountDataAccess, never()).updateUser(any(UserAccount.class));
  }

  @Test
  void testUnbanUser_Success() {
    // Arrange
    when(userAccountDataAccess.getUserById(3)).thenReturn(bannedUser);
    when(userAccountDataAccess.updateUserStatus(3, EUserStatus.ACTIVE)).thenReturn(true);

    // Act & Assert
    assertDoesNotThrow(() -> userStatusService.unbanUser(3));

    // Verify
    verify(userAccountDataAccess).getUserById(3);
    verify(userAccountDataAccess).updateUserStatus(3, EUserStatus.ACTIVE);
  }

  @Test
  void testUnbanUser_AdminUser() {
    // Arrange
    when(userAccountDataAccess.getUserById(2)).thenReturn(adminUser);

    // Act & Assert
    IllegalStateException exception =
        assertThrows(IllegalStateException.class, () -> userStatusService.unbanUser(2));
    assertEquals("Cannot modify admin user status", exception.getMessage());

    // Verify
    verify(userAccountDataAccess).getUserById(2);
    verify(userAccountDataAccess, never()).updateUserStatus(anyInt(), any(EUserStatus.class));
  }

  @Test
  void testIsUserBanned_BannedUser() {
    // Arrange
    when(userAccountDataAccess.getUserById(3)).thenReturn(bannedUser);

    // Act
    boolean result = userStatusService.isUserBanned(3);

    // Assert
    assertTrue(result);
    verify(userAccountDataAccess).getUserById(3);
  }

  @Test
  void testIsUserBanned_ActiveUser() {
    // Arrange
    when(userAccountDataAccess.getUserById(1)).thenReturn(normalUser);

    // Act
    boolean result = userStatusService.isUserBanned(1);

    // Assert
    assertFalse(result);
    verify(userAccountDataAccess).getUserById(1);
  }

  @Test
  void testIsUserBanned_UserNotFound() {
    // Arrange
    when(userAccountDataAccess.getUserById(999)).thenReturn(null);

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> userStatusService.isUserBanned(999));
    assertEquals("User not found", exception.getMessage());

    // Verify
    verify(userAccountDataAccess).getUserById(999);
  }

  @Test
  void testUndoLastStatusChange() {
    // Act & Assert
    assertDoesNotThrow(() -> userStatusService.undoLastStatusChange());
  }
}
