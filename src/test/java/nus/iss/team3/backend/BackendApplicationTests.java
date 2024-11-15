package nus.iss.team3.backend;

import nus.iss.team3.backend.controller.UserAccountController;
import nus.iss.team3.backend.domainService.user.IUserAccountService;
import nus.iss.team3.backend.entity.EUserRole;
import nus.iss.team3.backend.entity.EUserStatus;
import nus.iss.team3.backend.entity.UserAccount;
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
    testUser.setStatus(EUserStatus.ACTIVE);
    testUser.setRole(EUserRole.USER);
  }
}
