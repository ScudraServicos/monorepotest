start transaction;

INSERT INTO access_control 
(user_id, allowed, alteration_date, creation_date)
VALUES
    ('8566150', true, NOW(), NOW());

end transaction;