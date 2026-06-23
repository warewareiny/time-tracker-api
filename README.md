# Time Tracker API

REST API для управления задачами и учёта рабочего времени, разработанный на Spring Boot.

Проект позволяет регистрировать пользователей, создавать задачи, запускать таймеры для отслеживания затраченного времени и получать статистику по выполненной работе.

## Возможности

### Аутентификация и пользователи

* Регистрация нового пользователя
* Авторизация по JWT
* Получение информации о текущем пользователе
* Обновление профиля
* Удаление собственного аккаунта
* Просмотр пользователей (ADMIN)
* Получение пользователя по ID (ADMIN)
* Удаление пользователей (ADMIN)

### Управление задачами

* Создание задачи
* Получение списка задач
* Фильтрация задач по статусу
* Пагинация списка задач
* Получение задачи по ID
* Обновление задачи
* Удаление задачи

### Учёт рабочего времени

* Запуск таймера для задачи
* Остановка активного таймера
* Получение информации об активном таймере
* Получение продолжительности активного таймера
* Просмотр всех записей времени пользователя
* Получение статистики по затраченному времени

### Безопасность

* JWT Authentication
* Spring Security
* Role-Based Access Control (USER / ADMIN)

## Технологии

* Java 17
* Spring Boot 3
* Spring Security
* Spring Data JPA
* Hibernate
* PostgreSQL
* Liquibase
* JWT 
* MapStruct
* Lombok
* Swagger / OpenAPI
* Docker
* Docker Compose

### Тестирование

* JUnit 5
* Mockito
* Spring Boot Test
* Testcontainers

## Архитектура проекта

```
src/main/java/com/example/timetracker

├── auth
│   ├── config
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── exception
│   ├── filter
│   ├── mapper
│   ├── repository
│   └── service
│
├── task
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── exception
│   ├── mapper
│   ├── repository
│   └── service
│
├── timeentry
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── exception
│   ├── mapper
│   ├── repository
│   └── service
│
└── shared
    ├── config
    └── exception
```

## Модель данных

### User

| Поле         | Тип          |
| ------------ | ------------ |
| id           | Integer      |
| username     | String       |
| email        | String       |
| passwordHash | String       |
| role         | USER / ADMIN |
| createdAt    | Instant      |
| updatedAt    | Instant      |

### Task

| Поле        | Тип         |
| ----------- | ----------- |
| id          | Integer     |
| title       | String      |
| description | String      |
| status      | TODO / DONE |
| user        | User        |
| createdAt   | Instant     |
| updatedAt   | Instant     |

### TimeEntry

| Поле            | Тип     |
| --------------- | ------- |
| id              | Integer |
| startTime       | Instant |
| endTime         | Instant |
| durationMinutes | Long    |
| task            | Task    |
| user            | User    |
| createdAt       | Instant |
| updatedAt       | Instant |

## Статусы задач

| Статус | Описание                  |
| ------ | ------------------------- |
| TODO   | Задача ожидает выполнения |
| DONE   | Задача завершена          |

После остановки таймера задача автоматически переводится в статус `DONE`.

## API Endpoints

### Authentication

| Метод | Endpoint               |
| ----- | ---------------------- |
| POST  | `/api/v1/auth/sign-up` |
| POST  | `/api/v1/auth/sign-in` |

### Users

| Метод  | Endpoint             |
| ------ | -------------------- |
| GET    | `/api/v1/users/me`   |
| PUT    | `/api/v1/users/me`   |
| DELETE | `/api/v1/users/me`   |
| GET    | `/api/v1/users`      |
| GET    | `/api/v1/users/{id}` |
| DELETE | `/api/v1/users/{id}` |

### Tasks

| Метод  | Endpoint             |
| ------ | -------------------- |
| GET    | `/api/v1/tasks`      |
| POST   | `/api/v1/tasks`      |
| GET    | `/api/v1/tasks/{id}` |
| PUT    | `/api/v1/tasks/{id}` |
| DELETE | `/api/v1/tasks/{id}` |

Дополнительно поддерживаются:

```
GET /api/v1/tasks?status=TODO
GET /api/v1/tasks?page=0&size=10
```

### Time Entries

| Метод | Endpoint                              |
| ----- | ------------------------------------- |
| GET   | `/api/v1/time-entries`                |
| GET   | `/api/v1/time-entries/statistics`     |
| POST  | `/api/v1/time-entries/start/{taskId}` |
| POST  | `/api/v1/time-entries/stop`           |
| GET   | `/api/v1/time-entries/active`         |
| GET   | `/api/v1/time-entries/active/minutes` |

## Запуск через Docker


```bash
docker compose up --build
```

После запуска приложение будет доступно по адресу:

```text
http://localhost:8080
```

## Swagger

Документация API:

```text
http://localhost:8080/swagger-ui/index.html
```

## Тестирование

Запуск всех тестов:

```bash
./gradlew test
```

Проект содержит:

* Unit-тесты
* Интеграционные тесты
* Testcontainers для PostgreSQL

## Что было реализовано в проекте

* Spring Security + JWT
* REST API
* PostgreSQL
* Liquibase
* Docker
* DTO и MapStruct
* Глобальная обработка исключений
* Пагинация и фильтрация
* Unit и Integration Testing
* Ролевая модель доступа
* Документирование API через Swagger/OpenAPI
