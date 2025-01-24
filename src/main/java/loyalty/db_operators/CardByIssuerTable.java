package loyalty.db_operators;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import loyalty.database.config.CassandraConnectionConfig;
import loyalty.models.CardDTO;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CardByIssuerTable {
    public static List<CardDTO> getCards(CqlSession session, CassandraConnectionConfig config, String issuer){
        String query = "SELECT client_email, issuer_email, status FROM card_by_issuer_email_and_client_email WHERE issuer_email = ?;";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(issuer);
        boundStatement.setConsistencyLevel(config.getReadConsistency());
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

    public static CardDTO getCard(CqlSession session, CassandraConnectionConfig config, String issuer, String client){
        String query = "SELECT issuer_email, client_email, status FROM card_by_issuer_email_and_client_email WHERE issuer_email = ? AND client_email = ?";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(issuer, client);
        boundStatement.setConsistencyLevel(config.getReadConsistency());
        ResultSet resultSet = session.execute(boundStatement);

            List<CardDTO> cards = new ArrayList<>();

            for (Row row : resultSet) {
                String issuerEmail = row.getString("issuer_email");
                String clientEmail = row.getString("client_email");
                String status = row.getString("status");

                CardDTO card = CardDTO.builder()
                        .issuerEmail(issuerEmail)
                        .clientEmail(clientEmail)
                        .status(status)
                        .tokens(TokensTable.getTokenValue(session, config, row.getString("issuer_email"), row.getString("client_email")))
                        .build();
                cards.add(card);
            }

        return cards.stream().findFirst().orElse(new CardDTO());
    }

    public static void createCard(CqlSession session, CassandraConnectionConfig config, String issuer, String client) {
        String query = "INSERT INTO card_by_issuer_email_and_client_email (client_email, issuer_email, status) VALUES (?, ?, ?)";
        String status = "active";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(client, issuer, status);
        boundStatement.setConsistencyLevel(config.getWriteConsistency());
        session.execute(boundStatement);
    }

    public static void setStatus(CqlSession session, CassandraConnectionConfig config, String issuer, String client, String newStatus) {
        String query = "UPDATE card_by_issuer_email_and_client_email SET status = ? WHERE issuer_email = ? AND client_email = ?";
        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(newStatus, issuer, client);
        boundStatement.setConsistencyLevel(config.getReadConsistency());
        session.execute(boundStatement);
    }

}
