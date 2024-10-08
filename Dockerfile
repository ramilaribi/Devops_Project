# Use OpenJDK 17 as the base image (as specified in your project properties)
FROM openjdk:17

# Expose port 8082 for the application to allow external access
EXPOSE 8082

# Set the Nexus URL environment variable
ENV NEXUS_URL="http://192.168.33.10:8081"

# Set the path to the JAR file in Nexus. This is where the SNAPSHOT JAR is stored
# Ensure the JAR_FILE_PATH corresponds to your actual path in the Nexus repository
ENV JAR_FILE_PATH="http://192.168.33.10:8081/repository/maven-snapshots/tn/esprit/voltix/0.0.1-SNAPSHOT/voltix-0.0.1-20241008.163153-1.jar devops_project.jar"

# Download the JAR file from Nexus. The -o flag specifies the output file name
# Make sure the JAR path is correct and that it exists in Nexus
ADD "${NEXUS_URL}${JAR_FILE_PATH}" devops_project.jar

# Command to run the Spring Boot application using the downloaded JAR
ENTRYPOINT ["java", "-jar", "devops_project.jar"]
