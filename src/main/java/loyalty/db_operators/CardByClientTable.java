package loyalty.db_operators;

import com.datastax.driver.core.*;
import loyalty.database.config.CassandraConnectionConfig;
import loyalty.models.CardDTO;

import java.util.LinkedList;
import java.util.List;

public class CardByClientTable {
    public static List<CardDTO> getCards(Session session, CassandraConnectionConfig config, String clientEmail){
        String query = "SELECT client_email, issuer_email, status FROM card_by_client_email_and_issuer_email WHERE client_email = ?;";

        PreparedStatement preparedStatement = session.prepare(query).setConsistencyLevel(config.getReadConsistency());
        BoundStatement boundStatement = preparedStatement.bind(clientEmail);
        ResultSet resultSet = session.execute(boundStatement);

        List<CardDTO> cards = new LinkedList<>();

        for (Row row : resultSet) {
            CardDTO cardDTO = CardDTO.builder()
                    .clientEmail(row.getString("client_email"))
                    .issuerEmail(row.getString("issuer_email"))
                    .status(row.getString("status"))
                    .tokens(TokensTable.getTokenValue(session, config, row.getString("issuer_email"), row.getString("client_email")))
                    .build();
            cards.add(cardDTO);
        }
        return cards;
    }

    public static void createCard(Session session, CassandraConnectionConfig config, String client, String issuer) {
        String query = "INSERT INTO card_by_client_email_and_issuer_email (client_email, issuer_email, status) VALUES (?, ?, ?)";
        String status = "active";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(client, issuer, status);
        boundStatement.setConsistencyLevel(config.getWriteConsistency());
        session.execute(boundStatement);
    }

    public static void setStatus(Session session, CassandraConnectionConfig config, String client, String issuer, String newStatus) {
        String query = "UPDATE card_by_client_email_and_issuer_email SET status = ? WHERE issuer_email = ? AND client_email = ?";
        PreparedStatement preparedStatement = session.prepare(query).setConsistencyLevel(config.getReadConsistency());
        BoundStatement boundStatement = preparedStatement.bind(newStatus, issuer, client);
        session.execute(boundStatement);
    }

}
