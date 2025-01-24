package loyalty;

import com.datastax.oss.driver.api.core.CqlSession;
import loyalty.actors.client.ClientService;
import loyalty.database.ConnectionException;
import loyalty.database.connector.CassandraCommonConnector;
import loyalty.models.CardDTO;

public class Main {
    public static void main(String[] args) {
        String clientEmail = "client1@example.com";
        String issuerEmail = "issuer1@example.com";

        CassandraCommonConnector connector = CassandraCommonConnector.getInstance();
        CqlSession session;
        try {
            session = connector.connect();
        } catch (ConnectionException e) {
            throw new RuntimeException(e);
        }

        ClientService service = new ClientService(clientEmail, session);
        for(CardDTO card : service.selectClientsCards()) {
            System.out.println(card.toString());
        }

        session.close();

        return;
    }
}
