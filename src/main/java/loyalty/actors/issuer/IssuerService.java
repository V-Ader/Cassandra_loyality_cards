package loyalty.actors.issuer;

import com.datastax.driver.core.*;
import loyalty.models.Card;
import loyalty.models.CardDTO;

import java.util.ArrayList;
import java.util.List;

public class IssuerService {
    Session session;
    private final String email;

    public IssuerService(String email, Session session) {
        this.session = session;
        this.email = email;
    }

    public void createCard(String owner_email, long tokens){
        this.createCardExecution(owner_email);
        this.createTokensExecution(owner_email, tokens);
    }

    private void createCardExecution(String owner_email) {
        String query = "INSERT INTO card_by_owner_email_and_issuer_email (issuer_email, owner_email, status) VALUES (?, ?, ?)";
        String status = "active";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(email, owner_email, status);
        session.execute(boundStatement);
    }
    private void createTokensExecution(String owner_email, long tokens)  {
        String query = "UPDATE tokens_by_owner_email_and_issuer_email SET tokens = tokens + ? WHERE issuer_email = ? AND owner_email = ?";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(tokens, email, owner_email);
        session.execute(boundStatement);
    }

    public void updateCard(){

    }

    public List<CardDTO> getAllCards(){
        String query = "SELECT issuer_email, owner_email, status FROM card_by_owner_email_and_issuer_email WHERE issuer_email = ?";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(email);
        ResultSet resultSet = session.execute(boundStatement);

        List<CardDTO> cards = new ArrayList<>();

        for (Row row : resultSet) {
            String issuerEmail = row.getString("issuer_email");
            String ownerEmail = row.getString("owner_email");
            String status = row.getString("status");

            CardDTO card = CardDTO.builder()
                    .issuerEmail(issuerEmail)
                    .ownerEmail(ownerEmail)
                    .status(status)
                    .tokens(getTokenValue(ownerEmail))
                    .build();

            cards.add(card);
        }

        return cards;
    }

    private long getTokenValue(String client_email) {
        String query = "SELECT tokens FROM tokens_by_owner_email_and_issuer_email WHERE issuer_email = ? AND owner_email = ?";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(email, client_email);
        ResultSet resultSet = session.execute(boundStatement);

        long value = 0;

        for (Row row : resultSet) {
            value = row.getLong("tokens");
        }

        return value;
    }

    public void getCard(){

    }

    public void selectClients(){
        String query = "SELECT * FROM card_by_owner_email_and_issuer_email";

        // Execute the query
        ResultSet resultSet = session.execute(query);

        List<Card> cards = new ArrayList<>();

        for (Row row : resultSet) {
            String issuerEmail = row.getString("issuer_email");
            String ownerEmail = row.getString("owner_email");
            String status = row.getString("status");

            Card card = Card.builder()
                    .issuerEmail(issuerEmail)
                    .ownerEmail(ownerEmail)
                    .status(status)
                    .build();

            cards.add(card);
        }

        for (Card card : cards) {
            System.out.println(card.getIssuerEmail());
            System.out.println(card.getOwnerEmail());
            System.out.println(card.getStatus());
        }
    }
}