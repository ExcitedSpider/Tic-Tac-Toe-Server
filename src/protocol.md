# DQL Protocol

Author: Chew-Yi Feng

## 1. Introduction

DQL(Dictionary Query Language) protocol describe the specification of what the server is expected to receive 
as well as what message should client send.

This protocol is one that suppose to access natural language. 
This protocol support multiple dictionary which enables users to store word definition by categories.

This protocol is not about how to implement the storage or the detailed architecture of servers and clients.

DQL is dictionary query language, which is inspired by SQL. The protocol is amid at implement the same level of concise and readability as SQL.

## 2. Protocol Overview

### 2.1 Transfer Level

This protocol is defined on a **reliable data stream** like TCP. Any specification of unreliable data streams is not considered.

### 2.2 Lexical Tokens

Tokens are composed by characters from UTF-8 encoding.

```
Word = <any strings of characters without space and symbols> | QuotedSentence
QuotedSentence = " <any utf8 characters or EscapeCharacters> "
EscapeCharacters = \n | \" | \t
Symbol = ( | ) | " | ; | . | # | $ 
Newline = <ASCII newline \n>
EOF = "EOF"
```

### 2.3 Statement

Statements are the requests for multiple purposes which sent by clients to servers.
Statements are composed by legit lexical tokens defines in 2.2.
Statements must be complete with all required parameters and may not contain more than one statement.

Each statement can be terminated by newline or end-point symbol(.).

```text
Statement = 
    | <ListDictionary>
    | <CreateDictionary>
    | <QueryDictionary>
    | <QueryWords>
    | <UpsertWords>
    | <DeleteWords>
    | <Exit>
```

### 2.4 Response

Response have 2 parts: status and textual.
Response need to end with a single line of "."

#### 2.4.1 Status

Status indicates the basic status server's response.
Status response lines with 3 digit numeric code and a text sentence to distinguish all responses.
The first digit indicate broadly the success or failure:
Status has and only has one line which ends with a newline symbol.
Status must appear at the first line of response.

```text
200 Success
4xx Bad Request
5xx Server Error 
```

#### 2.4.2 Text Response

Text response is the detailed response corresponding to types of statements.
Text response ends with a single line containing only an end-point (".").
The detailed definition of text response is defined in section 3.

#### 2.4.3 Example

Client send statement:

```text
QUERY bbc abc FROM DEFAULT
```

Server response:

```text
200 Success
bbc: British Broadcasting Corporation
abc: Australian Broadcasting Corporation
.
```

## 3. Statement

### 3.1. ListDictionary

Change current target dictionary.

Formal Definition:

```text
sentence -> "LS" | "LIST"
```

Example

```text
LS
```

Response

```text
200 Success
DEFAULT ChineseDictionary KoreanDictionary
.
```

### 3.2 QueryDictionary

Query single or multiple words

```text
"QUERY" queryClause
queryClause ->  wordList fromClause
fromClause -> ε | "FROM" <dictionary>
wordList -> <word> restWords
restWords -> ε | <word> restWords
```

Example:

```text
QUERY abc bbc
```

```text
200 Success
bbc: British Broadcasting Corporation
abc: Australian Broadcasting Corporation
.
```

### 3.3 UpsertDictionary

Query single or multiple words

```text
"UPSERT" upsertClause
upsertClause -> wordDefs intoClause
wordDefs -> tuple | restWordDefs
restWordDefs -> ε | tuple restWordDefs
intoClause -> "INTO" <dictionay>

tuple -> "(" items ")"
items -> <tupleItem> restItems
restItems -> ε | "," restItems
```

Example:

```text
UPSERT (abc, "Australian Broadcasting Corporation")
200 Success
Insert words: abc
.
```

### 3.4 DeleteWords

This statement delete single or multiple words from dictionary

```text
"DELETE" deleteClause
targetDict -> <dictionary>
```

```text
DELETE bbc
200 Success
Deleted abc
.
```

### 3.5 Create Dictionary

This statement create a new dictionary if possible.

```text
"CREATE DICT" <dictionary>
```

```text
CREATE DICT dict2
200 Success
Create dictionary
.
```

### 3.6 Exit Connection

This statement is used when client want to close connection and leave.

```text
Exit
200 Success
Bye
.
```

## 4 Error Handling

This section focus on error.

### 4.1 Error Response

Error response need to have correct status and textual content as well.
Textual content should be the description of error.

Example:

```text
CREATE dict
400 BadRequest
Error: Dictionary "dict" already exists.
.
```