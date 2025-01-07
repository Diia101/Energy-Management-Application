# Stage 1: Build
FROM maven:3.8.3-openjdk-17 AS builder

COPY ./src /root/src
COPY ./pom.xml /root/
COPY ./checkstyle.xml /root/
WORKDIR /root
RUN mvn clean package -DskipTests

# Check if the JAR was built correctly
RUN ls -l ./target

# Stage 2: Run
FROM openjdk:23-jdk

# Copy the built JAR file into the new image
COPY --from=builder /root/target/ds-2020-0.0.1-SNAPSHOT.jar ./ds-2020-0.0.1-SNAPSHOT.jar

# Expose the port your application runs on
EXPOSE 8085

# Use the JAR file directly without layertools since we are not extracting layers
ENTRYPOINT ["java", "-jar", "ds-2020-0.0.1-SNAPSHOT.jar"]
