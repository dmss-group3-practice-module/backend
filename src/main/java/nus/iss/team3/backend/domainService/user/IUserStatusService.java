package nus.iss.team3.backend.domainService.user;

public interface IUserStatusService {
  void banUser(Integer userId);

  void unbanUser(Integer userId);

  void undoLastStatusChange();

  boolean isUserBanned(Integer userId);
}
