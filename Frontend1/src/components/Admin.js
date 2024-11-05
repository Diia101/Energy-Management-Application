import React, { useEffect, useState } from "react";
import axios from "axios";
import { Table, Button } from 'react-bootstrap';
import { HOST_PERSON, HOST_DEVICE } from "../Hosts";
import { toast } from 'react-toastify';
import './Admin.css';
import { useNavigate } from 'react-router-dom';
import Chat from "./ChatPage";

const Admin = () => {
  const openInNewTab = (url) => {
    window.open(url, '_blank', 'noopener,noreferrer');
  };

  const [userData, setUserData] = useState([]);
  const[deviceData, setDeviceData] = useState([]);
  const navigate = useNavigate();
  const handleLogout = () => {
    localStorage.removeItem('userData');
    localStorage.removeItem('token');
    navigate('/');
};

  useEffect(() => {
    let isRendered = true;
    const userRole = localStorage.getItem('userRole');
    console.log(userRole, typeof userRole, '8serrole');
    if (userRole === '1') {
      handleLogout()

    }
    var obj = localStorage.getItem('token');

    if (!obj) {
      console.log('Token not found in local storage');
      
    }
    let headers2 = {
      "headers": {
        'Accept': 'application/json',
        'Content-Type': 'application/json; charset=UTF-8',
        'Authorization': 'Bearer ' + obj
      }
    };

    axios.get(`${HOST_PERSON}/all`,headers2)
      .then((response) => {
        if (isRendered) {
          setUserData(response.data);
          toast.success("User data fetched successfully!");
        }
      })
      .catch((error) => {
        if (isRendered) {
          toast.error("Error fetching user data");
        }
      });

    return () => {
      isRendered = false;
    };
  }, []);

  const DeviceComponent = () => {
    const [deviceData, setDeviceData] = useState([]);
  
    useEffect(() => {
      let isRendered = true;
  
      axios.get(`${HOST_DEVICE}/all`)
        .then((response) => {
          if (isRendered) {
            setUserData(response.data);
            toast.success("Device data fetched successfully!");
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
    }, []);
  
   
  };
  function mapRole(role) {
    return role === 0 ? "ADMIN" : "CLIENT";
  }

  const handleInsert = () => {
    let isRendered = true;
    var obj = localStorage.getItem('token');
    if (!obj) {
      console.log('Token not found in local storage');
      
    }
    let headers2 = {
      "headers": {
        'Accept': 'application/json',
        'Content-Type': 'application/json; charset=UTF-8',
        'Authorization': 'Bearer ' + obj
      }
    };


    axios.post(`${HOST_PERSON}/save`,headers2)
      .then((response) => {
        if (isRendered) {
          setUserData(response.data);
          toast.success("User saved successfully!");
        }
      })
      .catch((error) => {
        if (isRendered) {
          toast.error("Error user insert");
        }
      });
  };

  const handleInsert1 = () => {
    let isRendered = true;
    const token = localStorage.getItem('token');
    if (!token) {
      console.log('Token not found in local storage');
   
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
          setUserData(response.data);
          toast.success("Device saved successfully!");
        }
      })
      .catch((error) => {
        if (isRendered) {
          toast.error("Error device insert");
        }
      });
  };

  const editUser = (user) => {
    navigate('/EditUser');
    const id = user.id;
    const username = user.username;
    const password = user.password;

    axios.post(`${HOST_PERSON}/update`, {
      id: id,
      username: username,
      password: password,
    })
      .then((response) => {
        setUserData(response.data);
        toast.success("User updated successfully!");
      })
      .catch((error) => {
        toast.error("Error user update");
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
    axios.delete(`${HOST_PERSON}/delete?id=${id}`, {headers})
      .then((response) => {
          toast.success("User deleted successfully!");
          axios.delete(`${HOST_DEVICE}/deleteByClientId?idClient=${id}`,{headers})
              .then((response)=> {
                toast.success("Device deleted")
                console.log("mergi mai odata")
              })
              .catch((error)=> {
                toast.error("Error deleting devices");
              })
          window.location.reload(true);
      })
      .catch((error) => {
          toast.error("Error deleting user");
      });
  };

  const handleDeleteDevice = (id) => {
    let isRendered = true;
    axios.delete(`${HOST_DEVICE}/delete?id=${id}`)
      .then((response) => {
        if (isRendered) {
          setUserData(response.data);
          toast.success("Device deleted successfully!");
        }
      })
      .catch((error) => {
        if (isRendered) {
          toast.error("Error deleting device");
        }
      });

   
  };

  return (
    <div className="admin-container">
      <h1>Admin Page</h1>
      <div className="button-container">
        <Button variant="success" onClick={() => navigate("/insertUser")}>
          Insert User
        </Button>
        <Button variant="success" onClick={() => navigate("/adminDevice")}>
          Device
        </Button>
        <Button onClick={handleLogout} variant="danger" style={{marginLeft:"20px","marginTop":"20px"}}>Logout</Button>
        
                {/*<Button onClick={() => navigate(`/chat`)}>Chat</Button>*/}
      </div>
      <Table striped bordered hover responsive className="device-table">
        <thead>
          <tr>
            <th>#ID</th>
            <th>Username</th>
            <th>Password</th>
            <th>Role</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {userData.map((user, index) => (
            <tr key={index}>
              <td>{user.id}</td>
              <td>{user.username}</td>
              <td>{user.password}</td>
              <td>{mapRole(user.role)}</td>
              <td>
                <Button variant="primary" onClick={() => navigate('/editUser')}>
                  Edit
                </Button>
                <Button variant="danger" onClick={() => handleDelete(user.id)}>
                  Delete
                </Button>
                {/*<Button onClick={() => navigate(`/chat`)}>Chat</Button>*/}
        
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </div>
  );
};

export default Admin;
