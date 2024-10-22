package nus.iss.team3.backend.service.user;

import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import nus.iss.team3.backend.entity.UserAccount;
import nus.iss.team3.backend.service.ProfileConfig;
import nus.iss.team3.backend.service.webservice.IWebserviceCaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Profile(ProfileConfig.PROFILE_NOT + ProfileConfig.PROFILE_USER_ACCOUNT)
public class UserAccountWebCaller implements IUserAccountService {

  private static final Logger logger = LogManager.getLogger(UserAccountWebCaller.class);

  @Autowired private IWebserviceCaller webServiceCaller;

  @Value("${service.url.user.address}")
  private String serviceUrl;

  @Value("${service.url.user.port}")
  private String servicePort;

  @PostConstruct
  public void postContruct() {
    logger.info("Recipe Service Web Caller initialized.");
  }

  private String getUrl(String path) {
    return "http://" + serviceUrl + ":" + servicePort + path;
  }

  /**
   * @param userAccount
   * @return
   */
  @Override
  public boolean addUser(UserAccount userAccount) {
    String url = getUrl("/user/add");
    ResponseEntity<Object> response = webServiceCaller.postCall(url, userAccount, Object.class);
    return response.getStatusCode().is2xxSuccessful();
  }

  /**
   * @param userAccount
   * @return
   */
  @Override
  public boolean updateUser(UserAccount userAccount) {

    String url = getUrl("/user/update");
    ResponseEntity<Object> response = webServiceCaller.postCall(url, userAccount, Object.class);
    return response.getStatusCode().is2xxSuccessful();
  }

  /**
   * @param id
   * @return
   */
  @Override
  public boolean deleteUserById(Integer id) {
    String url = getUrl("/user/delete/" + id);
    ResponseEntity<Object> response = webServiceCaller.deleteCall(url, Object.class);
    return response.getStatusCode().is2xxSuccessful();
  }

  /**
   * @param id
   * @return
   */
  @Override
  public UserAccount getUserById(Integer id) {
    String url = getUrl("/user/get/" + id);
    ResponseEntity<UserAccount> response = webServiceCaller.getCall(url, UserAccount.class);
    if (response.getStatusCode().is2xxSuccessful()) {
      return response.getBody();
    }
    return null;
  }

  /**
   * @return
   */
  @Override
  public List<UserAccount> getAllUsers() {
    String url = getUrl("/user/getAll/");
    ParameterizedTypeReference<List<UserAccount>> typeRef = new ParameterizedTypeReference<>() {};
    ResponseEntity<List<UserAccount>> response = webServiceCaller.getCall(url, typeRef);
    if (response.getStatusCode().is2xxSuccessful()) {
      return response.getBody();
    } else {
      return Collections.emptyList();
    }
  }
}
