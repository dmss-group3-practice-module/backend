package nus.iss.team3.backend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import nus.iss.team3.backend.controller.UserAccountController;
import nus.iss.team3.backend.entity.EUserAccountStatus;
import nus.iss.team3.backend.entity.EUserRole;
import nus.iss.team3.backend.entity.UserAccount;
import nus.iss.team3.backend.service.IUserAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BackendApplicationTests {

  @Mock private IUserAccountService userAccountService;

  @InjectMocks private UserAccountController userAccountController;

  private UserAccount testUser;

  @BeforeEach
  void setUp() {
    testUser = new UserAccount();
    testUser.setId(1);
    testUser.setName("testuser");
    testUser.setPassword("password123");
    testUser.setDisplayName("Test User");
    testUser.setEmail("testuser@example.com");
    testUser.setStatus(EUserAccountStatus.ACTIVE);
    testUser.setRole(EUserRole.USER);
  }

  /*@Test
  void testAddUser_Success() {
    when(userAccountService.addUser(any(UserAccount.class))).thenReturn(true);
    ResponseEntity response = userAccountController.addUser(testUser);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(true, response.getBody());
  }

  @Test
  void testAddUser_Failure() {
    when(userAccountService.addUser(any(UserAccount.class))).thenReturn(false);
    ResponseEntity response = userAccountController.addUser(testUser);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(false, response.getBody());
  }

  @Test
  void testUpdateUser_Success() {
    when(userAccountService.updateUser(any(UserAccount.class))).thenReturn(true);
    ResponseEntity response = userAccountController.updateUser(testUser);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(true, response.getBody());
  }

  @Test
  void testUpdateUser_Failure() {
    when(userAccountService.updateUser(any(UserAccount.class))).thenReturn(false);
    ResponseEntity response = userAccountController.updateUser(testUser);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(false, response.getBody());
  }

  @Test
  void testDeleteUser_Success() {
    when(userAccountService.deleteUserById(anyLong())).thenReturn(true);
    ResponseEntity response = userAccountController.deleteUser(1L);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(true, response.getBody());
  }

  @Test
  void testDeleteUser_Failure() {
    when(userAccountService.deleteUserById(anyLong())).thenReturn(false);
    ResponseEntity response = userAccountController.deleteUser(1L);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(false, response.getBody());
  }

  @Test
  void testGetUser_Found() {
    when(userAccountService.getUserById(anyLong())).thenReturn(testUser);
    ResponseEntity result = userAccountController.getUser(1L);
    assertNotNull(result);
    */
  /*assertEquals(testUser.getId(), result.getId());
  assertEquals(testUser.getName(), result.getName());*/
  /*
  }

  @Test
  void testGetUser_NotFound() {
    when(userAccountService.getUserById(anyLong())).thenReturn(null);
    ResponseEntity result = userAccountController.getUser(1L);
    assertNull(result);
  }

  @Test
  void testGetAllUser() {
    List<UserAccount> userList = Arrays.asList(testUser, new UserAccount());
    when(userAccountService.getAllUser()).thenReturn(userList);
    //    List<UserAccount> result = userAccountController.getAllUser();
    */
  /*assertNotNull(result);
  assertEquals(2, result.size());
  assertEquals(testUser.getId(), result.get(0).getId());*/
}
