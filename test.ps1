$response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -Body (Get-Content login.json -Raw) -ContentType "application/json"
$response | ConvertTo-Json
