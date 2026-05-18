# Word Frequency Analyzer

A small Spring Boot service that counts word frequencies in arbitrary text.
Built against Java 25 and Spring Boot 4.0.3.

## Running

```bash
mvn spring-boot:run
```

The app listens on `http://localhost:8080`.

## Tests

```bash
mvn test
```

## API

### `POST /api/word-frequency/highest-frequency`

```bash
curl -X POST http://localhost:8080/api/word-frequency/highest-frequency \
  -H 'Content-Type: application/json' \
  -d '{"text":"The car is the color purple."}'
# -> {"frequency":2}
```

### `POST /api/word-frequency/frequency-for-word`

```bash
curl -X POST http://localhost:8080/api/word-frequency/frequency-for-word \
  -H 'Content-Type: application/json' \
  -d '{"text":"The car is the color purple.","word":"the"}'
# -> {"frequency":2}
```

### `POST /api/word-frequency/most-frequent-words?n=3`

```bash
curl -X POST 'http://localhost:8080/api/word-frequency/most-frequent-words?n=3' \
  -H 'Content-Type: application/json' \
  -d '{"text":"The cat walks over the staircase"}'
# -> [{"word":"the","frequency":2},{"word":"cat","frequency":1},{"word":"over","frequency":1}]
```

## Design notes

### Tokenisation
The spec defines a word as "a sequence of one or more characters (a-z A-Z)",
implemented here as the regex `[a-zA-Z]+`. Anything that is not an ASCII
letter is a separator: digits, punctuation, whitespace, accented characters,
apostrophes. Tokens are lowercased with `Locale.ROOT` so the result is
identical on every host; using the default locale would break case-folding
on Turkish systems (`I` maps to `ı`, not `i`).
