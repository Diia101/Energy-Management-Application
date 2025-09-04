# Energy-Management-Application
This application is a web-based platform for energy management, built on a microservices-based architecture. The platform allows access with authentication for two types of users: administrator and client. The administrator can manage user accounts and smart devices, while each user can view their own devices and energy consumption.

Architecture and Key Features:

The system is divided into several microservices:
Frontend: Implemented in React to provide a dynamic interface.
User Management Microservice: Manages users through CRUD (Create, Read, Update, Delete) operations.
Device Management Microservice: Administers smart metering devices with CRUD operations.
Measurement Management Microservice: Collects, stores, and analyzes energy consumption data from smart devices.
Chat Management Microservice: Enables asynchronous communication between users and the administrator.
Security is based on session/cookie-based authentication for controlled access and uses JSON Web Token (JWT) for API security.


:Dependencies and Execution

To run the application, the following are required:
Dependencies: Java 17, Node.js 16, and PostgreSQL for the database.
Running with Docker: The simplest method is to use Docker.
Bash
docker-compose up --build
Manual Execution: Alternatively, you can run each microservice individually in IntelliJ and the frontend with npm start in the terminal.
