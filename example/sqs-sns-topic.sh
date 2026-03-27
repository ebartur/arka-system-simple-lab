aws sns publish \
    --topic-arn "arn:aws:sns:us-east-1:652919303299:arka-topic.fifo" \
    --message "Hello, this is a test message from the CLI!" \
    --message-group-id "group1" \
    --message-deduplication-id "msg1"


aws sqs receive-message \
    --queue-url "https://sqs.us-east-1.amazonaws.com/652919303299/arka-sqs-2" \
    --max-number-of-messages 1 \
    --message-attribute-names "All"

aws sqs receive-message \
    --queue-url "https://sqs.us-east-1.amazonaws.com/652919303299/arka-sqs" \
    --max-number-of-messages 1 \
    --message-attribute-names "All"