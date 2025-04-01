# Cassandra - Loyalty cards
-----

# Assumptions

1. Each customer can have multiple cards from different issuers..
2. Each issuer can have multiple customers.
3. A customer can have only one card from a given issuer.
4. Each card has a value that decreases after each use.
5. When a card reaches a value of 0, it becomes invalid.


# Sample Queries

## User

As a user, I want to see my cards
```sql
SELECT client_email, issuer_email, status
FROM card_by_client_email_and_issuer_email
WHERE client_email = ? AND issuer_email = ?;
```

And check their values:
```sql
SELECT tokens
FROM tokens_by_issuer_email_and_client_email
WHERE client_email = ? AND issuer_email = ?;
```

## Issuer

As an issuer, I want to see the issued cards:
```sql
SELECT issuer_email, client_email, status
FROM card_by_issuer_email_and_client_email
WHERE client_email = ? AND issuer_email = ?;
```

## Card Management 
The card value can be checked with the following query:
```sql
SELECT tokens
FROM tokens_by_client_email_and_issuer_email
WHERE client_email = ? AND issuer_email = ?;
```

If the card is active, it can be used with the following query:
```sql
UPDATE tokens_by_client_email_and_issuer_email
SET tokens = tokens - n
WHERE client_email = ? AND issuer_email = ?;
```


### Tables

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

# Consistency

Consistency is ensured through an additional user: WATCHER

WATCHER's operation principle:
1. Retrieve all counters with a value of ≤ 0.. 
   1. For cards with a value of 0 → set the corresponding card status to `INACTIVE`
   2. For cards with a value less than 0 → set the corresponding card status to `INVALID`.

Outcome:
1. No race conditions → only one `WATCHER` exists. If more capacity is needed, additional WATCHER instances can be created, e.g., per issuer.
2. Ultimate correctness of card status values. When an issuer retrieves their issued cards, they will see if any are in the INVALID state and can take appropriate business actions regarding the customer.


# Stress Testing

Stress tests were conducted as follows:
 
1) Create `i` issuers and c customers. Then, `i * c` cards are created in batches with a default value of 50. 
2) Create `n` client sessions, in which operations on the cards will be attempted. 
3)After each "allowed" operation by a client, a log entry is inserted into the logs table containing:
   1) The email address of the client using the card.
   2) The email address of the issuer
   3) The operation timestamp
   4) The card's value before use
   5) The card's value after use
4) After a series of operations, the `WATCHER` is executed, verifying the logs table to check if any operations were unauthorized.
5) If an unauthorized operation is detected, the card is marked as `INVALID`, and all logs related to invalid transactions are removed.
   
# Testing the Solution

To test the solution, you can use the `init_db.sh` script.

```bash
docker cp init_db.sh <cassandra_container>:/tmp/init_db.sh
docker exec -it <cassandra_container> bash
cd /tmp
chmod +x init_db.sh
./init_db.sh
```
