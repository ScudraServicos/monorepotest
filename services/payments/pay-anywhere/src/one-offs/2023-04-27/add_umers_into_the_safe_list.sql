start transaction;

INSERT INTO access_control 
(user_id, allowed, alteration_date, creation_date)
VALUES
    ('11109468', true, NOW(), NOW()),
    ('12608021', true, NOW(), NOW()),
    ('11023521', true, NOW(), NOW()),
    ('11318823', true, NOW(), NOW());
end transaction;