package nus.iss.team3.backend.controller;

import jakarta.servlet.http.HttpSession;
import nus.iss.team3.backend.entity.UserAccount;
import nus.iss.team3.backend.service.IAuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class AuthController {

  private static final Logger logger = LogManager.getLogger(AuthController.class);

  @Autowired private IAuthService authService;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
    try {
      if (loginRequest.getName() == null
          || loginRequest.getName().isEmpty()
          || loginRequest.getPassword() == null
          || loginRequest.getPassword().isEmpty()) {
        logger.warn("Login attempt with null or empty username/password");
        return new ResponseEntity<>(
            "Username and password cannot be empty", HttpStatus.BAD_REQUEST);
      }
      UserAccount user =
          authService.authenticate(loginRequest.getName(), loginRequest.getPassword());
      if (user != null) {
        logger.info("User logged in successfully: {}", user.getName());
        session.setAttribute("user", user);
        // Use JsonView to exclude password when returning user object
        return new ResponseEntity<>(user, HttpStatus.OK);
      } else {
        logger.warn("Login failed for user: {}", loginRequest.getName());
        return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
      }
    } catch (Exception e) {
      logger.error("Unexpected error during login", e);
      return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpSession session) {
    try {
      UserAccount user = (UserAccount) session.getAttribute("user");
      if (user != null) {
        logger.info("User logged out: {}", user.getName());
        session.invalidate();
        return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
      } else {
        logger.warn("Logout attempt with no active session");
        return new ResponseEntity<>("No active session", HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      logger.error("Unexpected error during logout", e);
      return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private static class LoginRequest {
    private String name;
    private String password;

    // Getters and setters
    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }
  }
}
