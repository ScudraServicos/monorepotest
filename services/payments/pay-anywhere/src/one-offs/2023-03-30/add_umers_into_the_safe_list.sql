start transaction;

INSERT INTO access_control 
(user_id, allowed, alteration_date, creation_date)
VALUES
    ('11272594', true, NOW(), NOW()),
    ('8566479', true, NOW(), NOW()),
    ('11404791', true, NOW(), NOW()),
    ('11016138', true, NOW(), NOW());

end transaction;