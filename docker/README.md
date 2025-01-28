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
docker compose --file ./docker/docker-compose.yml up -d
```
This command will do the below:

	•	Build the image (if not already built).
	•	Start the PostgreSQL database container.
	•	Start the Spring Boot application container.


### 2. Access the Application

Once the containers are up and running, you can access the application at:
http://localhost:8080/
**Note:** The port `8080` is the default port specified in the `docker-compose.yml` file. If you wish to change this port,
you can do so by modifying the `ports` section in the `docker-compose.yml` file. For example:

```yaml
ports:
  - "8080:8080"
```
To change the exposed port, simply update the first number. For instance, to use port 9090, update it to:

```yaml
ports:
  - "9090:8080"
```
After making the change, you will access the application at:
  http://localhost:9090/

```bash
docker compose ps
```
This command will show the status of your Docker containers.

**Note:** For additional configuration and instructions related to the Dockerfile
used to build your application’s image, refer to the docker/Dockerfile located
in the docker directory. You may need to customize this file if you want to adjust 
the base image, install additional dependencies, or configure the build process further.

For building Docker image from Dockerfile, use the command:
```Bash
docker build -t my-app-image
```
This command generates an image with the name my-app-image

For running container:
```Bash
docker run -d -p 8080:8080 my-app-image
```
This command will run the container in the background, using port 8080.

To view all created images, use:
```Bash
docker image
```

To delete an image, use:
```Bash
docker rmi my-app-image
```

### 3. Stopping the Application

To stop the running containers, use the following command:

```bash
docker compose --file ./docker/docker-compose.yml down
```
This command will stop and remove the containers, but the data stored in the PostgreSQL volume will be preserved.

### 4. Команди для роботи з БД з під docker:

1. Перевірка чи працює контейнер: 

```docker ps```

2. IP-адреса з БД: docker inspect -f 

```{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' khutir-db```

3. Доступ до БД khutir-db запущеної в контейнері docker:

```docker exec -it khutir-db psql -U postgres```

4. Перегляд БД:

```\l```

5. Вибір БД:

```\c khutir_craft```

6. Перевірка таблиць:

```\dt```

7. Структура:

```\d імʼя```

8. Всередині працюють команди притаманні sql: 

```SELECT * FROM table_name```

9. Лічильник записів даних: 

```SELECT COUNT(*) FROM table_name```

10. Довідка по командах: 

```\?```

11. Довідка по SQL-командах: 

```\h```

12. Вихід із клієнта: 

```\q```
