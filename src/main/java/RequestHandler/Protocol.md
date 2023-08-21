# Server Response Protocol Specs

## Status Code
* 200 - Success
* 400 - Bad Request
* 500 - Server Error

## Encoding 
Supporting 2 types of encoding:
* Text
* Json

## Example - Query Words
```
QUERY Yura Nunya
```

Text Response:
```
200 Success
Yura: 
Hello in Yugambeh language. 

Nunya: 
Thank you. 
Pronounce: None-Ya
```

Json

```json
{
  "status": 200,
  "data": {
    "Yura": {
      "meaning": "Hello in Yugambeh language"
    },
    "Nunya": {
      "meaning": "Thank you",
      "pronounce": "none-ya"
    }
  }
}
```

## Example - Others

```
DELETE Yura Nunya
```

Text Response:
```
200 Success
Deleted: Yura Nunya
```

Json Response:
```json
{
  "status": 200,
  "data": ["Yura", "Nunya"]
}
```