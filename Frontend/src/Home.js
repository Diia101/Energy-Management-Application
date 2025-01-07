import React from "react";
import './App.css';
import { Component } from "react";  
import 'bootstrap/dist/css/bootstrap.min.css';
import UserTable from "./components/UserTable";
import 'react-toastify/dist/ReactToastify.css';
import { ToastContainer} from 'react-toastify';
class Home extends Component {

  render() {
    return (
      <div >
      <div >
      <ToastContainer autoClose={3000} />
        <div className="App" >
          <div className='user-table' >
            <UserTable />
          </div>
        </div>
      </div>
      </div>
    );
  }
}

export default Home;