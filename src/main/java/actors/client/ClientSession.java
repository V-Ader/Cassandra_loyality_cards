package actors.client;

import com.datastax.driver.core.Session;
import database.CassandraConnection;
import database.connector.CassandraRandomConnector;
import database.ConnectionException;

public class ClientSession {

    private final CassandraConnection connection;

    private ClientService clientService;

    public ClientSession() throws ConnectionException {
        CassandraRandomConnector connector;
        try {
            connector = CassandraRandomConnector.getInstance();
        } catch (Exception e) {
            throw new ConnectionException("Could not establish the connection. Reason:", e);
        }
        this.connection = connector.connect();

        clientService = new ClientService(connection.getSession());

        clientService.selectClients();
    }

    public void closeConnection() {
        this.connection.close();
    }
}
