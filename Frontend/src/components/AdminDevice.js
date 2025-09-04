import React, { useEffect, useState } from "react";
import axios from "axios";
import { Table, Button } from 'react-bootstrap';
import { HOST_PERSON, HOST_DEVICE } from "../Hosts";
import { toast } from 'react-toastify';
import './Admin.css';
import { useNavigate } from 'react-router-dom';

const AdminDevice = () => {
  const openInNewTab = (url) => {
    window.open(url, '_blank', 'noopener,noreferrer');
  };

  const navigate = useNavigate();
  const[deviceData, setDeviceData] = useState([]);


  function getAll() {
    let isRendered = true;
    const token = localStorage.getItem('token');
    if (!token) {
      console.log('Token not found in local storage');
      // Handle the case where token is not available
      return;
    }

    let headers = {
      'Accept': 'application/json',
      'Content-Type': 'application/json; charset=UTF-8',
      'Authorization': 'Bearer ' + token
    };
    axios
      .get(`${HOST_DEVICE}/all`, {headers})
      .then((response) => {
        const devices = response.data;
        if (devices.length > 0) {
          const firstDevice = devices[0]; 
          localStorage.setItem("iddevice", firstDevice.id);
          if (isRendered) {
            setDeviceData(response.data);
            toast.success("Device data fetched successfully!");
          }
        }
      })
      .catch((error) => {
        if (isRendered) {
          toast.error("Error fetching device data");
        }
      });
  
  
    return () => {
      isRendered = false;
    };
  };


  const handleInsert = () => {
    let isRendered = true;
    const token = localStorage.getItem('token');
    if (!token) {
      console.log('Token not found in local storage');
      // Handle the case where token is not available
      return;
    }

    let headers = {
      'Accept': 'application/json',
      'Content-Type': 'application/json; charset=UTF-8',
      'Authorization': 'Bearer ' + token
    };

    axios.post(`${HOST_DEVICE}/save`, {headers})
      .then((response) => {
        if (isRendered) {
          setDeviceData(response.data);
          toast.success("User saved successfully!");
        }
      })
      .catch((error) => {
        if (isRendered) {
          toast.error("Error user insert");
        }
      });
  };

  const editDevice = (device) => {
    const id = device.id;
    const adress = device.adress;
    const description = device.description;
    const maxHour = device.maxHour;
    const token = localStorage.getItem('token');
    if (!token) {
      console.log('Token not found in local storage');
      // Handle the case where token is not available
      return;
    }

    let headers = {
      'Accept': 'application/json',
      'Content-Type': 'application/json; charset=UTF-8',
      'Authorization': 'Bearer ' + token
    };

    axios.post(`${HOST_DEVICE}/update`, {
      id: id,
      adress: adress,
      description: description,
      maxHour: maxHour
    }, {headers})
      .then((response) => {
        setDeviceData(response.data);
        toast.success("Device updated successfully!");
      })
      .catch((error) => {
        toast.error("Error device update");
      });
  };

  function handleDelete (id) {
    const token = localStorage.getItem('token');
    if (!token) {
      console.log('Token not found in local storage');
      // Handle the case where token is not available
      return;
    }

    let headers = {
      'Accept': 'application/json',
      'Content-Type': 'application/json; charset=UTF-8',
      'Authorization': 'Bearer ' + token
    };
    axios.delete(`${HOST_DEVICE}/delete?id=${id}`, {headers})
      .then((response) => {
          toast.success("Device deleted successfully!");
          window.location.reload(true);
      })
      .catch((error) => {
          toast.error("Error deleting device");
      });
  };

  
  return (
    <div className="admin-device-container">
    <h1>Admin Page</h1>
    <div className="button-container">
      <Button variant="success" onClick={() => getAll()}>See all devices</Button>
      <Button variant="success" onClick={() => navigate("/InsertDevice")}>
        Insert Device
      </Button>
      <Button variant="success" onClick={() => navigate('/Admin')}>
        Back
      </Button>
    </div>
    <Table striped bordered hover responsive className="device-table">
      <thead>
        <tr>
          <th>#ID</th>
          <th>Address</th>
          <th>Description</th>
          <th>Max Hour</th>
          <th>Id Client</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        {deviceData.map((device, index) => (
          <tr key={index}>
            <td>{device.id}</td>
            <td>{device.adress}</td>
            <td>{device.description}</td>
            <td>{device.maxHour}</td>
            <td>{device.idClient}</td>
            <td>
              <Button variant="primary" onClick={() => navigate('/EditDevice')}>
                Edit
              </Button>
              <Button variant="danger" onClick={() => handleDelete(device.id)}>
                Delete
              </Button>
            </td>
          </tr>
        ))}
      </tbody>
    </Table>
  </div>
);
};

export default AdminDevice;
