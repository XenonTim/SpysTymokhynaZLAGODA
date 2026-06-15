# ZLAGODA backend

## Що треба для запуску
- JDK 17
- MySQL 8+
- Maven 3.9+

## Налаштування бази
1. Створіть базу `ais_shop`.
2. Виконайте `src/main/resources/db/schema.sql`.
3. Виконайте `src/main/resources/db/data.sql`.
4. Перевірте `src/main/resources/db.properties`:
   - `db.url`
   - `db.user`
   - `db.password`

## Запуск
```bash
mvn clean tomcat7:run
```

Після запуску відкрийте:
```text
http://localhost:8080/
```

## Тестові дані для входу
У seed-даних всі працівники мають пароль:

```text
password123
```

Наприклад, можна зайти як працівник `101`.
