-- Меняем только имена исходных учебных игроков. ID, клуб, позиция, цена и
-- уже созданные связи с командами остаются прежними.
UPDATE players AS p
SET first_name = names.real_first_name,
    last_name = names.real_last_name
FROM (VALUES
    ('ARS', 'Alex', 'Morgan', 'David', 'Raya'),
    ('ARS', 'Ben', 'Turner', 'Kepa', 'Arrizabalaga'),
    ('ARS', 'Chris', 'Walker', 'William', 'Saliba'),
    ('LIV', 'Daniel', 'Gray', 'Virgil', 'van Dijk'),
    ('LIV', 'Ethan', 'Brown', 'Joe', 'Gomez'),
    ('LIV', 'Finn', 'Cooper', 'Jeremie', 'Frimpong'),
    ('CHE', 'George', 'Evans', 'Levi', 'Colwill'),
    ('CHE', 'Harry', 'Collins', 'Enzo', 'Fernandez'),
    ('CHE', 'Ivan', 'Hughes', 'Moises', 'Caicedo'),
    ('MCI', 'Jack', 'Ward', 'Phil', 'Foden'),
    ('MCI', 'Kai', 'Price', 'Rayan', 'Cherki'),
    ('MCI', 'Leo', 'Foster', 'Tijjani', 'Reijnders'),
    ('NEW', 'Max', 'Reed', 'Yoane', 'Wissa'),
    ('NEW', 'Noah', 'Bell', 'William', 'Osula'),
    ('NEW', 'Oscar', 'Brooks', 'Harvey', 'Barnes')
) AS names(short_name, old_first_name, old_last_name, real_first_name, real_last_name)
JOIN real_clubs AS c ON c.short_name = names.short_name
WHERE p.club_id = c.id
  AND p.first_name = names.old_first_name
  AND p.last_name = names.old_last_name;
