# Use OpenJDK 17 as the base image
FROM openjdk:17

# Expose port 8082 for the application to allow external access
EXPOSE 8082

# Set the Nexus URL environment variable
ENV NEXUS_URL="http://192.168.33.10:8081"

# Set the path to the JAR file in Nexus. Ensure the JAR_FILE_PATH corresponds to your actual path in the Nexus repository
ENV JAR_FILE_PATH="/repository/maven-releases/tn/esprit/voltix/0.0.1/voltix-0.0.1.jar"

# Download the JAR file from Nexus using wget
RUN curl -o devops_project.jar "${NEXUS_URL}${JAR_FILE_PATH}"

# Command to run the Spring Boot application using the downloaded JAR
ENTRYPOINT ["java", "-jar", "devops_project.jar"]
