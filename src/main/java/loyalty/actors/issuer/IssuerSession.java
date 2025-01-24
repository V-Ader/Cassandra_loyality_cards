package loyalty.actors.issuer;

import loyalty.database.CassandraConnection;
import loyalty.database.ConnectionException;
import loyalty.database.config.CassandraConnectionConfig;
import loyalty.database.connector.CassandraRandomConnector;
import loyalty.models.CardDTO;

import java.util.List;

public class IssuerSession {
    private final CassandraConnection connection;

    private final IssuerService service;


    public IssuerSession(String email) throws ConnectionException {
        this(email, CassandraConnectionConfig.getDefault());

    }
    public IssuerSession(String email, CassandraConnectionConfig config) throws ConnectionException {
        CassandraRandomConnector connector;
        try {
            connector = CassandraRandomConnector.getInstance();
        } catch (Exception e) {
            throw new ConnectionException("Could not establish the connection. Reason:", e);
        }
        this.connection = connector.connect();
        service = new IssuerService(email, connection.getSession(), config);
    }

    public void createCard(String clientEmail, long tokens){
        service.createCard(clientEmail, tokens);
    }

    public void updateCard(String clientEmail, String newStatus){
        service.changeStatus(clientEmail, newStatus);
    }

    public List<CardDTO> getAllCards(){
        return service.getAllCards();
    }

    public CardDTO getCard(String client){
        return service.getCard(client);
    }

    public void closeConnection() {
        this.connection.close();
    }
}
