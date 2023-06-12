start transaction;

INSERT INTO access_control 
(user_id, allowed, alteration_date, creation_date)
VALUES
    ('10419923', true, NOW(), NOW()),
    ('10420876', true, NOW(), NOW()),
    ('11263153', true, NOW(), NOW());

end transaction;