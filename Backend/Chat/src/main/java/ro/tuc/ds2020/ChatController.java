package ro.tuc.ds2020;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

// Gestionez mesajele primite de la frontend și decid unde să le trimit
@CrossOrigin(origins = "*")
@Controller
public class ChatController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate; // trimiterea mesajelor catre utilizatori prin websocket

    //prinde mesajele trimise la app/chat de fe
    @MessageMapping("/chat")
    public void sendMessage(Message message) {
        System.out.println(message);
        // trimite mesajul tuturor utilizatorilor conectati la topic/messages
        messagingTemplate.convertAndSend("/topic/messages", message);
    }

    @MessageMapping("/chatPrivate")
    public void sendMessagePrivate(Message message) {
        System.out.println("📩 Mesaj privat primit în backend: " + message);

        if (message.getReceiver() == null || message.getReceiver().isEmpty()) {
            System.err.println("❌ Eroare: receiver este NULL! Nu se poate trimite mesajul.");
            return; //il ignor daca reciever e null
        }

        message.setReceiver(message.getReceiver());

        // ma asigur că mesajul ajunge corect
        System.out.println("📨 Structura mesajului primit în backend: " + message);

        // prefixez corect mesajul pentru STOMP
        String destination = "/user/" + message.getReceiver() + "/queue/messages";
        System.out.println("📬 Trimit mesaj către topicul: " + destination);

        //trimit mesaj privat catre un anume utilizator
        messagingTemplate.convertAndSendToUser(message.getReceiver(), "/queue/messages", message);
    }

    @MessageMapping("/seen")
    public void sendSeenNotification(Message message) {
        System.out.println("👀 Notificare SEEN primită în backend: " + message);

        if (message.getReceiver() == null || message.getReceiver().isEmpty()) {
            System.err.println("❌ Eroare: receiver este NULL! Nu se poate trimite notificarea de seen.");
            return;
        }

        //user prefix special pt conv private, doar userul din message.getReceiver va primi notificarea
        String destination = "/user/" + message.getReceiver() + "/queue/seen";
        System.out.println("📬 Trimit notificare de SEEN către: " + destination);

        messagingTemplate.convertAndSendToUser(message.getReceiver(), "/queue/seen", message);
    }

    @MessageMapping("/typing")
    public void sendTypingNotification(Message message) {
        System.out.println("⌨️ [Typing] Notificare de typing primită: " + message);

        if (message.getReceiver() == null || message.getReceiver().isEmpty()) {
            System.err.println("❌ [Typing] Eroare: receiver este NULL! Nu se poate trimite notificarea de typing.");
            return;
        }

        String destination = "/user/" + message.getReceiver() + "/queue/typing";
        System.out.println("📬 [Typing] Trimit notificare de typing către: " + destination);

        messagingTemplate.convertAndSendToUser(message.getReceiver(), "/queue/typing", message);
    }

}

