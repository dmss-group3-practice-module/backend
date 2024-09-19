/* (C)2024 */
package nus.iss.team3.backend.dataaccess;

import java.util.List;
import nus.iss.team3.backend.entity.UserAccount;

/**
 * Interface class for UserAccountDataAccess, should contains all functionality needed for
 * useraccount.
 *
 * @author Desmond Tan Zhi Heng
 */
public interface IUserAccountDataAccess {

  public boolean addUser(UserAccount userAccount);

  public boolean updateUser(UserAccount userAccount);

  public boolean deleteUserById(String userId);

  public UserAccount getUserById(String userId);

  public UserAccount getUserByEmail(String email);

  public List<UserAccount> getAllUsers();
}
