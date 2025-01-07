import React, { Component } from 'react';
import axios from 'axios';
import { toast } from 'react-toastify';
import { HOST_DEVICE, HOST_PERSON } from '../Hosts';
import './InsertUser';
import './AdminDevice';
import { useNavigate } from 'react-router-dom';

class UpdateDevice extends Component {
  constructor(props) {
    super(props);
    this.state = {
      id:'',
      adress: '',
      description: '',
      maxHour:'',
      successMessage: '',
      errorMessage: '',
    };
  }

  handleInputChange = (event) => {
    const { name, value } = event.target;
    this.setState({ [name]: value });
  }

  handleSubmit = () => {
    const { id, adress, description, maxHour } = this.state;

    const deviceData = { id, adress, description, maxHour }; 
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

  
    axios.post(`${HOST_DEVICE}/update?id=${id}&adress=${adress}&description=${description}&maxHour=${maxHour}`, {headers})
      .then((response) => {
        
        this.setState({ successMessage: 'Device updated successfully!', errorMessage: '' });
        toast.success("Device updated successfully!");
       
      })
      .catch((error) => {
        this.setState({ successMessage: '', errorMessage: 'Error updating device' });
        toast.error("Error updating device");
      });
  }

  render() {
    return (
      <div className="update-device-container">
        <h2 className="form-title">Update Device</h2>
        <form>
          <div className="form-group">
            <label>Device ID:</label>
            <input
              type="text"
              name="id"
              value={this.state.id}
              onChange={this.handleInputChange}
            />
          </div>
          <div className="form-group">
            <label>Address:</label>
            <input
              type="text"
              name="adress"
              value={this.state.adress}
              onChange={this.handleInputChange}
            />
          </div>
          <div className="form-group">
            <label>Description:</label>
            <input
              type="text"
              name="description"
              value={this.state.password}
              onChange={this.handleInputChange}
            />
          </div>
          <div className="form-group">
            <label>Consumption -- maxHours:</label>
            <input
              type="number"
              name="maxHour"
              value={this.state.maxHour}
              onChange={this.handleInputChange}
            />
          </div>
          <button type="button" onClick={this.handleSubmit}>Update Device</button>
        </form>

        {this.state.successMessage && (
          <div className="success-message">
            {this.state.successMessage}
          </div>
        )}

        {this.state.errorMessage && (
          <div className="error-message">
            {this.state.errorMessage}
          </div>
        )}
      </div>
    );
  }
}

export default UpdateDevice;
