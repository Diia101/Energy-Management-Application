import React, { useEffect, useState } from "react";
import axios from "axios";
import {Table} from 'react-bootstrap'
import { HOST_PERSON } from "../Hosts";
import {  toast } from 'react-toastify';

const UserTable = () => {
const [userData, setUserData] = useState([]);
  
  useEffect(() => {
    let isRendered = true;

    axios.get(`${HOST_PERSON}/all`)
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

  function mapRole(role) {
    return role === 0 ? "ADMIN" : "CLIENT";
  }

  return (
    <Table variant='success' striped bordered hover className="small-table">
      <thead>   
        <tr>
          <th>#id</th>
          <th>Username</th>
          <th>Password</th>
          <th>Role</th>
        </tr>
      </thead>
      <tbody>
        {userData.map((user, index) => (
          <tr key={index}>
            <td>{user.id}</td>
            <td>{user.username}</td>
            <td>{user.password}</td>
            <td>{mapRole(user.role)}</td>
          </tr>
        ))}
      </tbody>
    </Table>
  );
};

export default UserTable;