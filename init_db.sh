#!/bin/bash

# Cassandra connection details
CASSANDRA_HOST="127.0.0.1"
CASSANDRA_PORT="9042"

# Function to execute CQL commands
execute_cql() {
    local query="$1"
    echo "Executing CQL: $query"
    echo "$query" | cqlsh $CASSANDRA_HOST $CASSANDRA_PORT
}

# Step 1: Create Keyspace and Tables
echo "Setting up the loyalty keyspace and tables..."
execute_cql "
CREATE KEYSPACE IF NOT EXISTS loyalty
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 3};

USE loyalty;

CREATE TABLE IF NOT EXISTS card_by_owner_email_and_issuer_email (
    issuer_email TEXT,
    owner_email TEXT,
    status TEXT,
    PRIMARY KEY (issuer_email, owner_email)
);

CREATE TABLE IF NOT EXISTS tokens_by_owner_email_and_issuer_email (
    owner_email TEXT,
    issuer_email TEXT,
    tokens COUNTER,
    PRIMARY KEY ((owner_email, issuer_email))
);
"

# Step 2: Insert Sample Data (Optional)
echo "Inserting sample data..."
execute_cql "
USE loyalty;

INSERT INTO card_by_owner_email_and_issuer_email (issuer_email, owner_email, status)
VALUES ('issuer1@example.com', 'owner1@example.com', 'active');

UPDATE tokens_by_owner_email_and_issuer_email
SET tokens = tokens + 100
WHERE owner_email = 'owner1@example.com' AND issuer_email = 'issuer1@example.com';
"

echo "Database setup completed successfully."
