$token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9NQU5BR0VSIiwic3ViIjoibWFuYWdlcjEiLCJpYXQiOjE3NzcwMTgyNTYsImV4cCI6MTc3NzEwNDY1Nn0.JPjb8WshXIIFgjLS9CwvojdiiYdcXy__O_SvCYLHgWQ"
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/manager/classes" -Method Get -Headers @{ Authorization = "Bearer $token" }
$response | ConvertTo-Json
