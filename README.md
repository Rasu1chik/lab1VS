# Fantasy Football — лабораторные работы

Один проект на двух этапах: монолит и его декомпозиция на микросервисы.

| Работа | Код и инструкции | Архитектура |
|---|---|---|
| №1 | [lab1/README.md](lab1/README.md) | Монолит Spring Boot и PostgreSQL |
| №2 | [lab2/README.md](lab2/README.md) | Два бизнес-сервиса и Spring Cloud |

## Первая лабораторная

```bash
cd lab1
docker compose up --build
```

## Вторая лабораторная

```bash
cd lab2
docker compose up --build -d --wait --wait-timeout 300
python3 scripts/smoke.py
```

Обе версии используют порт 8080, поэтому запускайте их по очереди. Данные
хранятся в отдельных Docker volumes. Вторая работа использует новые базы;
автоматического переноса пользовательских данных первой работы нет.

История первой лабораторной сохранена. Изменения второй продолжают её в ветке
`feature/lab2-microservices`. Отчёт для защиты находится в
[lab2/REPORT.md](lab2/REPORT.md), результаты исходной проверки — в
[lab2/VERIFICATION.md](lab2/VERIFICATION.md).
