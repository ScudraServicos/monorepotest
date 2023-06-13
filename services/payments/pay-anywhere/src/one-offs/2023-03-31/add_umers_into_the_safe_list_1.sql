start transaction;

INSERT INTO access_control 
(user_id, allowed, alteration_date, creation_date)
VALUES
    ('11231983', true, NOW(), NOW()),
    ('10942251', true, NOW(), NOW()),
    ('11057826', true, NOW(), NOW()),
    ('11157848', true, NOW(), NOW());

end transaction;