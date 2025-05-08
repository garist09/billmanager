# Use official OpenJDK image
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/billmanager-0.0.2-SNAPSHOT.jar app.jar

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
