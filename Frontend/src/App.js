import './App.css';
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate 
} from "react-router-dom";
import { useState, useEffect } from 'react';
import { StompSessionProvider, useSubscription } from "react-stomp-hooks";

import Home from "./Home";
import Admin from "./components/Admin";
import InsertUser from "./components/InsertUser";
import EditUser from "./components/EditUser";
import AdminDevice from "./components/AdminDevice";
import EditDevice from "./components/EditDevice";
import InsertDevice from "./components/InsertDevice";
import Login from "./components/Login";
import Client from "./components/Client";

import PrivateRoute from "./components/PrivateRoute";
import ChatPage from './components/ChatPage';
import MessageComposer from './components/MessageComposer';
import TypingIndicator from './components/TypingIndicator';


function App() {
  return (
    <div>
      <StompSessionProvider
          url="http://chat-service.localhost/ws"
          debug={(str) => console.log(str)}
      >
      <Router>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/client" element={<PrivateRoute element={<Client />} />} />
          <Route path="/admin" element={<PrivateRoute element={<Admin />} />} />
          <Route path="/insertUser"  element={<InsertUser />} />
          <Route path="/editUser" element={<PrivateRoute element={<EditUser />} />} />
          <Route path="/adminDevice" element={<PrivateRoute element={<AdminDevice />} />} />
          <Route path="/editDevice" element={<PrivateRoute element={<EditDevice />} />} />
          <Route path="/insertDevice" element={<PrivateRoute element={<InsertDevice />} />} />
          <Route path="/chat" element={<ChatPage />} />

        </Routes>
      </Router>
      </StompSessionProvider>
    </div>
  );
  // return<>
  //  <MessageComposer />
  //   <TypingIndicator/>
  // </>
   
}


export default App;
