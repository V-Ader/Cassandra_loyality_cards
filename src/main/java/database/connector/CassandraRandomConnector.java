package database.connector;

import com.datastax.driver.core.Cluster;
import database.CassandraConnection;
import database.ConnectionException;
import database.config.Address;
import database.config.CassandraConfig;
import database.config.CassandraConfigReader;
import database.config.CassandraConfigService;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class CassandraRandomConnector {

    private static CassandraRandomConnector instance;
    private CassandraConfig config;

    private CassandraRandomConnector() throws IOException {
        this.config = CassandraConfigReader.getConfig();
    }


    public static CassandraRandomConnector getInstance() throws IOException {
        if (instance == null) {
            synchronized (CassandraConfigService.class) {
                if (instance == null) {
                    instance = new CassandraRandomConnector();
                }
            }
        }
        return instance;
    }


    public CassandraConnection connect() throws ConnectionException {
        CassandraConnection connection = new CassandraConnection();

        try {
            this.config = CassandraConfigReader.getConfig();
        } catch (Exception e) {
            throw new ConnectionException("Connection parameters not specified: ", e);
        }

        Address address = getRandomAddress(config);
        Cluster.Builder b = Cluster.builder().addContactPoint(address.getAddress());
        if (address.getPort() != null) {
            b.withPort(address.getPort());
        }
        try {
            connection.setCluster(b.build());
            connection.setSession(connection.getCluster().connect(config.getKeyspace()));
        } catch (Exception e) {
            throw new ConnectionException("Failed to connect to Cassandra: ", e);
        }
        System.out.printf("connected with: %s : %d\n", address.getAddress(), address.getPort());
        return connection;
    }

    private static Address getRandomAddress(CassandraConfig config) {
        return config.getAddresses()
                .get(ThreadLocalRandom.current().nextInt(config.getAddresses().size()));
    }
}
