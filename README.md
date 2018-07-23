A sandbox - self-contained testing environment that mimics the live TestPay production environment. 

When the customer is ready to pay for goods or services on your website, they select the
TestPay payment option on your website.
2. You obtain the OAuth access token (if needed)
3. You request the payment by passing customer email and transaction details to the
TestPay
4. TestPay provides you with asynchronous notification, sent to your webhook listener,
confirming the transaction details and status


// get access token

curl -v http://localhost:8080/oauth2/token -H "Accept: application/json" -H "Accept-Language: en_US" -u "user:secret"  -d "grant_type=client_credentials"   

// payment

curl -v http://localhost:8080/payments/payment -H "Content-Type: application/json" -H "Authorization: Bearer <Access-Token>" -d '{"intent": "order","notification_url": "https://example.com/your_notification_url","payer": {"email": "test@example.com"},"transaction": {"external_id": "123456789","amount": {"value": "7.47","currency": "USD"},"description": "The payment transaction description"}}'



{
          "intent": "order",
           "notification_url": "https://example.com/your_notification_url",
            "payer": {
                 "email": "test@example.com"
              },
             "transaction":{
                  "external_id": "123456789",
                  "amount":{
                       "value":"7.47",
                       "currency": "USD"
                  },
                  "description": "The payment transaction description"
             }
}
