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

CREATE TABLE IF NOT EXISTS card_by_issuer_email_and_client_email (
    issuer_email TEXT,
    client_email TEXT,
    status TEXT,
    PRIMARY KEY (issuer_email, client_email)
);

CREATE TABLE IF NOT EXISTS card_by_client_email_and_issuer_email (
    client_email TEXT,
    issuer_email TEXT,
    status TEXT,
    PRIMARY KEY (client_email, issuer_email)
);

CREATE TABLE IF NOT EXISTS tokes_by_issuer_email_and_client_email (
    issuer_email TEXT,
    client_email TEXT,
    tokens COUNTER,
    PRIMARY KEY ((client_email, issuer_email))
);
"

echo "Database setup completed successfully."
