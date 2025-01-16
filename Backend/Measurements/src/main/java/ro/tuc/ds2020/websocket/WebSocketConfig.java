package ro.tuc.ds2020.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
@CrossOrigin(origins = "*")

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //activez un broker de mesaje care gestioneaza mesajele clientilor
        config.enableSimpleBroker("/topic");
        //specifica prefixul pentru destinatiile cererilor trimise de client catre server
        config.setApplicationDestinationPrefixes("/app");
    }

    //stabilesc conexiunea websocket cu serverul
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-message").setAllowedOriginPatterns("*").withSockJS();
    }
}