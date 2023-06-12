start transaction;

INSERT INTO access_control
(user_id, allowed, alteration_date, creation_date, groups)
VALUES
    ('11374049', true, NOW(), NOW(), 'INTERNAL_BETA');
end transaction;