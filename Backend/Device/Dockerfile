# Use OpenJDK 23 as the base image
FROM openjdk:23-jdk

# Set the working directory
WORKDIR /app

# Copy the JAR file from the target directory
COPY /target/ds-2020-0.0.1-SNAPSHOT.jar ./ds-2020-0.0.1-SNAPSHOT.jar

# Set environment variables for the database connection
ENV DB_HOST=pu
ENV DB_PORT=5432
ENV DB_USER=postgres
ENV DB_PASSWORD=diana123
ENV DB_NAME=city-db-user

# Expose the port your application will run on
EXPOSE 8085

# Specify the command to run your application
ENTRYPOINT ["java", "-jar", "ds-2020-0.0.1-SNAPSHOT.jar"]
