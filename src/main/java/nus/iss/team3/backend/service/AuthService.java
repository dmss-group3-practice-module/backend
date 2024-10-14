package nus.iss.team3.backend.service;

import nus.iss.team3.backend.dataaccess.IUserAccountDataAccess;
import nus.iss.team3.backend.entity.UserAccount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {

  private static final Logger logger = LogManager.getLogger(AuthService.class);

  @Autowired private IUserAccountDataAccess userAccountDataAccess;

  @Override
  public UserAccount authenticate(String username, String password)
      throws IllegalArgumentException {
    logger.debug("Attempting to authenticate user: {}", username);
    if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
      logger.warn("Username or password is null or empty");
      throw new IllegalArgumentException("Username and password cannot be empty");
    }

    try {
      UserAccount user = userAccountDataAccess.authenticateUser(username, password);
      if (user != null) {
        logger.info("User authenticated successfully: {}", username);
        return user;
      } else {
        logger.warn("Authentication failed for user: {}", username);
      }
    } catch (DataAccessException e) {
      logger.error("Database error occurred while authenticating user: {}", username, e);
    } catch (Exception e) {
      logger.error("Unexpected error occurred while authenticating user: {}", username, e);
    }
    return null;
  }

  private boolean verifyPassword(String inputPassword, String storedHash) {
    if (inputPassword == null || storedHash == null) {
      logger.error("Input password or stored password is null");
      return false;
    }
    logger.debug("Stored hash: " + storedHash);
    boolean result = BCrypt.checkpw(inputPassword, storedHash);
    logger.debug("Password verification result: " + result);
    return result;
  }

  public String hashPassword(String plainTextPassword) {
    return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
  }
}
