package nus.iss.team3.backend.businessService.auth;

import nus.iss.team3.backend.entity.UserAccount;

/**
 * Interface for authentication service
 *
 * @author REN JIARUI
 */
public interface IAuthBusinessService {

  /**
   * Authenticates a user based on username and password
   *
   * @param username The username of the user trying to authenticate
   * @param password The password of the user trying to authenticate
   * @return UserAccount if authentication is successful, null otherwise
   */
  UserAccount authenticate(String username, String password);
}
