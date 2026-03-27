token="token"
access_token="accesstoke"
id_token="idtoken"


curl https://apiid.execute-api.us-east-1.amazonaws.com/order/api/health \
    -H "Authorization: Bearer $access_token" | jq .


curl https://apiid.execute-api.us-east-1.amazonaws.com/order/api/health \
    -H "Authorization: Bearer $id_token" | jq .