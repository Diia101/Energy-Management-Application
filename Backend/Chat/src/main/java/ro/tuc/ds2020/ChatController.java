package ro.tuc.ds2020;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
@CrossOrigin(origins = "*")
@Controller
public class ChatController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate; //metode pt trimitere mesaje la clienti conectati la websocket
//gestionez mesaje primite de la clienti
    @MessageMapping("/chat")
    public void sendMessage(Message message) {
        System.out.println(message);
        // metoda care primeste un mesaj si il trimite catre un anumit topic pentru clienți
        messagingTemplate.convertAndSend("/topic/messages", message);
    }
}