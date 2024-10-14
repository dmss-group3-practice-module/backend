package nus.iss.team3.backend.service;

import nus.iss.team3.backend.dataaccess.IUserAccountDataAccess;
import nus.iss.team3.backend.entity.UserAccount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
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

    UserAccount user = userAccountDataAccess.authenticateUser(username, password);
    if (user != null) {
      logger.info("User authenticated successfully: {}", username);
      return user;
    } else {
      logger.warn("Authentication failed for user: {}", username);
      return null;
    }
  }

  private boolean verifyPassword(String inputPassword, String storedHash) {
    if (inputPassword == null || storedHash == null) {
      logger.error("Input password or stored password is null");
      return false;
    }
      return BCrypt.checkpw(inputPassword, storedHash);
  }

  public String hashPassword(String plainTextPassword) {
    return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
  }
}
