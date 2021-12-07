## To report a crash
```bash
curl -X POST localhost:8786/report-crash \
    -d "{\"message\":\"test\", \"stack\": \"test.js:10\", \"additionalData\":{\"device\": \"Example device\", \"os\": \"Android 11\"}}" \
    -H "Content-Type: application/json" -w %{time_total}
```

```bash
curl -X POST localhost:8786/report-crash \
    -d "{\"message\":\"test\", \"stack\": \"test.js:10\"}" \
    -H "Content-Type: application/json" -w %{time_total}
```
