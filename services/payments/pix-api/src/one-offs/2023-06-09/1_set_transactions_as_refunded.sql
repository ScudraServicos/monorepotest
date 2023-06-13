UPDATE "transaction"
SET
    status = 'REFUNDED',
    update_timestamp = NOW()
WHERE partner_external_id IN (
    '6110609246519296', '4814904653512704'
)