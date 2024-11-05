package nus.iss.team3.backend.domainService.user;

import java.util.Stack;
import nus.iss.team3.backend.domainService.user.command.UserStatusCommand;

public class UserStatusCommandInvoker {
  private final Stack<UserStatusCommand> commandHistory = new Stack<>();

  public void executeCommand(UserStatusCommand command) {
    command.execute();
    commandHistory.push(command);
  }

  public void undoLastCommand() {
    if (!commandHistory.isEmpty()) {
      UserStatusCommand command = commandHistory.pop();
      command.undo();
    }
  }
}
