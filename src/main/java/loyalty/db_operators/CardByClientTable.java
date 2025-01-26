package loyalty.db_operators;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import loyalty.database.config.CassandraConnectionConfig;
import loyalty.models.CardDTO;

import java.util.LinkedList;
import java.util.List;

public class CardByClientTable {

    public static void createCardByClientTable(CqlSession session, CassandraConnectionConfig config) {
        String query = "CREATE TABLE IF NOT EXISTS card_by_client_email_and_issuer_email (\n" +
                "    client_email TEXT,\n" +
                "    issuer_email TEXT,\n" +
                "    status TEXT,\n" +
                "    PRIMARY KEY (client_email, issuer_email)\n" +
                ");";
        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind();
        boundStatement = boundStatement.setConsistencyLevel(config.getWriteConsistency());
        session.execute(boundStatement);
        System.out.println("CardByClientTable was created successfully.");
    }

    public static List<CardDTO> getCards(CqlSession session, CassandraConnectionConfig config, String clientEmail){
        String query = "SELECT client_email, issuer_email, status FROM card_by_client_email_and_issuer_email WHERE client_email = ?;";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(clientEmail);
        boundStatement = boundStatement.setConsistencyLevel(config.getReadConsistency());
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

    public static void createCard(CqlSession session, CassandraConnectionConfig config, String client, String issuer) {
        String query = "INSERT INTO card_by_client_email_and_issuer_email (client_email, issuer_email, status) VALUES (?, ?, ?)";
        String status = "active";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(client, issuer, status);
        boundStatement = boundStatement.setConsistencyLevel(config.getWriteConsistency());
        session.execute(boundStatement);
    }

    public static void setStatus(CqlSession session, CassandraConnectionConfig config, String client, String issuer, String newStatus) {
        String query = "UPDATE card_by_client_email_and_issuer_email SET status = ? WHERE issuer_email = ? AND client_email = ?";
        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(newStatus, issuer, client);
        boundStatement = boundStatement.setConsistencyLevel(config.getReadConsistency());
        session.execute(boundStatement);
    }

}
