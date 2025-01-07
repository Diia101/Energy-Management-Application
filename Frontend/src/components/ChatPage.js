import React, { useState, useEffect, useRef } from 'react';
import SockJsClient from 'react-stomp';
import '../App.css';
import { Button } from 'react-bootstrap';
import { Form } from 'react-bootstrap';
import { trigger } from "polyrhythm";

const Chat = () => {
    const [messages, setMessages] = useState([]);
    const [messageInput, setMessageInput] = useState('');
    const [username, setUsername] = useState('');
    const [topics, setTopics] = useState([]);
    const [isSeen, setIsSeen] = useState(false);
    const [isTyping, setIsTyping] = useState(false);

    

    // ca sa putem trimite mesaje, trebe sa facem o referinta catre obiectul de client socket, pe care o initializam cu null la inceput
    //clientRef este un obiect de referință (Ref) 
    //creat folosind useRef hook din React
    //Aceasta este o modalitate de a păstra o referință către un element sau un obiect în cadrul componentelor funcționale în React, 
    //care poate fi utilizată pentru a accesa și manipula acest element sau obiect.
    const clientRef = useRef(null);
    

    useEffect(() => {
         // Utilizarea useEffect pentru a seta username-ul când componenta este randata
        setUsername(localStorage.getItem('userData'));

         // Funcția returnată va fi apelată când componenta este dezactivată, inchidem conexiunea
        return () => {
            if (clientRef.current && clientRef.current.deactivate) {
                clientRef.current.deactivate();
            }
        };
    }, []);

     // Funcție pentru a gestiona schimbarea valorii în input-ul de mesaj
    const handleMessageChange = (e) => {
        setMessageInput(e.target.value);
    };

      // Funcție pentru a trimite mesajul
    const handleSendMessage = () => {
        //aici voi schimbati, va mai adaugati ce va mai trebuie, eu am lasat reciever random
        const message = {
            sender: username,
            receiver: 'admin',
            content: messageInput,
        };
        //current este proprietatea din obiectul de referință care conține referința efectivă către elementul sau obiectul 
        //pe care l-ați legat cu useRef. 
        //Prin accesarea proprietății current, puteți interacționa cu obiectul sau elementul din afara ciclului de viață al componentei.
        //practic un mod de a accesa obiectul in sine, nu doar referinta

        //sendMessage este o metodă specifică a acestui client WebSocket (SockJsClient) 
        //care este utilizată pentru a trimite mesaje către serverul WebSocket
        if (clientRef.current && clientRef.current.sendMessage) {
            clientRef.current.sendMessage('/app/chat', JSON.stringify(message));
        }

        //golim mesajul dupa ce l-am trimis
        setMessageInput('');
    };

    // cand primim mesaj, pe langa cele pe care le avem deja vrem sa le pastram
    //de asta punem cu .. inainte, asta inseamna ca la array-ul de mesaje deja existent, mai adaugam pe cel pe care l-am primit
    const onMessageReceived = (msg) => {
        setMessages([...messages, msg]);
        setIsSeen(true); // Set seen status when a new message is received
    resetSeenStatus();
    };
    const handleChange = (event) => {
        handleMessageChange(event); 
        setIsTyping(true);         
        handleTyping();            
      };
      const debounce = (func, delay) => {
        let inDebounce;
        return function() {
          const context = this;
          const args = arguments;
          clearTimeout(inDebounce);
          inDebounce = setTimeout(() => func.apply(context, args), delay);
        };
      };
      
      
      const handleTyping = debounce(() => {
      
        setIsTyping(false);
      }, 2000);
      
   
      const onTextChange = (event) => {
        setIsTyping(true);
        handleTyping();
      };
      const resetSeenStatus = () => {
        setTimeout(() => {
          setIsSeen(false);
        }, 2000); 
      };
  

    let onConnected = () => {
      setTopics(['/topic/messages'])
      console.log("Connected!!")
    }
   

    return (
        <div className="chat-container">
        <div className="message-container">
            {messages.map((msg, index) => (
                <div
                    key={index}
                    className={`message ${msg.sender === username ? 'sender-me' : 'sender-other'}`} //aici am vrut sa fiu smechera sa pun clase diferite pt mesajele mele vs cele primite
                >
                    {msg.sender}: {msg.content} 
                    {isSeen && <div>Seen</div>}
  {isTyping && <div>Typing...</div>}
                </div>
                
                    //aici pot accesa senderu si contetu, ca mi le-am definit asa in java, voi afisati cum vreti
            ))}
        </div>
        <Form>
                <Form.Control
                    type="text"
                    value={messageInput}
                    onChange={handleChange}
                    placeholder="Type your message..."
                    style={{ marginTop:"5px"}}
                    className="bg-dark text-light" 
                

                />
         </Form>
        <Button style={{marginTop:"5px"}} variant='dark' onClick={handleSendMessage}>Send</Button>
       {/* New Polyrythm message sender */}
     
        <SockJsClient
            url="http://chat-service.localhost/ws"
           // url="http://localhost:8086/ws"
            topics={topics}
            onConnect={onConnected}
            onMessage={onMessageReceived}
            ref={(client) => {
              if (client) {
                  clientRef.current = client;
              }
              //aici dau referinta la acest client de socket
          }}
        />
    </div>
    );
};

export default Chat;
