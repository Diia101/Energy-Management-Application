import React, { useState } from "react";
import "./Login.css";
import axios from "axios";
import { toast } from "react-toastify";
import { HOST_PERSON } from "../Hosts";
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';



const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [userData, setUserData] = useState([]);
  const navigate = useNavigate();

  const mapRole = (role) => {
    if (userData.role === 0) {
      navigate("/admin")
      toast.success("Logged in successfully as ADMIN!");
    } else {
      navigate("/home");
      toast.success("Logged in successfully (home)!");
    }
  };

  function handleLogin  (event){
  //  console.log("aa");
    
    event.preventDefault()
    const usernameQueryParam = encodeURIComponent(username);
    const passwordQueryParam = encodeURIComponent(password);

    axios.post(`${HOST_PERSON}/login`, {
      username: usernameQueryParam,
      password: passwordQueryParam
    })
    .then((response) => {
      const token = response.data.token;
      if (token) {
        const decoded = jwtDecode(token);
        console.log(decoded); 
        console.log("eeeeeeeeee");
      } else {
        console.error("No token received");
      }
      const decoded = jwtDecode(token);
      const user = {
        username: decoded.sub, // Make sure this is included in the JWT
        role: decoded.role, // Make sure this is included in the JWT
        id: decoded.id
      };

      localStorage.setItem("userRole", decoded.role);
    
  // Note: Password should not be handled in this manner; it's shown here for continuity.
  console.log("token:" + token);
 // console.log("decode:" + decoded);
  console.log("user:" + user.username);
     
       
        const clientJSON = JSON.stringify(user)
        console.log("response:", response);
        console.log("role: " + user.role);
        console.log("idul: " + user.id)
        if (user.role === 0) {
          console.log(user.id)
          //localStorage.setItem("username", user.id)
          localStorage.setItem("userData", user.username);
          localStorage.setItem("token", token);
          navigate("/admin");
          toast.success("Logged in successfully as ADMIN!");
          console.log("BBBBBBBBBBBBBBBBBBBBBBBB")
        } else if (user.role === 1) {
          localStorage.setItem("iduser",user.id)
          localStorage.setItem("userData", user.username);
          localStorage.setItem("token", token);
          navigate("/client");
          toast.success("Logged in successfully as CLIENT!");
        } else {
          alert("error")
          console.log("noooooo")
          console.log(user.role)
        }
      })
      .catch((error) => {
        toast.error("Error logging in");
      });
  };

  return (
    <>
      <form>
        <label htmlFor="username">Username</label>
        <input
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          type="text"
          id="username"
        />
        <label htmlFor="password">Password</label>
        <input
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          type="password"
          id="password"
        />
        <button variant="success" onClick={handleLogin}>
          Login
        </button>
      </form>
    </>
  );
};

export default Login;
