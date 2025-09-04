import React, { useState, useEffect } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { v4 as uuidv4 } from "uuid";
import "../App.css";
import { Button, Form } from "react-bootstrap";

const Chat = () => {
    const [messages, setMessages] = useState([]);
    const [messageInput, setMessageInput] = useState("");
    const [username, setUsername] = useState(localStorage.getItem("userData") || ""); // setez username din localStorage si daca nu exista setez string gol
    const [stompClient, setStompClient] = useState(null); //websocket
    const [isTyping, setIsTyping] = useState(false);


    useEffect(() => {
        console.log("Inițializare WebSocket...");

        //creez o conexiune websocket catre backend
        const socket = new SockJS("http://chat-service.localhost/ws");
        //clientul stomp care gestioneaza websocketul
        const client = new Client({
            webSocketFactory: () => socket, //folosesc socketjs pt websocket
            reconnectDelay: 5000, //daca se intrerupe conexiunea se reconecteaza dupa 5 sec
            debug: (str) => console.log("🔍 [STOMP Debug]:", str),
            onConnect: () => {
                console.log("✅ [STOMP] Conectat la WebSocket!");
                setupSubscriptions(client); //ma abonez la mesaje
            },
            onDisconnect: () => {
                console.warn("🔴 [STOMP] WebSocket deconectat! Reîncerc...");
                client.activate(); // reîncercare automată a conectării
            },
            onStompError: (frame) => console.error("❌ [STOMP] Eroare WebSocket: ", frame),
        });
        //pornesc conexiunea websocket
        client.activate();
        setStompClient(client); //salvez clientul stomp in useState

        //cand chat e inchisa sau se incarca
        return () => {
            console.log("🧹 Curățare conexiune WebSocket...");
            client.deactivate(); //inchid websocketul
        };
    }, []);

//abonarea la evenimentele websocket
    const setupSubscriptions = (client) => {
        console.log("📡 Abonare la topicuri pentru:", username);
        //definesc cele 3 topicuri pt websocket
        const userQueue = `/user/${username}/queue/messages`; //mesajele private
        const seenQueue = `/user/${username}/queue/seen`;
        const typingQueue = `/user/${username}/queue/typing`;

        //se face abonarea la mesaje
        client.subscribe(userQueue, (message) => {
            //message.body contine datele trimise de backend (chatcontroller)
            console.log("🔍 [WebSocket] Mesaj primit de la backend:", message.body);
            handleMessage(message);
        });
        //abonarea la seen
        client.subscribe(seenQueue, (message) => {
            console.log("👀 [WebSocket] Notificare SEEN primită:", message.body);
            handleSeenMessage(message);
        });
        //abonarea la typing
        client.subscribe(typingQueue, (message) => {
            console.log("⌨️ [WebSocket] Notificare TYPING primită:", message.body);
            handleTypingMessage(message);
        });

        console.log(`✅ Subscris la: ${userQueue}, ${seenQueue} și ${typingQueue}`);
    };

//trimit mesaje prin ws catre backend
    const handleSendMessage = () => {
        //obtin userul cu care vb; trim()- elimina spatiile din nume
        const receiver = localStorage.getItem("chatUserName")?.trim() || "diia";

        //daca mesajul e gol iesim din functie
        if (!messageInput.trim()) return;

        //construiesc obiectul de mesaj
        const message = {
            messageId: uuidv4(),
            sender: username, //utilizatorul curent
            receiver,
            content: messageInput.trim(), //continutul mesajului curatat de spatii goale
            timestamp: new Date().toISOString(),
        };

        console.log("📤 Trimit mesaj:", message);

        //trimit mesajul catre server prin ws
        stompClient.publish({
            destination: "/app/chatPrivate", //spun backendului ca e un mesaj privat
            body: JSON.stringify(message), //convertesc mesajul intr un string deoarece ws accepta doar text
        });

        //adaug mesajul in interfata
        setMessages((prev) => [...prev, { ...message, self: true }]);
        //sterg mesajul din input dupa ce a fost trimis
        setMessageInput("");
    };


    //mesajele primite
    const handleMessage = (message) => {
        //message.body contine mesajul sub forma de text json trimis de backend
        const msg = JSON.parse(message.body); //convertesc intr un obiect javascript
        console.log("📩 [Frontend] Mesaj primit prin WebSocket:", msg);

        if (!msg.receiver) {
            console.warn("⚠️ Mesaj fără receiver, îl setez manual:", username);
            msg.receiver = username;
        }

        // Filtrez mesajele doar pentru conversația curentă
        // utilizatorul cu care vb, daca nu exista setam diia implicit
        const activeChatUser = localStorage.getItem("chatUserName") || "diia";
        //verific daca mesajul apartine conversatiei curente
        if (
            (msg.sender === username && msg.receiver === activeChatUser) || // mesaj trimis către userul activ
            (msg.sender === activeChatUser && msg.receiver === username) // mesaj primit de la userul activ
        ) {
            console.log(`💬 [Frontend] Adaug mesaj în chat: ${msg.sender} -> ${msg.receiver}: ${msg.content}`);
            setMessages((prev) => [...prev, msg]); //adaug noul mesaj la final
        } else {
            console.log("❌ [Frontend] Mesaj ignorat - nu este din conversația curentă.");
        }
    };

    //trimit seen
    const sendSeenMessage = () => {
        //daca e initializat si activ
        if (stompClient) {
            //userul cu care vb
            const sender = localStorage.getItem("chatUserName")?.trim() || "diia";
            //construiesc un mesaj json care indica ca msj a fost citit
            const seenMessage = { sender: username, receiver: sender, content: "SEEN" };

            console.log("📤 [Seen] Trimit notificare de seen:", seenMessage);

            //trimit mesajul de seen catre backend prin websocket
            stompClient.publish({
                destination: "/app/seen",
                body: JSON.stringify(seenMessage), //convertesc ca sa fie compatibil cu ws
            });
        }
    };
//primesc seen
    const handleSeenMessage = (message) => {
        const seenMsg = JSON.parse(message.body);
        console.log("👀 [Frontend] Mesaj de seen primit:", seenMsg);

        setMessages((prevMessages) => {
            // gasesc ultimul mesaj primit de la sender
            let lastIndex = -1; //incepem de la ultimul mesaj
            for (let i = prevMessages.length - 1; i >= 0; i--) {
                if (prevMessages[i].receiver === seenMsg.sender) { //daca destinatarul a dat seen
                    lastIndex = i; //salvez indexul mesajului
                    break;
                }
            }
            //actualizam lista de mesaje
            return prevMessages.map((msg, index) =>
                //daca indexul e egal cu ultimu mesaj primit de la sender adaug bifa la utlimu msj
                index === lastIndex ? { ...msg, seen: true } : { ...msg, seen: false }
            );
        });
    };

    //trimit notificare de typing prin ws catre backend
    const sendTypingNotification = () => {
        //daca ws e activ
        if (stompClient) {
            const receiver = localStorage.getItem("chatUserName")?.trim() || "diia";
            //creez obiectul mesajului
            const typingMessage = { sender: username, receiver, content: "TYPING" };

            console.log("⌨️ [Typing] Trimit notificare de typing:", typingMessage);

            //trimit mesajul prin ws catre backend
            stompClient.publish({
                destination: "/app/typing",
                body: JSON.stringify(typingMessage), //convertesc ca sa poate fi trimis prin ws
            });
        }
    };

    //primesc typing
    const handleTypingMessage = (message) => {
        const typingMsg = JSON.parse(message.body); //avem sender si reciever
        console.log("⌨️ [Frontend] Notificare de typing primită:", typingMsg);

        // filtrez typing doar pentru userul activ din conversație
        const activeChatUser = localStorage.getItem("chatUserName") || "diia";

        //verific daca mesajul de typing vine de la userul cu care vb
        if (typingMsg.sender === activeChatUser && typingMsg.receiver === username) {
            setIsTyping(true);
            setTimeout(() => setIsTyping(false), 2000); // ascund "Typing..." dupa 2 secunde
        } else {
            console.log("❌ [Frontend] Notificare de typing ignorată - nu este din conversația curentă.");
        }
    };


    return (
        <div className="chat-container">
            {/*lista de mesaje*/}
            <div className="message-container">
                {messages.map((msg, index) => (
                    <div
                        key={index}
                        className={`message ${msg.sender === username ? "sender-me" : "sender-other"}`}
                    >
                        {msg.sender}: {msg.content} {/*afiseaza userul care a dat mesaj si mesajul pe care l a dat*/}
                        {msg.seen && <span className="seen-indicator">✔</span>} {/*daca msg.seen === true coloram bifa*/}
                    </div>
                ))}
                {/*daca isTyping===true apare mesajul*/}
                {isTyping && <div className="typing-indicator">⌨️ Typing...</div>}
            </div>

            {/*input pt scrierea mesajelor*/}
            <Form>
                {/* form.control pt input text*/}
                <Form.Control
                    type="text" {/*tipul inputului*/}
                    value={messageInput} {/*actualizezz valorile in timp real*/}
                    onChange={(e) => {
                        setMessageInput(e.target.value);
                        sendTypingNotification(); // trimit notificare de typing
                    }}
                    onFocus={sendSeenMessage} // trimit notificare de seen când userul dau click pe input
                    placeholder="Type your message..." {/*cand inputul e gol*/}
                    style={{ marginTop: "5px" }}
                    className="bg-dark text-light"
                />
            </Form>
            <Button style={{ marginTop: "5px" }} variant="dark" onClick={handleSendMessage}>
                Send
            </Button>
        </div>
    );
};

export default Chat;
