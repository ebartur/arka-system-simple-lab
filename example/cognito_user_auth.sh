# https://cognito-idp.us-east-1.amazonaws.com/us-east-1_67ZFMSIH1/.well-known/openid-configuration
# https://cognitdns.auth.us-east-1.amazoncognito.com

curl -X POST https://cognitdns.auth.us-east-1.amazoncognito.com/oauth2/token \
    -H 'Content-Type: application/x-www-form-urlencoded' \
    --data-urlencode 'grant_type=authorization_code' \
    --data-urlencode 'client_id=clientid' \
    --data-urlencode 'code=code' \
    --data-urlencode 'client_secret=clientsecret' \
    --data-urlencode 'redirect_uri=https://redirect_uri' | jq .

