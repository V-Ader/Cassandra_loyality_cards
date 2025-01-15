package actors.client;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import models.Card;

import java.util.ArrayList;
import java.util.List;

public class ClientService {
    Session session;

    public ClientService(Session session) {
        this.session = session;
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
