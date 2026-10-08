# Fantasy Football — лабораторная работа №1

Монолит на Java 21 и Spring Boot 4.1.0. Приложение позволяет создавать менеджеров,
клубы и игроков, собирать фэнтези-команды, проводить трансферы, вносить статистику
туров и соревноваться в лигах. Исходная ER-диаграмма находится в
[`diagrams/er-diagram.png`](diagrams/er-diagram.png). Её следует показать
преподавателю для согласования перед защитой.

## Запуск

Нужен Docker с работающим daemon. Из корня проекта:

```bash
docker compose up --build
```

Swagger UI: <http://localhost:8080/swagger-ui.html>, OpenAPI JSON:
<http://localhost:8080/v3/api-docs>. Базовый путь API: `/api/v1`.
PostgreSQL и схема поднимаются автоматически; Liquibase применяет миграции при
старте. В начальных данных есть 20 клубов и 667 игроков из снимка Fantasy Premier
League сезона 2026/27 от 8 октября 2026 года, а также учебный тур. Цены — в
миллионах фунтов из FPL на дату снимка; они не обновляются автоматически.
Своих игроков можно добавить через Swagger.

Имена и позиции стартовых игроков сверены по официальным страницам
[Arsenal](https://www.arsenal.com/men/players/k) и
[сообщению клуба о Кепе Аррисабалаге](https://www.arsenal.com/news/transfer-windows-confirmed-for-202627-season-aKDa80B8CnMD),
[Liverpool](https://www.liverpoolfc.com/news/fantasy-premier-league-liverpool-player-prices-2026-27-revealed),
[Chelsea](https://www.chelseafc.com/en/teams/men),
[Manchester City](https://www.mancity.com/news/mens/man-city-fpl-prices-2026-27-revealed-haaland-rodri-foden-63920330) и
[Newcastle](https://www.newcastleunited.com/en/teams/mens-team). Полный каталог
получен из [официального набора данных FPL](https://fantasy.premierleague.com/api/bootstrap-static/).
В БД хранится `fpl_id`, чтобы сопоставлять игроков без изменения внутренних ID.
Если игрок уже входит в созданную фэнтези-команду, миграция сохраняет его
прежний клуб и позицию: это не ломает существующий состав при смене данных FPL.
Его имя, статус и текущая цена обновляются; цена покупки в команде остаётся прежней.

Переменные среды в `compose.yaml`: `DB_NAME`, `DB_USER`, `DB_PASSWORD`,
`APP_PORT`, `GAME_INITIAL_BUDGET`, `GAME_MAX_PLAYERS_PER_CLUB`. При запуске вне
Compose приложение также принимает `DB_HOST`, `DB_PORT` и `SERVER_PORT`.

## Проверка

```bash
mvn verify
```

Интеграционные тесты используют Testcontainers, поэтому Docker должен быть
запущен. JaCoCo проверяет покрытие строк не ниже 70%; отчёт появится в
`target/site/jacoco/index.html`.

## Основные правила и API

Состав команды: 15 игроков (2 GK, 5 DEF, 5 MID, 3 FWD), максимум три из клуба,
11 игроков в старте и один капитан из стартовых. Начальный бюджет — 100 млн.
Трансфер разрешён до дедлайна ближайшего незавершённого тура, на доступного
игрока той же позиции. Закрытие активного тура начисляет очки игрокам и
командам, капитану — вдвое больше.

CRUD доступен по `/managers`, `/clubs`, `/players`, `/fantasy-teams`,
`/gameweeks`, `/leagues`. Для списка игроков и истории трансферов используется
`Slice`: `hasNext` в теле, общего числа нет. Остальные списки возвращают
`X-Total-Count`. Каждый список принимает `page` и `size`; `size > 50` даёт 400.
В `/players` доступны фильтры `name`, `position`, `clubId`, `maxPrice`, например
`/api/v1/players?name=Haaland&size=10`.

Состав команды создаётся вместе с ней в одной транзакции. Трансфер изменяет
состав, бюджет и историю тоже в одной транзакции: при ошибке всё откатывается.
Закрытие тура одновременно записывает очки выступлений, игроков, команд и
статус тура; блокировка строки тура предотвращает двойное начисление.

Ответы с ошибками содержат время, HTTP-статус, понятное сообщение, путь и
список ошибок полей. Сущности JPA не возвращаются из контроллеров: API
использует отдельные DTO.

## Git

`main` — исходный каркас, `develop` — интеграционная ветка,
`feature/fantasy-api` — реализация API. Коммиты именуются по Conventional
Commits. После проверки feature-ветку можно слить в `develop`, а затем в `main`.
