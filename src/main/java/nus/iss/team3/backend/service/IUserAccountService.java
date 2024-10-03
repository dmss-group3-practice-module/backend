/* (C)2024 */
package nus.iss.team3.backend.service;

import java.util.List;
import nus.iss.team3.backend.entity.UserAccount;

/**
 * Interface for userAccountService, contains logic involving userAccounts.
 *
 * @author Desmond Tan Zhi Heng
 */
public interface IUserAccountService {

  boolean addUser(UserAccount userAccount);

  boolean deleteUserById(Long id);

  boolean updateUser(UserAccount userAccount);

  UserAccount getUserById(Long id);

  List<UserAccount> getAllUser();
}
