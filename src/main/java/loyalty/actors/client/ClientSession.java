package loyalty.actors.client;

import com.datastax.driver.core.*;
import loyalty.database.CassandraConnection;
import loyalty.database.connector.CassandraConnectionConfig;
import loyalty.database.connector.CassandraRandomConnector;
import loyalty.database.ConnectionException;

public class ClientSession {

    private final CassandraConnection connection;

    private final ClientService clientService;

    public ClientSession(String clientEmail, CassandraConnectionConfig connectionConfig) throws ConnectionException {
        CassandraRandomConnector connector;
        try {
            connector = CassandraRandomConnector.getInstance();
        } catch (Exception e) {
            throw new ConnectionException("Could not establish the connection. Reason:", e);
        }
        this.connection = connector.connect();
        clientService = new ClientService(clientEmail, connection.getSession(), connectionConfig);
    }

    public ClientSession(String clientEmail) throws ConnectionException {
        this(clientEmail, CassandraConnectionConfig.getDefaultConfig());
    }

    public void useToken(String issuerEmail){
        clientService.useClientsToken(issuerEmail, 1);
    }

    public void closeConnection() {
        this.connection.close();
    }
}
