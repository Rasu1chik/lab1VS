# Результаты проверки

Проверено 22 октября 2026 года на локальном Docker/Colima, Java 21 в контейнере.
Рабочий каталог подготовки: `/private/tmp/fantasy-lab2`.
Код после сборки не менялся. После уточнения сценария smoke он запущен повторно.

| Проверка | Результат |
|---|---|
| Maven reactor, все пять приложений | `BUILD SUCCESS`, код 0 |
| Unit-тесты менеджерского сервиса | 4, ошибок/пропусков нет |
| Unit-тесты игрового сервиса | 8, ошибок/пропусков нет |
| Docker Compose конфигурация | Успешно, код 0 |
| Сборка runtime-образов из полученных JAR | Успешно, код 0 |
| Запуск пяти приложений и двух PostgreSQL | Все 7 контейнеров healthy |
| Eureka | Зарегистрированы CONFIG-SERVER, API-GATEWAY, MANAGER-SERVICE, FANTASY-SERVICE |
| Config Server | Найдены отдельные propertySources для четырёх клиентов |
| OpenAPI через Gateway | Оба `/docs/*` вернули JSON со схемой и операциями |
| Закрытый внутренний маршрут через Gateway | `/internal/managers/1/references` вернул 404 |
| БД менеджеров | Содержит только таблицу `managers` |
| Игровая БД | Отдельные игровые таблицы, таблицы `managers` нет |
| Исходный каталог | 667 различных `fpl_id`, сохранены первые 15 имён и цена Haaland |
| HTTP smoke + отказ и восстановление | `ALL END-TO-END CHECKS PASSED`, код 0 |
| Реальный Feign Circuit Breaker | После отказов OPEN, после двух успешных проб CLOSED |

## Команды

Локальных Java/Maven не было, поэтому проверка Maven запускалась в контейнере.
Colima не видит `/private/tmp` как bind mount; исходники переданы через `docker cp`:

```bash
docker create --name fantasy-lab2-build \
  -v fantasy-lab2-maven:/root/.m2 -w /workspace \
  maven:3.9.9-eclipse-temurin-21 mvn -B verify
docker cp /private/tmp/fantasy-lab2/. fantasy-lab2-build:/workspace
docker start -a fantasy-lab2-build
```

После последнего изменения `ManagerErrors.java` файл был передан в этот контейнер,
и `docker start -a fantasy-lab2-build` повторён. Итог: 12 тестов, 0 failures,
0 errors, 0 skipped. Полученные пять JAR скопированы из контейнера в `*/target/`.

Запуск проверенной сборки:

```bash
docker compose config --quiet
docker compose -f compose.yaml -f compose.prebuilt.yaml \
  up --build -d --wait --wait-timeout 300
docker compose ps
python3 -u scripts/smoke.py --resilience
```

Для каждого шага получен окончательный код 0. HTTP-сценарий проверяет настоящие
сервисы и PostgreSQL, а не моки. В варианте отказа временно останавливается
`manager-service`, проверяется OPEN через `/actuator/circuitbreakers` игрового
сервиса; после перезапуска проверяются успешные Feign-вызовы и CLOSED.

Дополнительно прочитаны `/eureka/apps` и четыре конфигурации на Config Server,
оба OpenAPI через Gateway. Состав таблиц проверен `psql` внутри отдельных
контейнеров PostgreSQL; в игровой БД выполнен
`select count(distinct fpl_id) from players` — результат 667.

## Файлы результата

- `verification/maven-verify.log` — итоговая сборка с результатами каждого теста.
- `verification/smoke-resilience.log` — полный успешный HTTP/отказный сценарий.
- `verification/infrastructure.log` — реестр, конфигурация, OpenAPI, разделение БД.

## Область доказанного результата

Проверены компиляция исходников, unit-тесты и запуск готовых JAR через
`Dockerfile.runtime` и `compose.prebuilt.yaml`. Самостоятельная повторная Maven-сборка
внутри основного многостадийного `Dockerfile` отдельно не запускалась: он использует
ту же версию Maven/Java и команду package для тех же исходников. Для запуска без
предварительно собранных JAR предусмотрен обычный `docker compose up --build`.

Старый монолитный тест MockMvc не запускался как тест новой системы: его исходник
сохранён в `reference/lab1-tests`, основные сценарии выполняет HTTP smoke.
Процент покрытия JaCoCo исходного монолита на микросервисы не переносился и не заявляется.

Пользовательские данные из старой БД не мигрировались. Распределённая транзакция
между проверкой менеджера и созданием команды не реализована; граница описана
в README. Не проверялись production-развёртывание, конкурентное удаление менеджера
и создание команды, нагрузка и отказ самого Docker-хоста.

Контейнеры оставлены работающими для осмотра. Демо-записи smoke сохранены в новых
базах; состояние исходной лабораторной и её данные не изменялись.
