# Лабораторные работы №1 и №2. Учет договоров

Клиент ходит только в шлюз. Шлюз отправляет запросы в сервис договоров или в сервис отчетов. Оба сервиса читают и пишут одну базу PostgreSQL, в ней одна прикладная таблица `contracts`. Схему создает Liquibase внутри `contract-service`. `report-service` миграции не запускает.

```
client
  → gateway:8080
      → contract-service:8081  → postgres
      → report-service:8082    → postgres
```

## Порты

| Компонент | Порт в сети | Наружу в docker-compose.yml |
| --- | --- | --- |
| gateway | 8080 | 8080 |
| contract-service | 8081 | нет |
| report-service | 8082 | нет |
| postgres | 5432 | 5432 |

`docker-compose.override.yml` открывает 8081 и 8082 для отладки. Compose подхватывает этот файл сам, поэтому `docker compose up` публикует и их. Запуск, при котором снаружи видны только шлюз и PostgreSQL:

```bash
docker compose -f docker-compose.yml up --build
```

## Маршруты шлюза

| Путь | Куда |
| --- | --- |
| `/api/contracts/**` | contract-service |
| `/api/reports/**` | report-service |
| `GET /api/health` | сам шлюз: статус шлюза и обоих сервисов. Если сервис недоступен — 503 и его имя |

Swagger шлюза собирает документацию всех трех частей: http://localhost:8080/swagger-ui.html

## Запуск в Docker

```bash
docker compose -f docker-compose.yml up --build
```

Остановить, не удаляя данные:

```bash
docker compose -f docker-compose.yml down
```

Отладка с прямым доступом к сервисам:

```bash
docker compose up --build
```

## Локальный запуск

Нужен JDK 17.

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
docker compose -f docker-compose.yml up -d db
```

Если `localhost:5432` уже занят другим PostgreSQL:

```bash
DB_PORT=5433 docker compose -f docker-compose.yml up -d db
```

Дальше в трех терминалах. Для занятого порта добавьте `DB_PORT=5433` к первым двум командам.

```bash
./gradlew :contract-service:bootRun --args='--spring.profiles.active=local'
./gradlew :report-service:bootRun --args='--spring.profiles.active=local'
./gradlew :gateway:bootRun --args='--spring.profiles.active=local'
```

Адреса сервисов для шлюза берутся из `.env.example`: `CONTRACT_SERVICE_URL`, `REPORT_SERVICE_URL`.

Сборка:

```bash
./gradlew clean build
```

## API

Договоры, префикс `/api/contracts`: создать, список, договор по id, обновить, удалить, сменить менеджера, закрыть, отчет `{active, closed, total}`.

Отчеты, префикс `/api/reports`:

- `GET /api/reports/contracts` — список. Параметры `status`, `managerName`, `openedFrom`, `openedTo`, `page` (0), `limit` (20). Ответ `{items, total, page, limit}`, сортировка по дате открытия по убыванию.
- `GET /api/reports/contracts/summary` — `{active, closed, total}`.

Ошибки: `timestamp`, `status`, `error`, `message`, `path`. У валидации есть `fields`.

## Postman

Импортируйте `postman/RViP-labs.postman_collection.json` и `postman/RViP-labs.postman_environment.json`. Выберите окружение `RViP labs` (`baseUrl` = http://localhost:8080).

Collection Runner запускает папки сверху вниз: проверка шлюза, создание и изменение договора, отчеты, ошибки 400/404/409, удаление. Номер договора и `contractId` коллекция проставляет сама.

Из командной строки:

```bash
npx newman run postman/RViP-labs.postman_collection.json -e postman/RViP-labs.postman_environment.json
```
