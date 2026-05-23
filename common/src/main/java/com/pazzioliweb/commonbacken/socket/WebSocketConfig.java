package com.pazzioliweb.commonbacken.socket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // "*" en setAllowedOriginPatterns permite cualquier origen y mantiene
    // soporte para credenciales (cookies). Suficiente para intranet.
    // Si en el futuro lo expones a internet, restringe a dominios específicos.
    registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .withSockJS();
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");
    config.setApplicationDestinationPrefixes("/app");
  }
}