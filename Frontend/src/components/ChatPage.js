import React, { useState, useEffect, useRef } from 'react';
import SockJsClient from 'react-stomp';
import { useStompClient } from 'react-stomp-hooks';
import '../App.css';
import { Button } from 'react-bootstrap';
import { Form } from 'react-bootstrap';

const Chat = () => {
    const [messages, setMessages] = useState([]); //lista mesajelor din chat
    const [messageInput, setMessageInput] = useState(''); //textuk introdus de user in campu de mesaje
    const [username, setUsername] = useState(''); //numele userului curent
    const [topics, setTopics] = useState([]); //topics pt abonarea la websocket
    const [isSeen, setIsSeen] = useState(false); //daca a fost vazut mesajul
    const [isTyping, setIsTyping] = useState(false); //daca scrie

    //obtin acces la o instanta a clientului websocket configurat anterior
    const chatClient = useStompClient();

//trimit mesaju; notific in timp real clientii conectati la socket cand focusez
    const handleFocus = () => {
        if (chatClient) { //daca instanta chatClient e disponibila si conectata
            chatClient.publish({
                destination: '/topic/seen', // specific topicul websocketului
                //mesajul include numele utilizatorului care da seen si seenul
                body: JSON.stringify({ sender: username, recipient: '/topic/seen' }),
            });
        }
    };


    //clientRef este un obiect de referință (Ref)
    //referinta catre websocket pt trimiterea mesajelor
    const clientRef = useRef(null);
    

    useEffect(() => {
         // utilzizez useEffect pentru a seta username-ul când componenta este randata
        setUsername(localStorage.getItem('userData'));

         // fct returnata va fi apelată când componenta este dezactivată, inchidem conexiunea
        return () => {
            if (clientRef.current && clientRef.current.deactivate) {
                clientRef.current.deactivate();
            }
        };
    }, []);

     // functie pt a actualiza mesajul campului de text
    const handleMessageChange = (e) => {
        setMessageInput(e.target.value);
    };

      // fct pentru a trimite mesajul
    const handleSendMessage = () => {
        const message = { //obiect message care contine
            sender: username, //utilizator curent
            receiver: 'admin', //destianatarul mesajului implicit admin
            content: messageInput, //textu mesajului
        };

        //sendMessage este o metodă specifică a acestui client WebSocket (SockJsClient) 
        //care este utilizată pentru a trimite mesaje către serverul WebSocket
        if (clientRef.current && clientRef.current.sendMessage) {
            clientRef.current.sendMessage('/app/chat', JSON.stringify(message)); //trimit mesajul catre websocket pe sub /app/chat
        }

        //golim mesajul dupa ce l-am trimis
        setMessageInput('');
    };

    // cand primim mesaj, pe langa cele pe care le avem deja vrem sa le pastram
    //de asta punem cu .. inainte, asta inseamna ca la array-ul de mesaje deja existent, mai adaugam pe cel pe care l-am primit
    const onMessageReceived = (msg) => {
        console.log("msg: ", msg); //msg e mesajul primit de la server prin websocket
//aici il receptionez
        if (msg.recipient === '/topic/seen') {
            console.log('topic seen');
            if (msg.sender !== username) {
                setIsSeen(true);
                resetSeenStatus();
            }
        } else { //daca e pe alt topic, cel clasic de mesaje
            setMessages([...messages, msg]); //adaug mesajul in lista de mesaje(... pastrez mesajele vechi)
            console.log('username', username);

            resetSeenStatus();
        }
    };

    //cand apas de campul de text(sa scriu mesaj)
    const handleChange = (event) => {
        handleMessageChange(event); //actualizez textul
        setIsTyping(true);          //activez indicatorul typing
        handleTyping();             //resetez inidcatorul dupa 2 secunde
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
      }, 2000); //e fals dupa 2 sec de inactivitate
      
   
      const onTextChange = (event) => {
        setIsTyping(true);
        handleTyping();
      };

      const resetSeenStatus = () => {
        setTimeout(() => {
          setIsSeen(false);
        }, 2000); 
      };
  
 //conectarea cu websocket; setez subiectele pt abonare
    let onConnected = () => {
      setTopics(['/topic/messages','/topic/seen'])
      console.log("Connected!!")
    }


//un server la care se conecteaza toti userii la socket
    return (
        <div className="chat-container">
            {/*lista de mesaje*/}
        <div className="message-container">
            {messages.map((msg, index) => ( //parcurg lista de messages
                <div
                    key={index} //indexul unic al fiecarui mesaj
                    //aici am vrut sa pun clase diferite pt mesajele mele vs cele primite
                    className={`message ${msg.sender === username ? 'sender-me' : 'sender-other'}`}
                >
                    {msg.sender}: {msg.content} {/*afisez expeditorul mesajului si cu mesaju lui*/}
                    {isSeen && index === messages.length -1 && <div>Seen</div>} {/*sa apara seen la ultimul mesaj*/}
  {isTyping && msg.sender.toUpperCase() !== username.toUpperCase() &&  messages.length -1 && <div>Typing...</div>}
                </div>
            ))}
        </div>
        <Form>
            {/*camp de text si buton de trimitere*/}
                <Form.Control
                    type="text"
                    value={messageInput} //continutu campului e sincronizat cu messageInput
                    onChange={handleChange}
                    onFocus={handleFocus}
                    placeholder="Type your message..."
                    style={{ marginTop:"5px"}}
                    className="bg-dark text-light" 
                

                />
         </Form>
        <Button style={{marginTop:"5px"}} variant='dark' onClick={handleSendMessage}>Send</Button>

{/*stabilesc conexiunea la websocket*/}
        <SockJsClient
            url="http://chat-service.localhost/ws" //url serverului websocket la care se conecteaza
           // url="http://localhost:8086/ws"
            topics={topics} //lista de subiecte la care clientul e abonat
            onConnect={onConnected}
            onMessage={onMessageReceived}
            ref={(client) => {
              if (client) {
                  clientRef.current = client; //leg referinta la instanta socketjsclient
              }
              //aici dau referinta la acest client de socket
          }}
        />
    </div>
    );
};

export default Chat;
