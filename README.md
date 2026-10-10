# Лабораторные работы №1–3. Учет договоров

Клиент ходит только в шлюз. Сервис отчетов не открыт наружу и к базе не подключается: список и сводку он запрашивает у сервиса договоров через RestTemplate. База есть только у `contract-service`.

```
client
  → gateway:8080
      → report-service:8082
          → contract-service:8081
              → postgres
      → contract-service:8081
          → postgres
```

## Порты

| Компонент | Порт в сети | Наружу |
| --- | --- | --- |
| gateway | 8080 | 8080 |
| contract-service | 8081 | нет |
| report-service | 8082 | нет |
| postgres | 5432 | 5432 |

`docker-compose.override.yml` открывает только `contract-service` на 8081. Порт `report-service` наружу не пробрасывается. Compose подхватывает override сам. Запуск, при котором снаружи видны только шлюз и PostgreSQL:

```bash
docker compose -f docker-compose.yml up --build
```

## Роли сервисов

| Сервис | Роль |
| --- | --- |
| contract-service | Единственный доступ к PostgreSQL. CRUD договоров и внутренний поиск для отчета |
| report-service | Собирает отчет: запрашивает договоры и считает `durationDays`. Снаружи недоступен |
| gateway | Публичный вход. Закрывает внутренний путь `/api/contracts/search` |

## Маршруты шлюза

| Путь | Куда |
| --- | --- |
| `/api/contracts/**` | contract-service |
| `GET /api/contracts/search` | 404, путь внутренний |
| `/api/reports/**` | report-service |
| `GET /api/health` | сам шлюз: статус шлюза и обоих сервисов. Если сервис недоступен — 503 и его имя |

Swagger опубликован только у шлюза: http://localhost:8080/swagger-ui.html

## Запуск в Docker

```bash
docker compose -f docker-compose.yml up --build
```

Остановить, не удаляя данные:

```bash
docker compose -f docker-compose.yml down
```

Отладка с прямым доступом к сервису договоров:

```bash
docker compose up --build
```

`report-service` с хоста по-прежнему недоступен. Чтобы запустить его локально рядом с сервисом договоров:

```bash
CONTRACT_SERVICE_URL=http://localhost:8081 ./gradlew :report-service:bootRun --args='--spring.profiles.active=local'
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

Дальше в трех терминалах. Для занятого порта добавьте `DB_PORT=5433` к команде `contract-service`.

```bash
./gradlew :contract-service:bootRun --args='--spring.profiles.active=local'
CONTRACT_SERVICE_URL=http://localhost:8081 ./gradlew :report-service:bootRun --args='--spring.profiles.active=local'
./gradlew :gateway:bootRun --args='--spring.profiles.active=local'
```

Сборка:

```bash
./gradlew clean build
```

## API

Договоры, префикс `/api/contracts`: создать, список, договор по id, обновить, удалить, сменить менеджера, закрыть, отчет `{active, closed, total}`.

Внутренний `GET /api/contracts/search` есть только у `contract-service`. Через шлюз он отвечает 404. Параметры: `status`, `managerName`, `openedFrom`, `openedTo`, `page`, `limit`. Ответ `{items, total, page, limit}`, сортировка по дате открытия по убыванию.

Отчеты, префикс `/api/reports`:

- `GET /api/reports/contracts` — список. Параметры `status`, `managerName`, `openedFrom`, `openedTo`, `page` (0), `limit` (20). Ответ `{items, total, page, limit}`. В каждой строке есть `durationDays`: число дней от открытия до закрытия, для действующего договора — до сегодня.
- `GET /api/reports/contracts/summary` — `{active, closed, total}`.

Ошибки: `timestamp`, `status`, `error`, `message`, `path`. У валидации есть `fields`.

## Проверка отказоустойчивости

Остановите сервис договоров и запросите отчет через шлюз. Ответ — 503 в том же формате ошибки:

```bash
docker stop rvip-contract-service
curl -s -D - http://localhost:8080/api/reports/contracts
docker start rvip-contract-service
```

В теле будут `status` 503 и `message` «Сервис договоров недоступен».

## Postman

Импортируйте `postman/RViP-labs.postman_collection.json` и `postman/RViP-labs.postman_environment.json`. Выберите окружение `RViP labs` (`baseUrl` = http://localhost:8080).

Collection Runner запускает папки сверху вниз: проверка шлюза, создание и изменение договора, отчеты, ошибки 400/404/409, удаление, затем папка «Lab 3». Номер договора и `contractId` коллекция проставляет сама.

Папка «Lab 3» идет через шлюз: создает договор и видит его в отчете с `durationDays`, закрывает и находит фильтром `status=CLOSED`, сверяет сводку с `/api/contracts/report`, проверяет 404 на `/api/contracts/search`.

Из командной строки:

```bash
npx newman run postman/RViP-labs.postman_collection.json -e postman/RViP-labs.postman_environment.json
```
