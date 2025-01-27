package loyalty.actors.client;


import com.datastax.oss.driver.api.core.CqlSession;
import loyalty.db_operators.CardByClientTable;
import loyalty.db_operators.TokensTable;
import loyalty.database.config.CassandraConnectionConfig;
import loyalty.models.CardDTO;

import java.util.List;

public class ClientService {
    private final String clientEmail;
    CqlSession session;
    CassandraConnectionConfig config;

    public ClientService(String clientEmail, CqlSession session) {
        this(clientEmail, session, CassandraConnectionConfig.getDefault());
    }

    public ClientService(String clientEmail, CqlSession session, CassandraConnectionConfig config) {
        this.session = session;
        this.clientEmail = clientEmail;
        this.config = config;
    }

    public List<CardDTO> selectClientsCards(){
        return CardByClientTable.getCards(session, config, clientEmail);
    }

    public boolean useClientsToken(String issuerEmail, long value){
        return TokensTable.useClientsToken(session, config, issuerEmail, clientEmail, value);
    }
}
