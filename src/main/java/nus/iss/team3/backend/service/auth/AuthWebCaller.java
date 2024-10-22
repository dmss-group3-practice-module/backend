package nus.iss.team3.backend.service.auth;

import jakarta.annotation.PostConstruct;
import nus.iss.team3.backend.entity.LoginRequest;
import nus.iss.team3.backend.entity.UserAccount;
import nus.iss.team3.backend.service.ProfileConfig;
import nus.iss.team3.backend.service.webservice.IWebserviceCaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Profile(ProfileConfig.PROFILE_NOT + ProfileConfig.PROFILE_AUTHENTICATE)
public class AuthWebCaller implements IAuthService {

  private static final Logger logger = LogManager.getLogger(AuthWebCaller.class);

  @Value("${service.url.authenticate.address}")
  private String serviceUrl;

  @Value("${service.url.authenticate.port}")
  private String servicePort;

  @Autowired private IWebserviceCaller webServiceCaller;

  @PostConstruct
  public void postContruct() {
    logger.info("Authenticate Service Web Caller initialized.");
  }

  private String getUrl(String path) {
    return "http://" + serviceUrl + ":" + servicePort + path;
  }

  /**
   * @param username The username of the user trying to authenticate
   * @param password The password of the user trying to authenticate
   * @return
   */
  @Override
  public UserAccount authenticate(String username, String password) {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setName(username);
    loginRequest.setPassword(password);

    String url = getUrl("/authenticate/check");

    ResponseEntity<UserAccount> response =
        webServiceCaller.postCall(url, loginRequest, UserAccount.class);
    logger.info("response is " + response.getStatusCode());
    logger.info("response body is " + response.getBody());
    logger.info("response body class is " + response.getBody().getClass());
    if (response.getStatusCode().is2xxSuccessful()) {
      if (response.getBody() instanceof UserAccount) return (UserAccount) response.getBody();
    }

    return null;
  }
}
