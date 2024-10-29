/* (C)2024 */
package nus.iss.team3.backend.domainService.user;

import java.util.List;
import nus.iss.team3.backend.entity.UserAccount;

/**
 * Interface for userAccountService, contains logic involving userAccounts.
 *
 * @author Desmond Tan Zhi Heng
 */
public interface IUserAccountService {

  boolean addUser(UserAccount userAccount);

  boolean updateUser(UserAccount userAccount);

  boolean deleteUserById(Integer id);

  UserAccount getUserById(Integer id);

  List<UserAccount> getAllUsers();

  UserAccount authenticate(String username, String password);
}
