import React, { Component } from 'react';
import axios from 'axios';
import { toast } from 'react-toastify';
import { HOST_DEVICE} from '../Hosts';
import './InsertUser.css';

class InsertDevice extends Component {
  constructor(props) {
    super(props);
    this.state = {
      adress: '',
      description: '',
      maxHour: '',
      idClient: '',
      successMessage: '',
      errorMessage: '',
    };
  }

  handleInputChange = (event) => {
    const { name, value } = event.target;
    this.setState({ [name]: value });
  }

  handleSubmit = () => {
    const { adress, description, maxHour, idClient } = this.state;

    const deviceData = { adress, description, maxHour, idClient };
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

    axios.post(`${HOST_DEVICE}/save`, deviceData, {headers})
      .then((response) => {
        this.setState({ successMessage: 'Device saved successfully!', errorMessage: '' });
        toast.success("Device saved successfully!");
      })
      .catch((error) => {
        this.setState({ successMessage: '', errorMessage: 'Error insert device' });
        toast.error("Error insert device");
      });
  }

  render() {
    return (
      <div className="insert-device-container">
        <h2 className="form-title">Insert New Device</h2>
        <form>
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
              value={this.state.description}
              onChange={this.handleInputChange}
            />
          </div>
          <div className="form-group">
            <label>maxHour:</label>
            <input
              type="text"
              name="maxHour"
              value={this.state.maxHour}
              onChange={this.handleInputChange}
            />
          </div>
          <div className="form-group">
            <label>Id Client:</label>
            <input
              type="text"
              name="idClient"
              value={this.state.idClient}
              onChange={this.handleInputChange}
            />
          </div>
          <button type="button" onClick={this.handleSubmit}>Insert Device</button>
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

export default InsertDevice;
