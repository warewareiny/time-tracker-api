# Time Tracker API
Учебный Java REST API проект для управления задачами и учёта рабочего времени.
Приложение написано на **Spring Boot** с использованием **Spring Security**, **JWT**, **JPA/Hibernate** и **PostgreSQL**.

## Описание проекта
Time Tracker API — это REST-сервис, который позволяет пользователям создавать задачи, запускать и останавливать таймеры, а также отслеживать время, затраченное на выполнение задач.

Проект создан как учебный проеткт для практики:
* Java Core
* Spring Boot
* Spring Security
* JWT Authentication
* Spring Data JPA
* Hibernate
* REST API
* PostgreSQL
* Liquibase
* Swagger / OpenAPI
* DTO, Entity
* валидации данных
* обработки исключений

## Основная функциональность
* Регистрация пользователя
* Авторизация пользователя
* JWT-аутентификация
* Получение информации о текущем пользователе
* Редактирование профиля пользователя
* Удаление аккаунта
* Создание задач
* Просмотр списка задач
* Получение задачи по идентификатору
* Редактирование задачи
* Удаление задачи
* Запуск таймера для задачи
* Остановка активного таймера
* Просмотр активного таймера
* Получение длительности текущего таймера
* Разграничение прав доступа USER / ADMIN
* Swagger-документация API

## Статусы задач
1. `TODO` — задача ожидает выполнения
2. `DONE` — задача завершена
После остановки таймера задача автоматически переводится в статус `DONE`.

## Технологии
* Java 17
* Spring Boot
* Spring Security
* JWT
* Spring Data JPA
* Hibernate
* PostgreSQL
* Liquibase
* Lombok
* Swagger OpenAPI

## Архитектура проекта

Проект разделён на несколько слоёв: src/main/java/com/example/timetracker

* config — конфигурация приложения и безопасности
* controller — REST-контроллеры
* dto — объекты передачи данных
* entity — сущности базы данных
* exception — пользовательские исключения и обработчики
* filter — JWT-фильтры
* repository — работа с базой данных
* service — бизнес-логика приложения

Миграции базы данных находятся в: src/main/resources/db/changelog

## Сущности

### User
Пользователь содержит:
* id
* username
* email
* passwordHash
* role
* createdAt
* updatedAt

### Task
Задача содержит:
* id
* title
* description
* status
* createdAt
* updatedAt
* user

### TimeEntry
Запись учёта времени содержит:
* id
* startTime
* endTime
* durationMinutes
* task
* user
* createdAt
* updatedAt

### Role
Enum с ролями пользователей:
* USER
* ADMIN

### Status
Enum со статусами задач:
* TODO
* DONE

## Основные API-маршруты

### Authentication
| URL             | Метод | Описание                 |
| --------------- | ----- | ------------------------ |
| `/auth/sign-up` | POST  | Регистрация пользователя |
| `/auth/sign-in` | POST  | Авторизация пользователя |

### Users
| URL           | Метод  | Описание                      |
| ------------- | ------ | ----------------------------- |
| `/users/me`   | GET    | Текущий пользователь          |
| `/users/me`   | PUT    | Обновление профиля            |
| `/users/me`   | DELETE | Удаление своего аккаунта      |
| `/users`      | GET    | Список пользователей (ADMIN)  |
| `/users/{id}` | GET    | Пользователь по id (ADMIN)    |
| `/users/{id}` | DELETE | Удаление пользователя (ADMIN) |

### Tasks
| URL           | Метод  | Описание          |
| ------------- | ------ | ----------------- |
| `/tasks`      | GET    | Список задач      |
| `/tasks`      | POST   | Создание задачи   |
| `/tasks/{id}` | GET    | Получение задачи  |
| `/tasks/{id}` | PUT    | Обновление задачи |
| `/tasks/{id}` | DELETE | Удаление задачи   |

### Time Entries
| URL                            | Метод | Описание                       |
| ------------------------------ | ----- | ------------------------------ |
| `/time-entries/start/{taskId}` | POST  | Запуск таймера                 |
| `/time-entries/stop`           | POST  | Остановка таймера              |
| `/time-entries/active`         | GET   | Активный таймер                |
| `/time-entries/active/minutes` | GET   | Длительность активного таймера |

## Безопасность
Для доступа к защищённым эндпоинтам используется JWT-аутентификация.
После успешной регистрации или входа пользователь получает JWT-токен, который необходимо передавать в заголовке:
Authorization: Bearer <token>

## Swagger
После запуска приложения документация доступна по адресу:

http://localhost:8080/swagger-ui/index.html
