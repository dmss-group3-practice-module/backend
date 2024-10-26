package nus.iss.team3.backend.service.webservice;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

/**
 * Interface for doing rest API calls with support for generic.
 *
 * @author Mao Weining
 */
public interface IWebserviceCaller {

  <T> ResponseEntity<T> getCall(String url, Class<T> responseType);

  <T> ResponseEntity<T> getCall(String url, ParameterizedTypeReference<T> responseType);

  <T> ResponseEntity<T> postCall(String url, Object request, Class<T> responseType);

  <T> ResponseEntity<T> putCall(String url, Object request, Class<T> responseType);

  <T> ResponseEntity<T> deleteCall(String url, Class<T> responseType);
}
