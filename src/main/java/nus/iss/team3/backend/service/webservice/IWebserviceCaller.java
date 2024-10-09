package nus.iss.team3.backend.service.webservice;

public interface IWebserviceCaller {

  Object getCall(String url, Class<?> returnType);

  Object postCall(String url, Object bodyObject, Class<?> returnType);
}
