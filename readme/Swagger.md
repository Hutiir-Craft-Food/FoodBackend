# Документація для використання Swagger

## Локальне розгортання

### 1. Запуск додатку

Перед тим як використовувати Swagger, переконайтесь, що ваш Spring Boot додаток запущений локально. Для цього:

1.	Запустіть додаток:

```Bash
./mvnw spring-boot:run
або
mvn spring-boot:run
```

### 2.	Перевірте, що додаток працює: Переконайтесь, що додаток доступний за URL http://localhost:8080.

## 2. Доступ до Swagger UI

Після запуску додатку, ви можете відкрити Swagger UI у браузері за наступною URL-адресою:

```Bash
http://localhost:8080/swagger-ui/index.html
```

Цей URL відобразить інтерфейс Swagger UI, де ви зможете переглядати і тестувати API вашого додатку.

## Використання з Docker Compose

### 1. Запуск додатку з Docker Compose

Щоб запустити ваш Spring Boot додаток за допомогою Docker Compose:

1. Перейдіть до каталогу, де знаходиться ваш docker-compose.yml файл.	
2. Запустіть Docker Compose:

```Bash
 docker-compose --file ./docker/docker-compose.yml up -d
```
3. Перевірте, що контейнери працюють: Переконайтесь, що ваш Spring Boot додаток і база даних працюють, перевіривши логи в консолі.

### 2. Доступ до Swagger UI

```Bash
http://localhost:8081/swagger-ui/index.html
```

P.S.
Зверніть увагу, що Docker Compose пробрасує порт 8080 з контейнера на 8081 на хості, тому доступ до Swagger UI слід здійснювати через порт 8081.