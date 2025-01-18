package loyalty.actors.issuer;

import com.datastax.driver.core.*;
import loyalty.database.connector.CassandraConnectionConfig;
import loyalty.models.Card;
import loyalty.models.CardDTO;

import java.util.ArrayList;
import java.util.List;

public class IssuerService {
    Session session;
    CassandraConnectionConfig config;
    private final String email;

    public IssuerService(String email, Session session, CassandraConnectionConfig config) {
        this.session = session;
        this.email = email;
        this.config = config;
    }

    public void createCard(String clientEmail, long tokens){
        this.createCardExecution(clientEmail);
        this.createTokensExecution(clientEmail, tokens);
    }

    private void createCardExecution(String clientEmail) {
        String query = "INSERT INTO card_by_client_email_and_issuer_email (issuer_email, client_email, status) VALUES (?, ?, ?)";
        String status = "active";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(email, clientEmail, status);
        boundStatement.setConsistencyLevel(config.getWriteConsistency());
        session.execute(boundStatement);
    }
    private void createTokensExecution(String clientEmail, long tokens)  {
        String query = "UPDATE tokens_by_issuer_email_and_client_email SET tokens = tokens + ? WHERE issuer_email = ? AND client_email = ?";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(tokens, email, clientEmail);
        boundStatement.setConsistencyLevel(config.getWriteConsistency());
        session.execute(boundStatement);
    }

    public List<CardDTO> getAllCards(){
        String query = "SELECT issuer_email, client_email, status FROM card_by_client_email_and_issuer_email WHERE issuer_email = ?";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(email);
        boundStatement.setConsistencyLevel(config.getReadConsistency());
        ResultSet resultSet = session.execute(boundStatement);

        List<CardDTO> cards = new ArrayList<>();

        for (Row row : resultSet) {
            CardDTO card = CardDTO.builder()
                    .issuerEmail(row.getString("issuer_email"))
                    .clientEmail(row.getString("client_email"))
                    .status(row.getString("status"))
                    .tokens(getTokenValue(row.getString("client_email")))
                    .build();
            cards.add(card);
        }

        return cards;
    }

    private long getTokenValue(String clientEmail) {
        String query = "SELECT tokens FROM tokens_by_issuer_email_and_client_email WHERE issuer_email = ? AND client_email = ?";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(email, clientEmail);
        boundStatement.setConsistencyLevel(config.getReadConsistency());
        ResultSet resultSet = session.execute(boundStatement);

        long value = 0;

        for (Row row : resultSet) {
            value = row.getLong("tokens");
        }

        return value;
    }

    public CardDTO getCard(String clientEmail){
        String query = "SELECT issuer_email, client_email, status FROM card_by_issuer_email_and_client_email WHERE issuer_email = ? AND client_email = ?";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(email, clientEmail);
        boundStatement.setConsistencyLevel(config.getReadConsistency());
        ResultSet resultSet = session.execute(boundStatement);

        List<CardDTO> cards = new ArrayList<>();

        for (Row row : resultSet) {
            CardDTO card = CardDTO.builder()
                    .issuerEmail(row.getString("issuer_email"))
                    .clientEmail(row.getString("client_email"))
                    .status(row.getString("status"))
                    .tokens(getTokenValue(clientEmail))
                    .build();

            cards.add(card);
        }

        return cards.stream().findFirst().orElse(new CardDTO());
    }
}