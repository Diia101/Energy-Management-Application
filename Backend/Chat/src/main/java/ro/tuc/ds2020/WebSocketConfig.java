package ro.tuc.ds2020;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


//Configurez WebSocket-ul și broker-ul de mesaje pentru a permite trimiterea și primirea mesajelor
//nu procesez mesajele, doar stabileșc regulile de direcționare
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // inregistrarea endpoint-ului WebSocket și definirea originilor permise pentru conexiuni
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // /ws e pct de acces pt conexiunile Websocket; permite conexiuni doar de la localhost:3000; activezSockJS
        registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:3000").withSockJS();
    }

    //setez websocket si il folosesc pe fe
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // activez canalele WebSocket pt mesaje publice/private
        registry.enableSimpleBroker("/topic", "/queue", "/user");
        // /user- mesaje destinate unui utilizator specific
        // /queue- mesaje private
        // /topic- mesaje publice

        // prefix pentru mesajele trimise de frontend catre backend
        registry.setApplicationDestinationPrefixes("/app");

        // permit trimiterea mesajelor către un utilizator specific
        registry.setUserDestinationPrefix("/user");
    }

}
