package nus.iss.team3.backend.service.webservice;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

public interface IWebserviceCaller {

  <T> ResponseEntity<T> getCall(String url, Class<T> responseType);

  <T> ResponseEntity<T> getCall(String url, ParameterizedTypeReference<T> responseType);

  <T> ResponseEntity<T> postCall(String url, Object request, Class<T> responseType);

  <T> ResponseEntity<T> putCall(String url, Object request, Class<T> responseType);

  <T> ResponseEntity<T> deleteCall(String url, Class<T> responseType);
}
