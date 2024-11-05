import React, { useEffect, useState } from 'react';
import { Table, Button } from 'react-bootstrap';
import axios from 'axios';
import { toast } from 'react-toastify';
import SockJsClient from 'react-stomp';
import { HOST_MEASUREMENT } from "../Hosts";
import { useNavigate } from "react-router-dom";
import Card from 'react-bootstrap/Card';

const ClientComponent = () => {
  const [devices, setDevices] = useState([]);
  const [idClient, setIdClient] = useState(localStorage.getItem("iduser"));

  const handleLogout = () => {
    localStorage.removeItem('userId');
    window.location.href = 'http://localhost:3000';
  };
  const navigate = useNavigate();
  useEffect(() => {
    fetchDeviceData();
    fetchMeasurement();
  }, [idClient]);
  const fetchDeviceData = async () => {
    try {
      const token = localStorage.getItem('token');
      const username = localStorage.getItem("userData");
      console.log("lalla")

      if (!token) {
        console.log('Token not found in local storage');
        return;
      }

      const headers = {
        'Accept': 'application/json',
        'Content-Type': 'application/json; charset=UTF-8',
        'Authorization': 'Bearer ' + token
      };

      const response = await axios.get(`http://localhost:8081/device/devByUser?idClient=${idClient}`, { headers });
      setDevices(response.data);
      console.log(response.data)
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

      const maxResponse = await axios.get(`${HOST_MEASUREMENT}/getMaxHourFromDevice?idDevice=12`, { headers });
      const maxHourValue = maxResponse.data;

      if (currentSum > maxHourValue) {
        toast.error("S-a depasit maxHourConsumption!");
      } else {
        toast.info("Suma este sub valoarea maxima pe ora.");
      }
    } catch (error) {
      console.error("Error fetching measurement data:", error);
    }
  };

  // Parte websockets
  const [isMsg, setIsMsg] = useState(false);
  const [message, setMessage] = useState('');
  const [topics, setTopics] = useState([]);

  let onConnected = () => {
    console.log("Connected!!")
    setTopics(['/topic/message']);
  }

  let onDisconnect = () => {
    console.log("DISConnected!!")
  }

  let onMessageReceived = (msg) => {
    setMessage(msg)
    setIsMsg(true)

    //setTimeout
  };

  return (
    <div className="App">
      <Button onClick={handleLogout} variant="danger" style={{ marginLeft: "20px", "marginTop": "20px" }}>Logout</Button>
      {/*<h2><Button onClick={() => navigate(`/chat`)}>Chat</Button></h2>*/}
      <div className='user-table'>
        <h1>User Device Page</h1>
        <Table variant='success' striped bordered hover className="small-table">
          <thead>
            <tr>
              <th>#id</th>
              <th>Address</th>
              <th>Description</th>
              <th>Maximum hourly energy consumption</th>
            </tr>
          </thead>
          <tbody>
            {devices.map((device, index) => (
              <tr key={index}>
                <td>{device.id}</td>
                <td>{device.adress}</td>
                <td>{device.description}</td>
                <td>{device.maxHour}</td>
              </tr>
            ))}
          </tbody>
        </Table>
      </div>
      <SockJsClient
        url={'http://localhost:8082/ws-message'}
        topics={topics}
        onConnect={onConnected}
        onDisconnect={onDisconnect}
        onMessage={msg => onMessageReceived(msg)}
        debug={false}
      />
      {isMsg && <Card bg='danger' style={{ width: '20rem', marginLeft: '40%', alignContent: 'center' }}>
        <div>
          <Card.Text style={{ textAlign: "center", padding: "20px" }} >{message}</Card.Text>
        </div>
      </Card>}
    </div>
  );
};

export default ClientComponent;
