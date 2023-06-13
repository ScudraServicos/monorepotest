start transaction;

INSERT INTO access_control 
(user_id, allowed, alteration_date, creation_date)
VALUES
    ('8563126', true, NOW(), NOW()),
    ('11246445', true, NOW(), NOW()),
    ('1113008', true, NOW(), NOW()),
    ('11084887', true, NOW(), NOW()),
    ('11408102', true, NOW(), NOW()),
    ('11224544', true, NOW(), NOW()),
    ('381701', true, NOW(), NOW()),
    ('11419177', true, NOW(), NOW()),
    ('11112930', true, NOW(), NOW()),
    ('10877392', true, NOW(), NOW()),
    ('11263833', true, NOW(), NOW()),
    ('11680918', true, NOW(), NOW()),
    ('11328182', true, NOW(), NOW()),
    ('11212079', true, NOW(), NOW());

end transaction;