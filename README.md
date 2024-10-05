# ![Header Image](https://www.sfmtechnologies.com/wp-content/uploads/2023/10/Fichier-2-1.png)

<h1 align="center"> Voltix Backend </h1>

## Project Description

Voltix is a backend system for managing energy data, notifications, and user management. It leverages Spring Boot and provides APIs for integration with front-end applications and other services.

## Table of Contents

- [Installation Instructions](#installation-instructions)
- [Usage](#usage)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Contributing](#contributing)
- [License](#license)

## Installation Instructions

### Prerequisites

| Requirement   | Description                                                                                      | Link                                                      |
|---------------|--------------------------------------------------------------------------------------------------|-----------------------------------------------------------|
| **JDK 17**    | Ensure JDK 17 or higher is installed.                                                             | [Oracle JDK 17 Downloads](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or use OpenJDK. |
| **Maven**     | Install Apache Maven for building and managing project dependencies.                             | [Maven Downloads](https://maven.apache.org/download.cgi)   |

### Steps

| Step                                                | Command/Action                                                                                     
|-----------------------------------------------------|----------------------------------------------------------------------------------------------------
| **1. Clone the Repository**                         | ```bash git clone https://github.com/ramilaribi/Backend-Voltix.git```               
| **2. Navigate into the Project Directory**          | ```bash cd Backend-Voltix ```                                                                                                                    
| **3. Install Prerequisites**                        | - Ensure JDK 17+ and Maven are installed.                                                                                                          
| **4. Build the Project**                            | ```bash mvn clean install ```                                                                                                                         |
| **5. Configure Application Properties**             | - Update `src/main/resources/application.properties` with the following properties:                                                             
|                                                     | **MySQL Database Configuration:** <br>```spring.datasource.url=jdbc:mysql://localhost:3306/database_name?createDatabaseIfNotExist=true spring.datasource.username=database_username spring.datasource.password=database_password ```<br>- Replace placeholders with your MySQL details. |                                                            |
|                                                     | **MongoDB Configuration:** <br>```spring.data.mongodb.uri=mongodb://localhost:27017/mongo_db_name  ```<br>- Replace `mongo_db_name` with your MongoDB database name. |                                                            |
|                                                     | **Server Configuration:** <br>```server.port=8080 server.servlet.context-path=/app_context_path ```<br>- Replace `app_context_path` with the desired context path. |                                                            |
|                                                     | **JWT Secret Key:** <br>```jwt.secret=jwt_secret_key ```<br>- Replace `jwt_secret_key` with your actual JWT secret key. 
| **6. Run the Application**                         | ```bash mvn spring-boot:run ```                                                               |                                                            |
| **7. Access the API Documentation**                 | Available at: <br> **Swagger UI:** `http://localhost:8080/app_context_path/swagger-ui.html` <br>- Provides an interactive interface for exploring and testing API endpoints. |                                                            |

## Usage

- **API Endpoints:** Refer to the [API Documentation](#api-documentation) for details on available endpoints and how to use them. 📄
- **Authentication:** Use JWT for authentication. Include the token in the `Authorization` header of your requests. 🔑

## Configuration

- Configuration is managed through the `application.properties` file located in `src/main/resources`. 🛠️
- Adjust the properties based on your environment and needs. ⚙️

## API Documentation

- **Swagger UI:** Access the Swagger UI for interactive API documentation at `http://localhost:8080/app_context_path/swagger-ui.html`. 📊

## Contributing

We welcome contributions from the community. To contribute:

1. Fork the repository. 🍴
2. Create a feature branch. 🌿
3. Make your changes and test thoroughly. 🧪
4. Submit a pull request with a clear description of the changes. 🔄

For detailed guidelines, refer to our [Contributing Guide](CONTRIBUTING.md).

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details. 📝
