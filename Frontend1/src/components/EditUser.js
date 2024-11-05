import React, { Component } from 'react';
import axios from 'axios';
import { toast } from 'react-toastify';
import { HOST_PERSON } from '../Hosts';
import './InsertUser';

class UpdatePerson extends Component {
  constructor(props) {
    super(props);
    this.state = {
      id: '',
      username: '',
      password: '',
      successMessage: '',
      errorMessage: '',
    };
  }

  handleInputChange = (event) => {
    const { name, value } = event.target;
    this.setState({ [name]: value });
  }

  handleSubmit = () => {
    const { id, username, password } = this.state;

    const userData = { id, username, password };

    axios.post(`${HOST_PERSON}/update?id=${id}&password=${password}&username=${username}`)
      .then((response) => {
        this.setState({ successMessage: 'Person updated successfully!', errorMessage: '' });
        toast.success("Person updated successfully!");
      })
      .catch((error) => {
        this.setState({ successMessage: '', errorMessage: 'Error updating person' });
        toast.error("Error updating person");
      });
  }

  render() {
    return (
      <div className="update-person-container">
        <h2 className="form-title">Update Person</h2>
        <form>
          <div className="form-group">
            <label>Person ID:</label>
            <input
              type="text"
              name="id"
              value={this.state.id}
              onChange={this.handleInputChange}
            />
          </div>
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
          <button type="button" onClick={this.handleSubmit}>Update Person</button>
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

export default UpdatePerson;
