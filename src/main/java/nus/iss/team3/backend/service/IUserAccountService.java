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

  public boolean addUser(UserAccount userAccount);

  public boolean updateUser(UserAccount userAccount);

  public boolean deleteUserById(String userName);

  public UserAccount getUserById(String userName);

  public List<UserAccount> getAllUser();
}
