package ro.tuc.ds2020.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.socket.WebSocketMessage;

    @Controller
    public class WebSocketController {

        @Autowired
        SimpMessagingTemplate template;

        //grstionez trimiterea mesajelor catre clienti prin websocket
        @CrossOrigin(origins = "*")
        @PostMapping("/send")
        //mesajul trimis de client e preluat din corpul http si mapat la textmessagedto
        public ResponseEntity<Void> sendMessage(@RequestBody String textMessageDTO) {
            //mesajul primit e trimis catre toti clientii abonati la /topic/message
            template.convertAndSend("/topic/message", textMessageDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }