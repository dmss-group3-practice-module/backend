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

  boolean addUser(UserAccount userAccount);

  boolean updateUser(UserAccount userAccount);

  boolean deleteUserById(Integer id);

  UserAccount getUserById(Integer id);

  UserAccount getUserByName(String name);

  UserAccount getUserByEmail(String email);

  List<UserAccount> getAllUsers();
}
