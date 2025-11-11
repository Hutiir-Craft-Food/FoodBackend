FROM maven:3.8.5-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17.0.17_10-jre-jammy

WORKDIR /app

COPY --from=build /app/target/khutirCraftuBackend-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]

