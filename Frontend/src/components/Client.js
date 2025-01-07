import React, { useEffect, useState } from 'react';
import { Table, Button } from 'react-bootstrap';
import axios from 'axios';
import { toast } from 'react-toastify';
import SockJsClient from 'react-stomp';
import { HOST_MEASUREMENT } from "../Hosts";
import { useNavigate } from "react-router-dom";
import Card from 'react-bootstrap/Card';
import MonitoringChart from './MonitoringChart'; // Importă componenta MonitoringChart



// // Funcție pentru a obține idClient după username
// async function getClientIdByUsername(username) {
//   try {
//     const response = await fetch(`http://user-service.localhost/person/getByUsername?username=${username}`, {
//       method: 'GET',
//       headers: {
//         'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
//       }
//     });
//
//     if (response.ok) {
//       const user = await response.json();
//       console.log('ID Client:', user.id);
//       return user.id;
//     } else {
//       console.error('Failed to fetch user data');
//     }
//   } catch (error) {
//     console.error('Error:', error);
//   }
// }


  async function getClientIdByUsername(username) {
    try {
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

      if (response.status === 200) {
        const user = response.data;
        console.log('ID Client:', user.id);
        return user.id;
      } else {
        console.error('Failed to fetch user data');
      }
    } catch (error) {
      console.error('Error:', error);
    }
  }

const ClientComponent = () => {
  const [devices, setDevices] = useState([]);
  const [idClient, setIdClient] = useState(localStorage.getItem("iduser"));
  const [selectedDeviceId, setSelectedDeviceId] = useState(null); // Adăugați starea pentru dispozitivul selectat

  const handleLogout = () => {
    localStorage.removeItem('userId');
    window.location.href = 'http://localhost:3000';
  };

  const navigate = useNavigate();

  useEffect(() => {
    const fetchClientId = async () => {
      console.log("mergi sau ciau?");
      // if (idClient===undefined) {
        console.log("vrem id client");
        const username = localStorage.getItem("userData");
        const fetchedId = await getClientIdByUsername(username);
        console.log("fetched id: ", fetchedId);
        if (fetchedId) {
          console.log("l-am gasit!!!!!!! : ", fetchedId);
          setIdClient(fetchedId);
          localStorage.setItem('iduser', fetchedId);
        }
      // }
    };

    fetchClientId();
  }, [idClient]);

  useEffect(() => {
    const role = localStorage.getItem("userRole");
    if (role === "ROLE_0") {
      navigate('/')
    }
  },[])

  useEffect(() => {
    if (idClient) {
      fetchDeviceData();
      fetchMeasurement();
    }
  }, [idClient]);

  const fetchDeviceData = async () => {
    try {
      const token = localStorage.getItem('token');
      console.log("token: ", token);
      if (!token) {
        console.log('Token not found in local storage');
        return;
      }

      const headers = {
        'Accept': 'application/json',
        'Content-Type': 'application/json; charset=UTF-8',
        'Authorization': 'Bearer ' + token
      };
      console.log("id client:", idClient);
      console.log("tipul e:", typeof(idClient));

      const response = await axios.get(`http://device-service.localhost/device/devByUser?idClient=${idClient}`, { headers });
      setDevices(response.data);
    } catch (error) {
      console.error("Error fetching device data:", error);
    }
  };

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

      const response = await axios.get(`${HOST_MEASUREMENT}/getSum?idDevice=12`, { headers });
      const currentSum = response.data;
      console.log("suma este :", currentSum);

      const maxResponse = await axios.get(`${HOST_MEASUREMENT}/getMaxHourFromDevice?idDevice=12`, { headers });
      const maxHourValue = maxResponse.data;

      console.log("maxhour value", maxHourValue);
      if (currentSum > maxHourValue) {
        alert("S-a depasit maxHourConsumption!");
        toast.error("S-a depasit maxHourConsumption!");
      } else {
        alert("Suma este sub valoarea maxima pe ora");
        toast.info("Suma este sub valoarea maxima pe ora.");
      }
    } catch (error) {
      console.log("eroare");
      console.error("Error fetching measurement data:", error);
    }
  };

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
        <Button onClick={handleLogout} variant="danger"
                style={{marginLeft: "20px", "marginTop": "20px"}}>Logout</Button>
        <h2><Button onClick={() => navigate(`/chat`)}>Chat</Button></h2>
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

        {selectedDeviceId &&
            <MonitoringChart selectedDeviceId={selectedDeviceId}/>} {/* Aici se adaugă componenta MonitoringChart */}

        <SockJsClient
            url={'http://measurements-service.localhost/ws-message'}
            topics={topics}
            onConnect={onConnected}
            onDisconnect={onDisconnect}
            onMessage={msg => onMessageReceived(msg)}
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
