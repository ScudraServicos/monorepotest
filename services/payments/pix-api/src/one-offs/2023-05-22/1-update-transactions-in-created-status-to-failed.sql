update "transaction"
set
    status = 'FAILED',
    update_timestamp = NOW()
WHERE partner_external_id IN (
    '6187348198948864',
    '5000619576262656',
    '5875475758972928',
    '6692261366595584',
    '6315370226909184'
)