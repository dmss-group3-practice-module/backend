package nus.iss.team3.backend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import nus.iss.team3.backend.controller.UserAccountController;
import nus.iss.team3.backend.entity.EUserAccountStatus;
import nus.iss.team3.backend.entity.EUserRole;
import nus.iss.team3.backend.entity.UserAccount;
import nus.iss.team3.backend.service.IUserAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BackendApplicationTests {

  @Mock private IUserAccountService userAccountService;

  @InjectMocks private UserAccountController userAccountController;

  private UserAccount testUser;

  @BeforeEach
  void setUp() {
    testUser = new UserAccount();
    testUser.setId(1);
    testUser.setName("testuser");
    testUser.setPassword("password123");
    testUser.setDisplayName("Test User");
    testUser.setEmail("testuser@example.com");
    testUser.setStatus(EUserAccountStatus.ACTIVE);
    testUser.setRole(EUserRole.USER);
  }
}
