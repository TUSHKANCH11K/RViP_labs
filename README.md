# Лабораторная работа №1. Сервис учета договоров

Основной сервис распределенной системы учета контрагентов и договоров. Контрагент и ответственный менеджер хранятся в той же записи, что и договор: в PostgreSQL одна прикладная таблица `contracts`.

Можно завести контрагента и открыть с ним договор, сменить ответственного менеджера, закрыть договор и получить отчет: сколько договоров действует и сколько закрыто.

## Стек

Java 17, Spring Boot 3.5, Gradle 8 (Groovy DSL), Spring Web, Spring Data JPA, Validation, Actuator, PostgreSQL 16, Liquibase, springdoc-openapi, Lombok, Docker Compose.

Схема базы создается только Liquibase. `spring.jpa.hibernate.ddl-auto=validate`.

## Запуск

Нужны JDK 17 и Docker.

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

Параметры подключения читаются из переменных `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`. Значения по умолчанию — в `.env.example` и в профилях приложения.

### Только база в Docker, приложение на хосте

```bash
docker compose up -d db
./gradlew :contract-service:bootRun --args='--spring.profiles.active=local'
```

Приложение подключается к `localhost:5432`.

Если `localhost:5432` уже занят другим PostgreSQL, контейнер не перехватит этот адрес. Тогда поднимите базу на свободном порту и передайте его приложению:

```bash
DB_PORT=5433 docker compose up -d db
DB_PORT=5433 ./gradlew :contract-service:bootRun --args='--spring.profiles.active=local'
```

### Вся система в контейнерах

```bash
docker compose up --build
```

Сервис `contract-service` стартует с профилем `docker` и ходит в PostgreSQL по имени `db` в сети `rvip-net`.

Остановить:

```bash
docker compose down
```

## Swagger UI

http://localhost:8081/swagger-ui.html

Проверка живости: http://localhost:8081/actuator/health

## Эндпоинты

| Метод | Путь | Назначение |
| --- | --- | --- |
| POST | `/api/contracts` | Завести контрагента и открыть договор |
| GET | `/api/contracts` | Список, фильтр `?status=ACTIVE` или `?status=CLOSED` |
| GET | `/api/contracts/{id}` | Договор по id |
| PUT | `/api/contracts/{id}` | Обновить данные договора и контрагента |
| DELETE | `/api/contracts/{id}` | Удалить запись |
| PATCH | `/api/contracts/{id}/close` | Закрыть договор. Уже закрыт — 409 |
| PATCH | `/api/contracts/{id}/manager` | Назначить менеджера. Договор закрыт — 409 |
| GET | `/api/contracts/report` | `{"active": N, "closed": M, "total": N+M}` |

Ошибки приходят в одном формате: `timestamp`, `status`, `error`, `message`, `path`. У ошибки валидации дополнительно есть `fields` — сообщения по полям.

В базе уже есть 5 тестовых договоров: три `ACTIVE` и два `CLOSED`. Следующий созданный договор получит id `6`, если строки не удаляли.

## Сценарий демонстрации

Создать контрагента и открыть договор:

```bash
curl -s -X POST http://localhost:8081/api/contracts \
  -H 'Content-Type: application/json' \
  -d '{
    "contractNumber": "ДГ-2026-010",
    "counterpartyName": "ООО «Поле»",
    "counterpartyInn": "7701234567",
    "counterpartyContact": "+7-495-111-22-33",
    "managerName": "Иванова Анна"
  }'
```

Назначить нового менеджера:

```bash
curl -s -X PATCH http://localhost:8081/api/contracts/6/manager \
  -H 'Content-Type: application/json' \
  -d '{"managerName": "Смирнов Олег"}'
```

Закрыть договор:

```bash
curl -s -X PATCH http://localhost:8081/api/contracts/6/close
```

Повторное закрытие вернет 409:

```bash
curl -s -i -X PATCH http://localhost:8081/api/contracts/6/close
```

Отчет:

```bash
curl -s http://localhost:8081/api/contracts/report
```

После этого сценария в отчете будет 3 действующих и 3 закрытых договора (`active` 3, `closed` 3, `total` 6): тестовые данные плюс только что закрытый договор.

Сборка без запуска:

```bash
./gradlew clean build
```
