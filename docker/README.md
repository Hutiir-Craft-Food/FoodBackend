# Food Backend

This project is a Spring Boot application with PostgreSQL as the database. Docker is used to containerize both the application and the database, making it easier to run and deploy.

## Prerequisites

- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

## Project Structure

- **Dockerfile**: Defines how to build the Docker image for the Spring Boot application.
- **docker-compose.yml**: Defines and runs multi-container Docker applications. This file sets up the Spring Boot application and PostgreSQL database.
- **.env**: Contains environment variables used by the application and PostgreSQL. Make sure this file is correctly configured.

## How to Use

### 1. Start the Application with Docker Compose

To start the application and the PostgreSQL database using Docker Compose, run:

```bash
docker-compose.yml up -d
```

This command will:

	•	Build the image (if not already built).
	•	Start the PostgreSQL database container.
	•	Start the Spring Boot application container.

### 2. Access the Application

Once the containers are up and running, you can access the application at:
http://localhost:8080/...

### 3. Stopping the Application

To stop the running containers, use the following command:

```bash
docker-compose.yml down
```

This command will stop and remove the containers, but the data stored in the PostgreSQL volume will be preserved.