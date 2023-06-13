UPDATE "transaction"
SET
    status = 'REFUNDED',
    update_timestamp = NOW()
WHERE partner_external_id IN (
    '5857193089826816', '4736183892443136', '5866870120906752', '6383552337805312',
    '4856084699283456', '6165113690128384', '6623867904720896'
)