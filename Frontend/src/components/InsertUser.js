import React, { Component } from 'react';
import axios from 'axios';
import { toast } from 'react-toastify';
import { HOST_PERSON } from '../Hosts';
import './InsertUser.css';

class InsertUser extends Component {
  constructor(props) {
    super(props);
    this.state = {
      username: '',
      password: '',
      role: '',
      successMessage: '',
      errorMessage: '',
    };
  }

  handleInputChange = (event) => {
    const { name, value } = event.target;
    this.setState({ [name]: value });
  }

  handleSubmit = () => {
    const { username, password, role } = this.state;
    const userData = { username, password, role };

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

    axios.post(`${HOST_PERSON}/save`, userData, { headers })
      .then((response) => {
        this.setState({ successMessage: 'User saved successfully!', errorMessage: '' });
        toast.success("User saved successfully!");
      })
      .catch((error) => {
        console.error(error);
        this.setState({ successMessage: '', errorMessage: 'Error user insert' });
        toast.error("Error user insert");
      });
}

  render() {
    return (
      <div className="insert-user-container">
        <h2 className="form-title">Insert New User</h2>
        <form>
          <div className="form-group">
            <label>Username:</label>
            <input
              type="text"
              name="username"
              value={this.state.username}
              onChange={this.handleInputChange}
            />
          </div>
          <div className="form-group">
            <label>Password:</label>
            <input
              type="password"
              name="password"
              value={this.state.password}
              onChange={this.handleInputChange}
            />
          </div>
          <div className="form-group">
            <label>Role:</label>
            <input
              type="text"
              name="role"
              value={this.state.role}
              onChange={this.handleInputChange}
            />
          </div>
          <button type="button" onClick={this.handleSubmit}>Insert User</button>
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

export default InsertUser;
