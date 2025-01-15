package actors.client;

import com.datastax.driver.core.Session;
import database.connector.CassandraRandomConnector;
import database.ConnectionException;

public class ClientSession {

    private final Session session;

    private static ClientService clientService;

    public ClientSession() throws ConnectionException {
        CassandraRandomConnector connector;
        try {
            connector = CassandraRandomConnector.getInstance();
        } catch (Exception e) {
            throw new ConnectionException("Could not establish the connection. Reason:", e);
        }
        session = connector.connect().getSession();
    }
}
