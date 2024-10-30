package nus.iss.team3.backend.domainService.webservice;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Interface for doing rest API calls with support for generic.
 *
 * @author Mao Weining
 */
@Service
public class WebServiceCaller implements IWebserviceCaller {

  private static final int MAX_RETRY = 3;
  private static final int BUFFER_BETWEEN_TRY = 1;
  private static final Logger logger = LogManager.getLogger(WebServiceCaller.class);

  // List of HTTP status codes that are considered as error, and should be retried..
  private static final List<Integer> retryHttpCode = List.of(HttpStatus.REQUEST_TIMEOUT.value());

  /** Called after the bean is initialized. Currently, no actions are taken. */
  @PostConstruct
  private void postConstruct() {}

  @Override
  public <T> ResponseEntity<T> getCall(String url, Class<T> responseType) {
    logger.info("Executing simple GET request to URL: {}", url);
    RestTemplate restTemplate = new RestTemplate();
    for (int i = 0; i < MAX_RETRY; i++) {
      try {
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, null, responseType);
        logger.info(
            "[{}/{}]Simple GET request to URL: {} completed with status: {}",
            (i + 1),
            MAX_RETRY,
            url,
            response.getStatusCode());
        if (successfulCall(response)) {
          return response;
        }
      } catch (RestClientException e) {
        logger.error(
            "[{}/{}]Simple GET request to URL: {} failed. Error: {}",
            (i + 1),
            MAX_RETRY,
            url,
            e.getMessage());
        // client exception, re-trying will not help.so end here
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
      try {
        TimeUnit.SECONDS.sleep(BUFFER_BETWEEN_TRY);
      } catch (InterruptedException ex) {
        throw new RuntimeException(ex);
      }
    }
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  public <T> ResponseEntity<T> getCall(String url, ParameterizedTypeReference<T> responseType) {
    logger.info("Executing GET request to URL: {}", url);
    RestTemplate restTemplate = new RestTemplate();
    for (int i = 0; i < MAX_RETRY; i++) {
      try {
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, null, responseType);
        logger.info(
            "[{}/{}]GET request to URL: {} completed with status: {}",
            (i + 1),
            MAX_RETRY,
            url,
            response.getStatusCode());
        if (successfulCall(response)) {
          return response;
        }
      } catch (RestClientException e) {
        logger.error(
            "[{}/{}]GET request to URL: {} failed. Error: {}",
            (i + 1),
            MAX_RETRY,
            url,
            e.getMessage());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
      try {
        TimeUnit.SECONDS.sleep(BUFFER_BETWEEN_TRY);
      } catch (InterruptedException ex) {
        throw new RuntimeException(ex);
      }
    }
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  public <T> ResponseEntity<T> postCall(String url, Object request, Class<T> responseType) {
    logger.info("Executing POST request to URL: {} with payload: {}", url, request);
    RestTemplate restTemplate = new RestTemplate();
    for (int i = 0; i < MAX_RETRY; i++) {
      try {
        HttpEntity<Object> entity = new HttpEntity<>(request);
        ResponseEntity<T> response =
            restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
        logger.info(
            "[{}/{}]POST request to URL: {} completed with status: {}",
            (i + 1),
            MAX_RETRY,
            url,
            response.getStatusCode());
        if (successfulCall(response)) {
          return response;
        }
      } catch (RestClientException e) {
        logger.error(
            "[{}/{}]POST request to URL: {} failed. Error: {}",
            (i + 1),
            MAX_RETRY,
            url,
            e.getMessage());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
      try {
        TimeUnit.SECONDS.sleep(BUFFER_BETWEEN_TRY);
      } catch (InterruptedException ex) {
        throw new RuntimeException(ex);
      }
    }
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  public <T> ResponseEntity<T> putCall(String url, Object request, Class<T> responseType) {
    logger.info("Executing PUT request to URL: {} with payload: {}", url, request);
    RestTemplate restTemplate = new RestTemplate();
    for (int i = 0; i < MAX_RETRY; i++) {
      try {
        HttpEntity<Object> entity = new HttpEntity<>(request);
        ResponseEntity<T> response =
            restTemplate.exchange(url, HttpMethod.PUT, entity, responseType);
        logger.info(
            "[{}/{}]PUT request to URL: {} completed with status: {}",
            (i + 1),
            MAX_RETRY,
            url,
            response.getStatusCode());
        if (successfulCall(response)) {
          return response;
        }
      } catch (RestClientException e) {
        logger.error(
            "[{}/{}]PUT request to URL: {} failed. Error: {}",
            (i + 1),
            MAX_RETRY,
            url,
            e.getMessage());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
      try {
        TimeUnit.SECONDS.sleep(BUFFER_BETWEEN_TRY);
      } catch (InterruptedException ex) {
        throw new RuntimeException(ex);
      }
    }
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  public <T> ResponseEntity<T> deleteCall(String url, Class<T> responseType) {
    logger.info("Executing DELETE request to URL: {}", url);
    RestTemplate restTemplate = new RestTemplate();
    for (int i = 0; i < MAX_RETRY; i++) {
      try {
        ResponseEntity<T> response =
            restTemplate.exchange(url, HttpMethod.DELETE, null, responseType);
        logger.info(
            "[{}/{}]DELETE request to URL: {} completed with status: {}",
            (i + 1),
            MAX_RETRY,
            url,
            response.getStatusCode());
        if (successfulCall(response)) {
          return response;
        }
      } catch (RestClientException e) {
        logger.error(
            "[{}/{}]DELETE request to URL: {} failed. Error: {}",
            (i + 1),
            MAX_RETRY,
            url,
            e.getMessage());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
      try {
        TimeUnit.SECONDS.sleep(BUFFER_BETWEEN_TRY);
      } catch (InterruptedException ex) {
        throw new RuntimeException(ex);
      }
    }
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private boolean successfulCall(ResponseEntity<?> response) {
    if (response.getStatusCode().is2xxSuccessful()) {
      return true;
    }
    if (response.getStatusCode().is5xxServerError()) {
      return false;
    }
    return !retryHttpCode.contains(response.getStatusCode().value());
  }
}
