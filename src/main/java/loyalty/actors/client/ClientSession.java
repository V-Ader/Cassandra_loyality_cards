package loyalty.actors.client;

import loyalty.database.CassandraConnection;
import loyalty.database.connector.CassandraRandomConnector;
import loyalty.database.ConnectionException;

public class ClientSession {

    private final CassandraConnection connection;

    private final ClientService clientService;

    private final String clientEmail;

    private final String issuerEmail;

    public ClientSession(String clientEmail, String issuerEmail) throws ConnectionException {
        CassandraRandomConnector connector;
        try {
            connector = CassandraRandomConnector.getInstance();
        } catch (Exception e) {
            throw new ConnectionException("Could not establish the connection. Reason:", e);
        }
        this.connection = connector.connect();
        this.clientEmail = clientEmail;
        this.issuerEmail = issuerEmail;

        clientService = new ClientService(issuerEmail, connection.getSession());
    }

    public void useToken(){
        clientService.useClientsToken(issuerEmail, 1);
    }

    public void closeConnection() {
        this.connection.close();
    }
}
