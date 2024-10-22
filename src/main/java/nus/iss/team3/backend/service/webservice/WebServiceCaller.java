package nus.iss.team3.backend.service.webservice;

import jakarta.annotation.PostConstruct;
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

  private static final Logger logger = LogManager.getLogger(WebServiceCaller.class);

  /** Called after the bean is initialized. Currently, no actions are taken. */
  @PostConstruct
  private void postConstruct() {}

  @Override
  public <T> ResponseEntity<T> getCall(String url, Class<T> responseType) {
    logger.info("Executing simple GET request to URL: {}", url);
    RestTemplate restTemplate = new RestTemplate();
    try {
      ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, null, responseType);
      logger.info(
          "Simple GET request to URL: {} completed with status: {}", url, response.getStatusCode());
      return response;
    } catch (RestClientException e) {
      logger.error("Simple GET request to URL: {} failed. Error: {}", url, e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public <T> ResponseEntity<T> getCall(String url, ParameterizedTypeReference<T> responseType) {
    logger.info("Executing GET request to URL: {}", url);
    RestTemplate restTemplate = new RestTemplate();
    try {
      ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, null, responseType);
      logger.info(
          "GET request to URL: {} completed with status: {}", url, response.getStatusCode());
      return response;
    } catch (RestClientException e) {
      logger.error("GET request to URL: {} failed. Error: {}", url, e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public <T> ResponseEntity<T> postCall(String url, Object request, Class<T> responseType) {
    logger.info("Executing POST request to URL: {} with payload: {}", url, request);
    RestTemplate restTemplate = new RestTemplate();
    try {
      HttpEntity<Object> entity = new HttpEntity<>(request);
      ResponseEntity<T> response =
          restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
      logger.info(
          "POST request to URL: {} completed with status: {}", url, response.getStatusCode());
      return response;
    } catch (RestClientException e) {
      logger.error("POST request to URL: {} failed. Error: {}", url, e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public <T> ResponseEntity<T> putCall(String url, Object request, Class<T> responseType) {
    logger.info("Executing PUT request to URL: {} with payload: {}", url, request);
    RestTemplate restTemplate = new RestTemplate();
    try {
      HttpEntity<Object> entity = new HttpEntity<>(request);
      ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.PUT, entity, responseType);
      logger.info(
          "PUT request to URL: {} completed with status: {}", url, response.getStatusCode());
      return response;
    } catch (RestClientException e) {
      logger.error("PUT request to URL: {} failed. Error: {}", url, e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public <T> ResponseEntity<T> deleteCall(String url, Class<T> responseType) {
    logger.info("Executing DELETE request to URL: {}", url);
    RestTemplate restTemplate = new RestTemplate();
    try {
      ResponseEntity<T> response =
          restTemplate.exchange(url, HttpMethod.DELETE, null, responseType);
      logger.info(
          "DELETE request to URL: {} completed with status: {}", url, response.getStatusCode());
      return response;
    } catch (RestClientException e) {
      logger.error("DELETE request to URL: {} failed. Error: {}", url, e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
