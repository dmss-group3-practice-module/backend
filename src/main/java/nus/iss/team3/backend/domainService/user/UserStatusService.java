package nus.iss.team3.backend.domainService.user;

import nus.iss.team3.backend.dataaccess.UserAccountDataAccess;
import nus.iss.team3.backend.domainService.user.command.*;
import nus.iss.team3.backend.entity.EUserRole;
import nus.iss.team3.backend.entity.EUserStatus;
import nus.iss.team3.backend.entity.UserAccount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserStatusService implements IUserStatusService {
  private final UserAccountDataAccess userAccountDataAccess;
  private final UserStatusCommandInvoker commandInvoker;
  private static final Logger logger = LogManager.getLogger(UserStatusService.class);

  @Autowired
  public UserStatusService(UserAccountDataAccess userAccountDataAccess) {
    this.userAccountDataAccess = userAccountDataAccess;
    this.commandInvoker = new UserStatusCommandInvoker();
  }

  public void banUser(Integer userId) {
    logger.info("Attempting to ban user with ID: {}", userId);
    UserAccount user = userAccountDataAccess.getUserById(userId);
    validateUserAndAdmin(user);

    user.setStatus(EUserStatus.BANNED);

    UserStatusCommand command = new BanUserCommand(user, userAccountDataAccess);
    commandInvoker.executeCommand(command);
    logger.info("Successfully banned user: {}", userId);
  }

  public void unbanUser(Integer userId) {
    logger.info("Attempting to unban user with ID: {}", userId);
    UserAccount user = userAccountDataAccess.getUserById(userId);
    validateUserAndAdmin(user);

    user.setStatus(EUserStatus.ACTIVE);

    UserStatusCommand command = new UnbanUserCommand(user, userAccountDataAccess);
    commandInvoker.executeCommand(command);
    logger.info("Successfully unbanned user: {}", userId);
  }

  public void undoLastStatusChange() {
    logger.info("Attempting to undo last status change");
    commandInvoker.undoLastCommand();
    logger.info("Successfully undid last status change");
  }

  public boolean isUserBanned(Integer userId) {
    UserAccount user = userAccountDataAccess.getUserById(userId);
    if (user == null) {
      throw new IllegalArgumentException("User not found");
    }
    return user.getStatus() == EUserStatus.BANNED;
  }

  private void validateUserAndAdmin(UserAccount user) {
    if (user == null) {
      logger.error("Attempted to modify status of non-existent user");
      throw new IllegalArgumentException("User not found");
    }
    if (user.getRole() == EUserRole.ADMIN) {
      logger.error("Attempted to modify status of admin user: {}", user.getId());
      throw new IllegalStateException("Cannot modify admin user status");
    }
  }
}
