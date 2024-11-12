package nus.iss.team3.backend.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nus.iss.team3.backend.domainService.user.IUserAccountService;
import nus.iss.team3.backend.domainService.user.IUserStatusService;
import nus.iss.team3.backend.entity.EUserRole;
import nus.iss.team3.backend.entity.EUserStatus;
import nus.iss.team3.backend.entity.LoginRequest;
import nus.iss.team3.backend.entity.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserAccountController.class)
public class TestUserAccountController {
  @Autowired private MockMvc mockMvc;

  @MockBean private IUserAccountService userAccountService;
  @MockBean private IUserStatusService userStatusService;

  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  public void setup() {
    objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Test
  public void testAddUser_Success() throws Exception {
    UserAccount userAccount = createValidUserAccount();
    when(userAccountService.addUser(any(UserAccount.class))).thenReturn(true);

    mockMvc
        .perform(
            post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAccount)))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));
  }

  @Test
  public void testAddUser_NullAccount() throws Exception {
    mockMvc
        .perform(
            post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(null)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testAddUser_InvalidName() throws Exception {
    UserAccount userAccount = createValidUserAccount();
    userAccount.setName("");
    when(userAccountService.addUser(any(UserAccount.class)))
        .thenThrow(new IllegalArgumentException("Username cannot be empty or blank"));

    mockMvc
        .perform(
            post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAccount)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Username cannot be empty or blank"));
  }

  @Test
  public void testAddUser_InvalidPassword() throws Exception {
    UserAccount userAccount = createValidUserAccount();
    userAccount.setPassword("");
    when(userAccountService.addUser(any(UserAccount.class))).thenReturn(false);

    mockMvc
        .perform(
            post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAccount)))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("false"));
  }

  @Test
  public void testAddUser_InvalidEmail() throws Exception {
    UserAccount userAccount = createValidUserAccount();
    userAccount.setEmail("");
    when(userAccountService.addUser(any(UserAccount.class)))
        .thenThrow(new IllegalArgumentException("Username cannot be empty or blank"));

    mockMvc
        .perform(
            post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAccount)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Username cannot be empty or blank"));
  }

  @Test
  public void testAddUser_DuplicateEmail() throws Exception {
    UserAccount userAccount = createValidUserAccount();
    when(userAccountService.addUser(any(UserAccount.class))).thenReturn(false);

    mockMvc
        .perform(
            post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAccount)))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("false"));
  }

  @Test
  public void testUpdateUser_Success() throws Exception {
    UserAccount userAccount = createValidUserAccount();
    userAccount.setId(1);
    when(userAccountService.updateUser(any(UserAccount.class))).thenReturn(true);

    mockMvc
        .perform(
            post("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAccount)))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));
  }

  @Test
  public void testUpdateUser_NullAccount() throws Exception {
    mockMvc
        .perform(post("/user/update").contentType(MediaType.APPLICATION_JSON).content(""))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testUpdateUser_MissingId() throws Exception {
    UserAccount userAccount = createValidUserAccount();
    when(userAccountService.updateUser(any(UserAccount.class)))
        .thenThrow(new IllegalArgumentException("User ID cannot be null"));

    mockMvc
        .perform(
            post("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAccount)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("User ID cannot be null"));
  }

  @Test
  public void testUpdateUser_NonExistingAccount() throws Exception {
    UserAccount userAccount = createValidUserAccount();
    userAccount.setId(999);
    when(userAccountService.updateUser(any(UserAccount.class))).thenReturn(false);

    mockMvc
        .perform(
            post("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAccount)))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("false"));
  }

  @Test
  public void testUpdateUser_DuplicateEmail() throws Exception {
    UserAccount userAccount = createValidUserAccount();
    userAccount.setId(1);
    when(userAccountService.updateUser(any(UserAccount.class))).thenReturn(false);

    mockMvc
        .perform(
            post("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAccount)))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("false"));
  }

  @Test
  public void testDeleteUser_Success() throws Exception {
    when(userAccountService.deleteUserById(1)).thenReturn(true);

    mockMvc
        .perform(delete("/user/delete/1"))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));
  }

  @Test
  public void testDeleteUser_NonExistingUser() throws Exception {
    when(userAccountService.deleteUserById(999)).thenReturn(false);

    mockMvc
        .perform(delete("/user/delete/999"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("false"));
  }

  @Test
  public void testGetUser_Success() throws Exception {
    UserAccount userAccount = createValidUserAccount();
    userAccount.setId(1);
    when(userAccountService.getUserById(1)).thenReturn(userAccount);

    mockMvc
        .perform(get("/user/get/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Test User"));
  }

  @Test
  public void testGetUser_NotFound() throws Exception {
    when(userAccountService.getUserById(999)).thenReturn(null);

    mockMvc
        .perform(get("/user/get/999"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("false"));
  }

  @Test
  public void testGetAllUsers() throws Exception {
    List<UserAccount> userList = Arrays.asList(createValidUserAccount(), createValidUserAccount());
    userList.get(0).setId(1);
    userList.get(1).setId(2);

    when(userAccountService.getAllUsers()).thenReturn(userList);

    mockMvc
        .perform(get("/user/getAll"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[0].name").value("Test User"))
        .andExpect(jsonPath("$[1].name").value("Test User"));
  }

  @Test
  public void testgetAllUserIds() throws Exception {
    List<Integer> userIdList = new ArrayList<>();
    userIdList.add(1);
    userIdList.add(2);

    when(userAccountService.getAllUserIds()).thenReturn(userIdList);

    mockMvc
        .perform(get("/user/getAllUserIds"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0]").value(1))
        .andExpect(jsonPath("$[1]").value(2));
  }

  @Test
  public void checkLoginCall_userNull() throws Exception {
    LoginRequest userAccount = new LoginRequest();
    userAccount.setName("loginName");
    userAccount.setPassword("password");
    UserAccount user = null;

    when(userAccountService.authenticate(anyString(), anyString())).thenReturn(user);

    mockMvc
        .perform(
            post("/user/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAccount)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void checkLoginCall_userBanned() throws Exception {
    LoginRequest userAccount = new LoginRequest();
    userAccount.setName("loginName");
    userAccount.setPassword("password");
    UserAccount user = createValidUserAccount();
    user.setStatus(EUserStatus.BANNED);

    when(userAccountService.authenticate(anyString(), anyString())).thenReturn(user);

    mockMvc
        .perform(
            post("/user/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAccount)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void checkLoginCall_userOk() throws Exception {
    LoginRequest userAccount = new LoginRequest();
    userAccount.setName("loginName");
    userAccount.setPassword("password");
    UserAccount user = createValidUserAccount();
    user.setStatus(EUserStatus.ACTIVE);

    when(userAccountService.authenticate(anyString(), anyString())).thenReturn(user);

    mockMvc
        .perform(
            post("/user/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAccount)))
        .andExpect(status().isOk());
  }

  @Test
  public void testBanUser_Success() throws Exception {
    doNothing().when(userStatusService).banUser(1);

    mockMvc
        .perform(put("/user/1/ban"))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));

    verify(userStatusService).banUser(1);
  }

  @Test
  public void testBanUser_NotFound() throws Exception {
    doThrow(new IllegalArgumentException("User not found")).when(userStatusService).banUser(999);

    mockMvc
        .perform(put("/user/999/ban"))
        .andExpect(status().isNotFound())
        .andExpect(content().string("User not found"));
  }

  @Test
  public void testUnbanUser_Success() throws Exception {
    doNothing().when(userStatusService).unbanUser(1);

    mockMvc
        .perform(put("/user/1/unban"))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));

    verify(userStatusService).unbanUser(1);
  }

  @Test
  public void testUndoLastStatusChange() throws Exception {
    doNothing().when(userStatusService).undoLastStatusChange();

    mockMvc.perform(post("/user/status/undo")).andExpect(status().isOk());

    verify(userStatusService).undoLastStatusChange();
  }

  private UserAccount createValidUserAccount() {
    UserAccount userAccount = new UserAccount();
    userAccount.setName("Test User");
    userAccount.setPassword("password");
    userAccount.setDisplayName("Test Display Name");
    userAccount.setEmail("test@example.com");
    userAccount.setStatus(EUserStatus.ACTIVE);
    userAccount.setRole(EUserRole.USER);
    return userAccount;
  }
}
