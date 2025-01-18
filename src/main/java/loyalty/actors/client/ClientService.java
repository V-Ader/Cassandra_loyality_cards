package loyalty.actors.client;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import loyalty.database.connector.CassandraConnectionConfig;
import loyalty.models.CardDTO;

import java.util.LinkedList;
import java.util.List;

public class ClientService {
    private final String clientEmail;
    Session session;
    CassandraConnectionConfig config;

    public ClientService(String clientEmail, Session session, CassandraConnectionConfig config) {
        this.session = session;
        this.clientEmail = clientEmail;
        this.config = config;
    }

    public List<CardDTO> selectClientsCards(){
        String query = "SELECT client_email, issuer_email, status FROM card_by_client_email_and_issuer_email WHERE client_email = ?;";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(clientEmail);
        boundStatement.setConsistencyLevel(config.getReadConsistency());
        ResultSet resultSet = session.execute(boundStatement);

        List<CardDTO> cards = new LinkedList<>();

        for (Row row : resultSet) {
            CardDTO cardDTO = CardDTO.builder()
                    .clientEmail(row.getString("client_email"))
                    .issuerEmail(row.getString("issuer_email"))
                    .status(row.getString("status"))
                    .tokens(getTokenValue(row.getString("issuer_email")))
                    .build();

            cards.add(cardDTO);
        }
        return cards;
    }

    private long getTokenValue(String issuerEmail) {
        String query = "SELECT tokens FROM tokens_by_issuer_email_and_client_email WHERE issuer_email = ? AND client_email = ?";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(clientEmail, issuerEmail);
        boundStatement.setConsistencyLevel(config.getReadConsistency());
        ResultSet resultSet = session.execute(boundStatement);

        long value = 0;

        for (Row row : resultSet) {
            value = row.getLong("tokens");
        }

        return value;
    }

    public void useClientsToken(String issuerEmail, long value){
        String query = "UPDATE tokens_by_issuer_email_and_client_email SET tokens = tokens - ? WHERE issuer_email = ? AND client_email = ?;";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(value, issuerEmail, clientEmail);
        boundStatement.setConsistencyLevel(config.getWriteConsistency());
        session.execute(boundStatement);
    }
}
