# Событийно-ориентированный движок процессов Camunda

Пример реализации и запуска движка процессов Camunda, который взаимодействует исключительно через события Kafka.
Синхронные REST-вызовы обрабатываются с помощью ReplyingKafkaTemplate.

## Настройка инфраструктуры

```shell
docker compose up -d
```

## Сборка и запуск сервисов

```shell
mvn clean install
```

### REST-API

```shell
mvn -pl rest-api spring-boot:run
```

### quote worker

```shell
mvn -pl quote-worker spring-boot:run
```

### process engine

```shell
mvn -pl process-engine spring-boot:run
```

## start process and request results

Use [rest-api.http](./rest-api.http) to start the business process.

## Camunda-Admin
http://localhost:9081/ (admin/admin123)