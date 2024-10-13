package nus.iss.team3.backend.service;

import nus.iss.team3.backend.dataaccess.IUserAccountDataAccess;
import nus.iss.team3.backend.entity.UserAccount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {

  private static final Logger logger = LogManager.getLogger(AuthService.class);

  @Autowired private IUserAccountDataAccess userAccountDataAccess;

  @Override
  public UserAccount authenticate(String username, String password) {
    logger.debug("Attempting to authenticate user: {}", username);
    if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
      logger.warn("Username or password is null or empty");
      return null;
    }
    try {
      UserAccount user = userAccountDataAccess.getUserByNameForAuth(username);
      if (user != null) {
        logger.debug("User found in database: {}", username);
        if (user.getPassword() == null) {
          logger.error("Stored password is null for user: {}", username);
          return null;
        }
        if (verifyPassword(password, user.getPassword())) {
          logger.info("Password verified successfully for user: {}", username);
          user.setPassword(null);
          return user;
        } else {
          logger.warn("Password verification failed for user: {}", username);
        }
      } else {
        logger.warn("User not found in database: {}", username);
      }
    } catch (Exception e) {
      logger.error("Error occurred while authenticating user: {}", username, e);
    }
    return null;
  }

  private boolean verifyPassword(String inputPassword, String storedPassword) {
    if (inputPassword == null || storedPassword == null) {
      logger.error("Input password or stored password is null");
      return false;
    }
    logger.debug(
        "Verifying password. Input length: {}, Stored length: {}",
        inputPassword.length(),
        storedPassword.length());
    // TODO: Implement proper password hashing and verification
    boolean result = inputPassword.equals(storedPassword);
    logger.debug("Password verification result: {}", result);
    return result;
  }
}
