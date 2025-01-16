package loyalty.database.connector;

import com.datastax.driver.core.Cluster;
import loyalty.database.CassandraConnection;
import loyalty.database.ConnectionException;
import loyalty.database.config.Address;
import loyalty.database.config.CassandraConfig;
import loyalty.database.config.CassandraConfigReader;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class CassandraRandomConnector {

    private CassandraConfig config;

    private CassandraRandomConnector() throws IOException {
        this.config = CassandraConfigReader.getConfig();
    }


    private static final class InstanceHolder {
        private static final CassandraRandomConnector instance;

        static {
            try {
                instance = new CassandraRandomConnector();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static CassandraRandomConnector getInstance() {
        return InstanceHolder.instance;
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
