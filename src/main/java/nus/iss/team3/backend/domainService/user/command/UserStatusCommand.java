package nus.iss.team3.backend.domainService.user.command;

public interface UserStatusCommand {
  void execute();

  void undo();
}
