package nus.iss.team3.backend.service;

import nus.iss.team3.backend.entity.UserAccount;

/** Interface for authentication service */
public interface IAuthService {

  /**
   * Authenticates a user based on username and password
   *
   * @param username The username of the user trying to authenticate
   * @param password The password of the user trying to authenticate
   * @return UserAccount if authentication is successful, null otherwise
   */
  UserAccount authenticate(String username, String password);

  // You might want to add more methods in the future, such as:
  // void logout(String username);
  // boolean changePassword(String username, String oldPassword, String newPassword);
  // UserAccount register(UserAccount newUser);
}
