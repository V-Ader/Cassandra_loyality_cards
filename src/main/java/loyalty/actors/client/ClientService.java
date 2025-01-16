package loyalty.actors.client;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import loyalty.models.Card;
import loyalty.models.Token;

import java.util.ArrayList;
import java.util.List;

public class ClientService {
    private final String ownerEmail;
    private final String issuerEmail;
    Session session;

    public ClientService(String ownerEmail,String issuerEmail, Session session) {
        this.session = session;
        this.ownerEmail = ownerEmail;
        this.issuerEmail = issuerEmail;
    }

    public void selectClientsCards(){
        String query = "SELECT status FROM card_by_owner_email_and_issuer_email WHERE owner_email = ? AND issuer_email = ?;";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(ownerEmail, issuerEmail);
        ResultSet resultSet = session.execute(boundStatement);

        List<Card> cards = new ArrayList<>();

        for (Row row : resultSet) {
            String status = row.getString("status");

            Card card = Card.builder()
                    .issuerEmail(issuerEmail)
                    .ownerEmail(ownerEmail)
                    .status(status)
                    .build();

            cards.add(card);
        }

        for (Card card : cards) {
            System.out.println(card);
        }
    }

    public void selectClientsTokens(){
        String query = "SELECT tokens FROM tokens_by_owner_email_and_issuer_email WHERE owner_email = ? AND issuer_email = ?;";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(ownerEmail, issuerEmail);
        ResultSet resultSet = session.execute(boundStatement);

        List<Token> tokens = new ArrayList<>();

        for (Row row : resultSet) {
            long tokens_value = row.getLong("tokens");

            Token token = Token.builder()
                    .issuerEmail(issuerEmail)
                    .ownerEmail(ownerEmail)
                    .tokens(tokens_value)
                    .build();

            tokens.add(token);
        }

        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    public void useClientsToken(long value){
        String query = "UPDATE tokens_by_owner_email_and_issuer_email SET tokens = tokens - ? WHERE owner_email = ? AND issuer_email = ?;";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(value, ownerEmail, issuerEmail);
        ResultSet resultSet = session.execute(boundStatement);
    }
}
