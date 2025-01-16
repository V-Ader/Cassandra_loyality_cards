package loyalty.actors.issuer;

import loyalty.database.CassandraConnection;
import loyalty.database.ConnectionException;
import loyalty.database.connector.CassandraRandomConnector;
import loyalty.models.CardDTO;

import java.util.List;

public class IssuerSession {
    private final CassandraConnection connection;

    private final IssuerService service;

    public IssuerSession(String email) throws ConnectionException {
        CassandraRandomConnector connector;
        try {
            connector = CassandraRandomConnector.getInstance();
        } catch (Exception e) {
            throw new ConnectionException("Could not establish the connection. Reason:", e);
        }
        this.connection = connector.connect();
        service = new IssuerService(email, connection.getSession());
    }

    public void createCard(String owner_email, long tokens){
        service.createCard(owner_email, tokens);
        System.out.println("Card created");
    }

    public void updateCard(){

    }

    public void getAllCards(){
        List<CardDTO> cards =  service.getAllCards();
        for (CardDTO card : cards) {
            System.out.println("----------------------------------");
            System.out.println("|           Card Details         |");
            System.out.println("|--------------------------------|");
            System.out.printf("| Issuer Email: %-17s|\n", card.getIssuerEmail());
            System.out.printf("| Owner Email:  %-17s|\n", card.getOwnerEmail());
            System.out.printf("| Status:       %-17s|\n", card.getStatus());
            System.out.printf("| tokens:       %d|\n", card.getTokens());
            System.out.println("----------------------------------");
            System.out.println();
        }
    }

    public CardDTO getCard(String client){
        return service.getCard(client);
    }

    public void closeConnection() {
        this.connection.close();
    }
}
