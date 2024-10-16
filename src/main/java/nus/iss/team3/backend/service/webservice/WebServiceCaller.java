package nus.iss.team3.backend.service.webservice;

import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/** Interface for doing rest API calls
 *
 * @Author Desmond Tan Zhi Heng, REN JIARUI
 * */
@Service
public class WebServiceCaller implements IWebserviceCaller {

  private static final Logger logger = LogManager.getLogger(WebServiceCaller.class);

  @Autowired
  private RestTemplate restTemplate;

  @PostConstruct
  private void postConstruct() {}

  /**
   * @param url
   * @return
   */
  public Object getCall(String url, Class<?> returnType) {
    try {
      logger.info("going to call get {}", url);
      ResponseEntity<?> response = restTemplate.getForEntity(url, returnType);
      logger.info("going to call get {}, returning : {}", url, response.getBody());
      if (response.getStatusCode().is2xxSuccessful()) {
        return response.getBody();
      }
      return null;
    } catch (RestClientException e) {
      logger.error("error with url : {}", e.toString());
      return null;
    }
  }

  /**
   * @param url
   * @param bodyObject
   * @return
   */
  public Object postCall(String url, Object bodyObject, Class<?> returnType) {
    try {
      logger.info("going to call post {}", url);
      ResponseEntity<?> response = restTemplate.postForEntity(url, bodyObject, returnType);
      logger.info("going to call post {}, returning {}", url, response.getBody());
      if (response.getStatusCode().is2xxSuccessful()) {
        return response.getBody();
      }
      return null;
    } catch (RestClientException e) {
      logger.error("error with url : {}", e.toString());
      return null;
    }
  }
}
