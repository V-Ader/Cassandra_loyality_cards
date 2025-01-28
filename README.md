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

Wartość karty można sprawdzić zapytaniem:
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
    client_email TEXT,
    issuer_email TEXT,
    status TEXT
    PRIMARY KEY (client_email, issuer_email)
);
```

```sql
CREATE TABLE card_by_issuer_email_and_client_email (
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
```sql
CREATE TABLE logs_by_issuer_email_and_ (
    issuer_email TEXT,
    client_email TEXT,
    change_timestamp TIMESTAMP, 
    previous_value INT,
    tokens COUNTER,
    PRIMARY KEY ((client_email, issuer_email), change_timestamp)
);
```

# Poprawność

Poprawność ma być zapewniona poprzez dodatkowego użytkownika: WATCHER

Zasada działania `WATCHERA`:
1. Pobierz wszystkie liczniki, których wartość jest <= 0. 
   1. Dla kart, których wartość równa 0 -> ustaw status odpowiadającej karty na `INACTIVE`.
   2. Dla kart, których wartość jest mniejsza niż 0 -> ustaw status odpowiadającej karty na `INVALID`.

__Rezultat__:
1. Brak race condition -> istnieje 1 WATCHER. W przypadku większego zapotrzebowania można ich utworzyć więcej z podziałem na np. wystawców.
2. Ostateczna poprawność wartości statusu kart. Gdy wystawca ostatecznie pobierze swoje wystawione karty, będzie widział, czy jest któraś w stanie `INVALID` i będzie mógł podjąć decyzję biznesową wobec danego klienta.

# Stress testy

Stres testy przeprowadziliśmy w następujący sposób: 
1) Tworzymy `i` issuerów oraz `c` klientów. Seriami jest tworzone `i*c` kart z domyślną wartością 50. 
2) Tworzymy `n` sesji klienckich, w ramach których będziemy próbowali wykonać operację na karcie. 
3) Po każdej "dozwolonej" operacji ze strony klienta, jest wstawiany wiersz to tabeli logów notujący informację zawierające takie dane jak:
   1) adres email klienta używającego karty
   2) adres email wystawcy
   3) timestamp operacji
   4) wartość karty przed jej użyciem
   5) wartość karty po jej użyciu
4) Po serii operacji uruchamiany jest `WATCHER`, który weryfikuję tabelę z logami sprawdzając czy któraś z operacji była niedozwolona. 
5) Jeżeli wyszuka niedozwoloną operację, karta przechodzi w stan `INVALID` i wszystkie logi, które były niedozwolone z globalnego punktu widzenia są usuwane. 
# Testowanie rozwiązania

W celu przetestowania można wykorzystać plik `init_db.sh`.

```bash
docker cp init_db.sh <cassandra_container>:/tmp/init_db.sh
docker exec -it <cassandra_container> bash
cd /tmp
chmod +x init_db.sh
./init_db.sh
```
