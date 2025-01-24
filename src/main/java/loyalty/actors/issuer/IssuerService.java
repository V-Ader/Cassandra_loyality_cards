package loyalty.actors.issuer;

import com.datastax.driver.core.*;
import loyalty.database.config.CassandraConnectionConfig;
import loyalty.db_operators.CardByClientTable;
import loyalty.db_operators.CardByIssuerTable;
import loyalty.db_operators.TokensTable;
import loyalty.models.CardDTO;

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
        this.createCardOnClientExecution(clientEmail);
        this.createCardOnIssuerExecution(clientEmail);
        this.createTokensExecution(clientEmail, tokens);
    }

    private void createCardOnClientExecution(String clientEmail) {
        CardByClientTable.createCard(session, config, clientEmail, email);
    }
    private void createCardOnIssuerExecution(String clientEmail) {
        CardByIssuerTable.createCard(session, config, email, clientEmail);
    }

    private void createTokensExecution(String clientEmail, long tokens)  {
        TokensTable.createTokens(session, config, email, clientEmail, tokens);
    }

    public List<CardDTO> getAllCards(){
        return CardByIssuerTable.getCards(session, config, email);
    }

    public void changeStatus(String clientEmail, String newStatus) {
        CardByIssuerTable.setStatus(session, config, email, clientEmail, newStatus);
    }

    public CardDTO getCard(String clientEmail){
        return CardByIssuerTable.getCard(session, config, email, clientEmail);
    }
}