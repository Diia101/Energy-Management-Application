import React, { useEffect, useState } from 'react';
import { Table, Button } from 'react-bootstrap';
import axios from 'axios';
import { toast } from 'react-toastify';
import SockJsClient from 'react-stomp';
import { HOST_MEASUREMENT } from "../Hosts";
import { useNavigate } from "react-router-dom";
import Card from 'react-bootstrap/Card';
import MonitoringChart from './MonitoringChart';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';

//preiau idul clientului pe baza usernameului
async function getClientIdByUsername(username) {
  try { //verific daca este un token in localstoarge
    const token = localStorage.getItem('token');
    if (!token) {
      console.error('Token not found in local storage');
      return;
    }

    const headers = {
      'Accept': 'application/json',
      'Content-Type': 'application/json; charset=UTF-8',
      'Authorization': `Bearer ${token}`,
    };

    const response = await axios.get(`http://user-service.localhost/person/getByUsername?username=${username}`, { headers });

    if (response.status === 200) { //daca are succes
      const user = response.data;
      console.log('ID Client:', user.id);
      return user.id; //returnez id
    } else {
      console.error('Failed to fetch user data');
    }
  } catch (error) {
    console.error('Error:', error);
  }
}

const ClientComponent = () => {
  const [devices, setDevices] = useState([]); //lista device ale user
  const [idClient, setIdClient] = useState(localStorage.getItem("iduser")); //idclient din localstorage
  const [selectedDeviceId, setSelectedDeviceId] = useState(null); //id device selectat pt a vedea grafic
  const [selectedDate, setSelectedDate] = useState(new Date()); //data selectata

  const handleLogout = () => {
    localStorage.removeItem('userId');
    window.location.href = 'http://localhost:3000';
  };

  const navigate = useNavigate();

  //preiau dispozitivele utilizatorului
  useEffect(() => {
    const fetchClientId = async () => {
      console.log("vrem id client");
      const username = localStorage.getItem("userData");
      const fetchedId = await getClientIdByUsername(username);
      console.log("fetched id: ", fetchedId);
      if (fetchedId) {
        console.log("l-am gasit!!!!!!! : ", fetchedId);
        setIdClient(fetchedId);
        localStorage.setItem('iduser', fetchedId);
      }
    };

    fetchClientId();
  }, [idClient]);


  //sa nu merg pe pagina de admin daca is client
  useEffect(() => {
    const role = localStorage.getItem("userRole");
    if (role === "ROLE_0") {
      navigate('/');
    }
  }, []);

  useEffect(() => {
    if (idClient) {
      fetchDeviceData();
    }
  }, [idClient]);

  const fetchDeviceData = async () => {
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        console.log('Token not found in local storage');
        return;
      }

      const headers = {
        'Accept': 'application/json',
        'Content-Type': 'application/json; charset=UTF-8',
        'Authorization': 'Bearer ' + token
      };

      const response = await axios.get(`http://device-service.localhost/device/devByUser?idClient=${idClient}`, { headers });
      setDevices(response.data);
    } catch (error) {
      console.error("Error fetching device data:", error);
    }
  };


//verific daca measurement a depasit limita max pe ora si notificare
  const fetchMeasurement = async () => {
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        console.log('Token not found in local storage');
        return;
      }

      const headers = {
        'Accept': 'application/json',
        'Content-Type': 'application/json; charset=UTF-8',
        'Authorization': 'Bearer ' + token
      };

      //aici selectez id-ul la care vreau sa vad graficul
      console.log("selected device id: ", selectedDeviceId);
      const response = await axios.get(`${HOST_MEASUREMENT}/getSum?idDevice=${selectedDeviceId}`, { headers });
      const currentSum = response.data; //obtin consumul curent
      console.log("suma este :", currentSum); //suma din ultima ora

      const maxResponse = await axios.get(`${HOST_MEASUREMENT}/getMaxHourFromDevice?idDevice=${selectedDeviceId}`, { headers });
      const maxHourValue = maxResponse.data; //obtin limita maxima pe ora
      let mesaj_notificare = "Notificare de proba";
      console.log("maxhour value", maxHourValue);
      if (currentSum > maxHourValue) { //compar valorile si notific
        mesaj_notificare = "S-a depasit maxHourConsumption pentru id_device=" + selectedDeviceId; //s a depasit pe ultima ora
        alert("S-a depasit maxHourConsumption!");
        toast.error("S-a depasit maxHourConsumption!");
      } else {
        alert("Suma este sub valoarea maxima pe ora");
        toast.info("Suma este sub valoarea maxima pe ora.");
      }

      const response_mesaj = await axios.post('http://measurements-service.localhost/send', mesaj_notificare, { headers });

      if (response_mesaj.status === 200) {
        console.log('Message sent successfully');
      } else {
        console.error('Failed to send message');
      }
    } catch (error) {
      console.error("Error fetching measurement data:", error);
    }
  };

  useEffect(() => {
    if (selectedDeviceId) {
      fetchMeasurement();
    }
  }, [selectedDeviceId]);

  // WebSocket logic
  const [isMsg, setIsMsg] = useState(false);
  const [message, setMessage] = useState('');
  const [topics, setTopics] = useState([]);

  let onConnected = () => {
    setTopics(['/topic/message']);
  };

  let onDisconnect = () => {
    console.log("DISConnected!!");
  };

  let onMessageReceived = (msg) => {
    setMessage(msg);
    setIsMsg(true);
  };


  return (
      <div className="App">
        <Button onClick={handleLogout} variant="danger" style={{ marginLeft: "20px", "marginTop": "20px" }}>Logout</Button>
        <h2><Button onClick={() => navigate('/chat')}>Chat</Button></h2>
        <div className='user-table'>
          <h1>User Device Page</h1>
          <Table variant='success' striped bordered hover className="small-table">
            <thead>
            <tr>
              <th>#id</th>
              <th>Address</th>
              <th>Description</th>
              <th>Maximum hourly energy consumption</th>
              <th>Action</th>
            </tr>
            </thead>
            <tbody>
            {devices.map((device, index) => (
                <tr key={index}>
                  <td>{device.id}</td>
                  <td>{device.adress}</td>
                  <td>{device.description}</td>
                  <td>{device.maxHour}</td>
                  <td>
                    <Button onClick={() => setSelectedDeviceId(device.id)} variant="info">View Data</Button>
                  </td>
                </tr>
            ))}
            </tbody>
          </Table>
        </div>

        {selectedDeviceId && (
            <div>
              <h3>Select a Day for Historical Data:</h3>
              <DatePicker
                  selected={selectedDate}
                  onChange={(date) => setSelectedDate(date)}
                  dateFormat="yyyy-MM-dd"
              />

              <MonitoringChart
                  selectedDeviceId={selectedDeviceId}
                  selectedDate={selectedDate}
              />
            </div>
        )}

        <SockJsClient
            url={'http://measurements-service.localhost/ws-message'}
            topics={topics} //suboectele pt notificari
            onConnect={onConnected}
            onDisconnect={onDisconnect}
            onMessage={msg => onMessageReceived(msg)} //apelata cand primeste emsaj
            debug={false}
        />

        {isMsg && <Card bg='danger' style={{width: '20rem', marginLeft: '40%', alignContent: 'center'}}>
          <div>
            <Card.Text style={{textAlign: "center", padding: "20px"}}>{message}</Card.Text>
          </div>
        </Card>}
      </div>
  );
};

export default ClientComponent;
