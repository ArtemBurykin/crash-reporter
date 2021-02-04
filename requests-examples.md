## To report a crash
```bash
curl -X POST localhost:8787/report-crash -d "{\"message\":\"test\", \"stack\": \"test.js:10\"}" -H "Content-Type: application/json" -w %{time_total}
```
