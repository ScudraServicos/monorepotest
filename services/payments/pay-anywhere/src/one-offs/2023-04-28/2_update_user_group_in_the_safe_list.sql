start transaction;
UPDATE access_control
SET groups = 'PREVIA_CAMPANHA_MAES_G1'
WHERE user_id = '11109468';
end transaction;