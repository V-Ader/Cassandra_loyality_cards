package loyalty.actors.watcher;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import loyalty.database.ConnectionException;
import loyalty.database.connector.CassandraRandomConnector;

public class TablesInitializer {
    private final Session session;

    public TablesInitializer(Session session) {
        this.session = session;
    }

    public void initTables() {
        createTableCardByIssuerEmailAndClientEmail();
        createTableCardByClientEmailAndIssuerEmail();
        createTableTokensByIssuerEmailAndClientEmail();
    }

    private void createTableCardByIssuerEmailAndClientEmail() {
        String query = """
        CREATE TABLE IF NOT EXISTS card_by_issuer_email_and_client_email (
            issuer_email TEXT,
            client_email TEXT,
            status TEXT,
            PRIMARY KEY (issuer_email, client_email)
        );
    """;
        executeQuery(query);
    }

    private void createTableCardByClientEmailAndIssuerEmail() {
        String query = """
        CREATE TABLE IF NOT EXISTS card_by_client_email_and_issuer_email (
            client_email TEXT,
            issuer_email TEXT,
            status TEXT,
            PRIMARY KEY (client_email, issuer_email)
        );
    """;
        executeQuery(query);
    }

    private void createTableTokensByIssuerEmailAndClientEmail() {
        String query = """
        CREATE TABLE IF NOT EXISTS tokens_by_issuer_email_and_client_email (
            issuer_email TEXT,
            client_email TEXT,
            tokens COUNTER,
            PRIMARY KEY ((client_email, issuer_email))
        );
    """;
        executeQuery(query);
    }

    private void executeQuery(String query) {
        try {
            session.execute(new SimpleStatement(query));
            System.out.println("Table created or already exists: " + query);
        } catch (Exception e) {
            System.err.println("Failed to execute query: " + query);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws ConnectionException {
        CassandraRandomConnector connector;
        try {
            connector = CassandraRandomConnector.getInstance();
        } catch (Exception e) {
            throw new ConnectionException("Could not establish the connection. Reason:", e);
        }
        TablesInitializer init = new TablesInitializer(connector.connect().getSession());
        init.initTables();

        init.session.close();
    }
}
