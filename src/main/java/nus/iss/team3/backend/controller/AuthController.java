package nus.iss.team3.backend.controller;

import jakarta.servlet.http.HttpSession;
import nus.iss.team3.backend.entity.LoginRequest;
import nus.iss.team3.backend.entity.UserAccount;
import nus.iss.team3.backend.service.IAuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class to handle web call for authentication related queries
 *
 * @author REN JIARUI
 */
@RestController
@RequestMapping("user")
public class AuthController {

  private static final Logger logger = LogManager.getLogger(AuthController.class);

  @Autowired private IAuthService authService;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
    try {
      UserAccount user =
          authService.authenticate(loginRequest.getName(), loginRequest.getPassword());
      if (user != null) {
        logger.info("User logged in successfully: {}", user.getName());
        session.setAttribute("user", user);
        return new ResponseEntity<>(user, HttpStatus.OK);
      } else {
        logger.warn("Login failed for user: {}", loginRequest.getName());
        return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
      }
    } catch (IllegalArgumentException e) {
      logger.warn("Login attempt with invalid input: {}", e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
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
}
