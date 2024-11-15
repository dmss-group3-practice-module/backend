package nus.iss.team3.backend.controller.config;

import nus.iss.team3.backend.businessService.notification.NotificationWebSocketObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
  @Autowired private NotificationWebSocketObserver webSocketHandler;

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(webSocketHandler, "/ws").setAllowedOrigins("*");
  }
}
