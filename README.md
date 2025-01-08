# Założenia

1. Każdy klient może mieć wiele kart rówżnych wystawców.
2. Każdy wystawca może mieć wiele kleintów.
3. Jeden jeden klient może mieć tylko jedną kartę od danego wystawcy
4. Każda karta ma wartość, która zmniejsza się po każdym użyciu.
5. Gdy karta ma osiąga wartość 0 staje się nie ważna.


# Przykładowe zapytania

## Użytkownik

Jako użytkownik, chcę widzieć swoje karty
```sql
SELECT card_id, issuer_email, status
FROM card_by_owner_email_and_issuer_email
WHERE owner_email = ?;
```

Oraz sprawdzić ich wartości
```sql
SELECT tokens
FROM tokens_by_owner_email_and_issuer_email
WHERE owner_email = ? AND issuer_email = ?;
```

## Wystawca

Jako wystawca, chcę widzieć karty wystawione
```sql
SELECT card_id, owner_email, status
FROM card_by_owner_email_and_issuer_email
WHERE issuer_email = ? AND owner_email = ?;
```

## Zarządzanie kartą 

wartość katry można sprawdzić zapytaniem
```sql
SELECT tokens
FROM tokens_by_owner_email_and_issuer_email
WHERE owner_email = ? AND issuer_email = ?;
```

Gdy karta jest aktywna, aby jej użyć można wykonać zapytanie:
```sql
UPDATE tokens_by_owner_email_and_issuer_email
SET tokens = tokens - 1
WHERE owner_email = ? AND issuer_email = ?;
```


### Tabele

```sql
CREATE TABLE card_by_owner_email_and_issuer_email (
    issuer_email TEXT,
    owner_email TEXT,
    status TEXT
    PRIMARY KEY (issuer_email, owner_email)
);
```


```sql
CREATE TABLE tokens_by_owner_email_and_issuer_email (
    owner_email TEXT,
    issuer_email TEXT,
    tokens COUNTER,
    PRIMARY KEY ((owner_email, issuer_email))
);
```

# Poprawność

Poprawność ma być zapewniona poprzez dodatkowego użytkownika: WATCHER

Założenia:
- poprawność ważniejsza niż czas odpowiedzi

Zasada działania WATCHERA:
1. Pobierz wszystkei liczniki, których wartość jest <= 0. 
2. Dla kart, których wartość = 0 -> ustaw status na 'inactive'
3. Dla kart, których wartość jest  < 0 -> ustaw status na 'invalid'

Rezultat:
1. brak race condition -> istnieje 1 WATCHER. W przypadku większej większego zapotrzebowania można ich utworzyć więcej z podziałem na np. wystawców.
2. ostateczna poprawność wartości statusu kart. Gdy wystawca ostatecznie pobierze swoje wystawione karty, będzie widział, czy jest któraś w stanie invalid i będzie mógł podjąć decyzję biznesową.
