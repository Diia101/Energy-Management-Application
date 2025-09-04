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
  const navigate = useNavigate();


  // const mapRole = (role) => {
  //   if (userData.role === 0) {
  //     navigate("/admin")
  //     toast.success("Logged in successfully as ADMIN!");
  //   } else {
  //     navigate("/home");
  //     toast.success("Logged in successfully (home)!");
  //   }
  // };

  const handleLogin = (event) => {
    event.preventDefault();

    const usernameQueryParam = encodeURIComponent(username);
    const passwordQueryParam = encodeURIComponent(password);

    axios.post(`${HOST_PERSON}/login`, {
      username: usernameQueryParam,
      password: passwordQueryParam
    })
        .then((response) => {
          const token = response.data.token;
          console.log("token: ", token);
          if (token) {
            const decoded = jwtDecode(token);
            const user = {
              username: decoded.sub,
              role: decoded.role,
              id: decoded.id,
            };

            // salvarea în localStorage
            localStorage.setItem("userRole", user.role);
            localStorage.setItem("userData", user.username);
            localStorage.setItem("token", token);
            localStorage.setItem("iduser", user.id);
            console.log("rolu ii:", user.role);
            console.log("tipul e:", typeof(user.role));
            console.log("Primul element din user.role:", user.role[0]);
            console.log("id client: ", user.id);
            // Navigare în funcție de rol
            if (user.role[0] === 'ROLE_0') {
              navigate("/admin");
              toast.success("Logged in successfully as ADMIN!");
            } else if (user.role[0] === "ROLE_1") {
              localStorage.setItem("iduser", user.id);
              navigate("/client");
              toast.success("Logged in successfully as CLIENT!");
            } else {
              toast.error("Unknown role");
            }
          } else {
            console.error("No token received");
            toast.error("No token received");
          }
        })
        .catch((error) => {
          console.error("Login error:", error);
          toast.error("Error logging in");
        });
  };

  //
  // const handleLogin = (event) => {
  //   event.preventDefault();
  //
  //   axios.post(`${HOST_PERSON}/login`, {
  //     username: username,
  //     password: password
  //   })
  //       .then((response) => {
  //         const token = response.data.token;
  //
  //         if (token) {
  //           const decoded = jwtDecode(token);
  //           const user = {
  //             username: decoded.sub,
  //             role: decoded.role,
  //             id: decoded.id,
  //           };
  //
  //           // Salvarea în localStorage
  //           localStorage.setItem("userRole", user.role);
  //           localStorage.setItem("userData", user.username);
  //           localStorage.setItem("token", token);
  //
  //           // Navigare în funcție de rol
  //           if (user.role === 0) {
  //             navigate("/admin");
  //             toast.success("Logged in successfully as ADMIN!");
  //           } else if (user.role === 1) {
  //             localStorage.setItem("iduser", user.id);
  //             navigate("/client");
  //             toast.success("Logged in successfully as CLIENT!");
  //           } else {
  //             toast.error("Unknown role");
  //           }
  //         } else {
  //           console.error("No token received");
  //           toast.error("No token received");
  //         }
  //       })
  //       .catch((error) => {
  //         console.error("Login error:", error);
  //         toast.error("Error logging in");
  //       });
  // };

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
          <button type="button" onClick={handleLogin}>
            Login
          </button>
        </form>
      </>
  );
};

export default Login;
