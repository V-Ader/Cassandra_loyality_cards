# Cassandra - Loyalty cards
-----


# Założenia

1. Każdy klient może mieć wiele kart różnych wystawców.
2. Każdy wystawca może mieć wiele klientów.
3. Jeden klient może mieć tylko jedną kartę od danego wystawcy.
4. Każda karta ma wartość, która zmniejsza się po każdym użyciu.
5. Gdy karta ma osiągnąć wartość 0 staje się nie ważna.


# Przykładowe zapytania

## Użytkownik

Jako użytkownik, chcę widzieć swoje karty:
```sql
SELECT client_email, issuer_email, status
FROM card_by_client_email_and_issuer_email
WHERE client_email = ? AND issuer_email = ?;
```

Oraz sprawdzić ich wartości:
```sql
SELECT tokens
FROM tokens_by_issuer_email_and_client_email
WHERE client_email = ? AND issuer_email = ?;
```

## Wystawca

Jako wystawca, chcę widzieć karty wystawione:
```sql
SELECT issuer_email, client_email, status
FROM card_by_issuer_email_and_client_email
WHERE client_email = ? AND issuer_email = ?;
```

## Zarządzanie kartą 

Wartość katry można sprawdzić zapytaniem:
```sql
SELECT tokens
FROM tokens_by_client_email_and_issuer_email
WHERE client_email = ? AND issuer_email = ?;
```

Gdy karta jest aktywna, aby jej użyć można wykonać zapytanie:
```sql
UPDATE tokens_by_client_email_and_issuer_email
SET tokens = tokens - 1
WHERE client_email = ? AND issuer_email = ?;
```


### Tabele

```sql
CREATE TABLE card_by_client_email_and_issuer_email (
    issuer_email TEXT,
    client_email TEXT,
    status TEXT
    PRIMARY KEY (issuer_email, client_email)
);
```


```sql
CREATE TABLE tokens_by_client_email_and_issuer_email (
    client_email TEXT,
    issuer_email TEXT,
    tokens COUNTER,
    PRIMARY KEY ((client_email, issuer_email))
);
```

# Poprawność

Poprawność ma być zapewniona poprzez dodatkowego użytkownika: WATCHER

Założenia:
- poprawność ważniejsza niż czas odpowiedzi

Zasada działania WATCHERA:
1. Pobierz wszystkie liczniki, których wartość jest <= 0. 
2. Dla kart, których wartość równa 0 -> ustaw status odpowiadającej karty na 'inactive'.
3. Dla kart, których wartość jest mniejsza niż 0 -> ustaw status odpowiadającej karty na 'invalid'.

Rezultat:
1. Brak race condition -> istnieje 1 WATCHER. W przypadku większego zapotrzebowania można ich utworzyć więcej z podziałem na np. wystawców.
2. Ostateczna poprawność wartości statusu kart. Gdy wystawca ostatecznie pobierze swoje wystawione karty, będzie widział, czy jest któraś w stanie invalid i będzie mógł podjąć decyzję biznesową wobec danego klienta.

# Testowanie rozwiązania

W celu przetestowania można wykorzystać plik `init_db.sh`.

```bash
docker cp init_db.sh <cassandra_container>:/tmp/init_db.sh
docker exec -it <cassandra_container> bash
cd /tmp
chmod +x init_db.sh
./init_db.sh
```
