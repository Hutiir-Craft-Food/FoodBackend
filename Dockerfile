# Етап збірки (builder)
FROM maven:3.8.6-eclipse-temurin-17 AS builder

# Встановлюємо робочу директорію для збірки
WORKDIR /app

# Копіюємо файл pom.xml і всі необхідні залежності
COPY pom.xml .

# Виконуємо команду, щоб завантажити всі залежності без збірки проекту
RUN mvn dependency:go-offline -B

# Копіюємо весь код проекту
COPY src ./src

# Виконуємо команду збірки проекту, виключаючи тести
RUN mvn clean package -DskipTests

# Етап виконання (runtime)
FROM eclipse-temurin:17-jre-jammy

# Встановлюємо робочу директорію для виконання
WORKDIR /app

# Копіюємо зібраний jar файл з етапу збірки
COPY --from=builder /app/target/*.jar app.jar

# Відкриваємо порт 8080 для доступу до додатку
EXPOSE 8080

# Встановлюємо команду для запуску нашої програми
ENTRYPOINT ["java", "-jar", "/app.jar"]