import React from 'react';
import { useLocation, Navigate } from 'react-router-dom';

export function PrivateRoute({ element }) {
  const location = useLocation();


  const token = localStorage.getItem('token');


  if (token) {
  
    return element;
  } else {

    return <Navigate to="/" state={{ from: location }} />;
  }
}

export default PrivateRoute;

