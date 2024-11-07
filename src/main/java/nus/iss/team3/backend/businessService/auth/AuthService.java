package nus.iss.team3.backend.businessService.auth;

import jakarta.annotation.PostConstruct;
import nus.iss.team3.backend.domainService.user.IUserAccountService;
import nus.iss.team3.backend.entity.EUserStatus;
import nus.iss.team3.backend.entity.UserAccount;
import nus.iss.team3.backend.service.util.UserBannedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for user authentication and password management.
 *
 * @author REN JIARUI
 */
@Service
public class AuthService implements IAuthService {

  private static final Logger logger = LogManager.getLogger(AuthService.class);

  @Autowired private IUserAccountService userAccountService;

  @PostConstruct
  public void postConstruct() {
    logger.info("Authenticate Service Logic initialized.");
  }

  @Override
  public UserAccount authenticate(String username, String password)
      throws IllegalArgumentException {
    logger.debug("Attempting to authenticate user: {}", username);

    // Validate input parameters
    if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
      logger.warn("Username or password is null or empty");
      throw new IllegalArgumentException("Username and password cannot be empty");
    }

    // Authenticate user credentials
    UserAccount user = userAccountService.authenticate(username, password);
    if (user == null) {
      logger.warn("Authentication failed for user: {}", username);
      return null;
    }

    // Check for BANNED status
    if (user.getStatus() == EUserStatus.BANNED) {
      logger.warn("Login attempt by banned user: {}", username);
      throw new UserBannedException("Your account has been banned. Please contact administrator.");
    }

    logger.info("User authenticated successfully: {}", username);
    return user;
  }
}
