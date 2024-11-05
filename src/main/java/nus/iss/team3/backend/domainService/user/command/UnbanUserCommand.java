package nus.iss.team3.backend.domainService.user.command;

import nus.iss.team3.backend.dataaccess.UserAccountDataAccess;
import nus.iss.team3.backend.entity.EUserStatus;
import nus.iss.team3.backend.entity.UserAccount;

public class UnbanUserCommand implements UserStatusCommand {
  private final UserAccount userAccount;
  private final UserAccountDataAccess userAccountDataAccess;
  private final EUserStatus previousStatus;

  public UnbanUserCommand(UserAccount userAccount, UserAccountDataAccess userAccountDataAccess) {
    this.userAccount = userAccount;
    this.userAccountDataAccess = userAccountDataAccess;
    this.previousStatus = userAccount.getStatus();
  }

  @Override
  public void execute() {
    userAccountDataAccess.updateUserStatus(userAccount.getId(), EUserStatus.ACTIVE);
  }

  @Override
  public void undo() {
    userAccountDataAccess.updateUserStatus(userAccount.getId(), previousStatus);
  }
}
