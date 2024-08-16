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
docker-compose --file ./docker/docker-compose.yml up -d
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
docker-compose --file ./docker/docker-compose.yml down
```

This command will stop and remove the containers, but the data stored in the PostgreSQL volume will be preserved.




# Налаштування проекту

## Умови

Перед тим як розпочати, переконайтеся, що у вас встановлено наступне:

- [Docker Desktop](https://www.docker.com/products/docker-desktop)
- [Docker Compose](https://docs.docker.com/compose/install/)

## Налаштування на ОС Windows

### Встановлення Docker та Docker Compose

1. **Завантаження Docker Desktop**:
    - Перейдіть на [Docker Desktop](https://www.docker.com/products/docker-desktop) та завантажте інсталяційний файл для Windows.
    - Запустіть інсталяційний файл і слідуйте інструкціям на екрані для завершення установки.
    - Після завершення встановлення, Docker Desktop автоматично запустить Docker та Docker Compose.

2. **Перевірка установки**:
    - Відкрийте командний рядок (CMD) або PowerShell і виконайте наступні команди, щоб перевірити, чи Docker та Docker Compose були встановлені успішно:

      ```bash
      docker --version
      docker-compose --version
      ```

### Налаштування проекту

1. **Клонування репозиторію**:
    - Виконайте команду для клонування репозиторію на ваш комп'ютер:

      ```bash
      git clone <URL_вашого_репозиторію>
      ```

2. **Перехід до каталогу проекту**:
    - Перейдіть у каталог проекту, наприклад:

      ```bash
      cd <назва_каталогу>
      ```

3. **Запуск Docker контейнерів**:
    - Запустіть Docker контейнери за допомогою `docker-compose`:

      ```bash
      docker-compose --file ./docker/docker-compose.yml up -d
      ```

    - Це створить та запустить всі необхідні контейнери для вашого проекту.

4. **Перевірка роботи**:
    - Перевірте, чи всі контейнери працюють коректно:

      ```bash
      docker-compose ps(ls)
      ```

---
