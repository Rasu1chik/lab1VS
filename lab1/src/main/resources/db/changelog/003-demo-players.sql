-- Учебный набор игроков: позиции и клубы образуют допустимый состав из 15 человек.
INSERT INTO players (club_id, first_name, last_name, position, status, price)
SELECT c.id, v.first_name, v.last_name, v.position, 'AVAILABLE', 5.0
FROM (VALUES
    ('ARS', 'Alex', 'Morgan', 'GK'),
    ('ARS', 'Ben', 'Turner', 'GK'),
    ('ARS', 'Chris', 'Walker', 'DEF'),
    ('LIV', 'Daniel', 'Gray', 'DEF'),
    ('LIV', 'Ethan', 'Brown', 'DEF'),
    ('LIV', 'Finn', 'Cooper', 'DEF'),
    ('CHE', 'George', 'Evans', 'DEF'),
    ('CHE', 'Harry', 'Collins', 'MID'),
    ('CHE', 'Ivan', 'Hughes', 'MID'),
    ('MCI', 'Jack', 'Ward', 'MID'),
    ('MCI', 'Kai', 'Price', 'MID'),
    ('MCI', 'Leo', 'Foster', 'MID'),
    ('NEW', 'Max', 'Reed', 'FWD'),
    ('NEW', 'Noah', 'Bell', 'FWD'),
    ('NEW', 'Oscar', 'Brooks', 'FWD')
) AS v(short_name, first_name, last_name, position)
JOIN real_clubs c ON c.short_name = v.short_name;
