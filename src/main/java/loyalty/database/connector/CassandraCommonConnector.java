package loyalty.database.connector;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.internal.core.loadbalancing.DcInferringLoadBalancingPolicy;
import loyalty.database.ConnectionException;
import loyalty.database.config.Address;
import loyalty.database.config.CassandraConfig;
import loyalty.database.config.CassandraConfigReader;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CassandraCommonConnector {
    private CassandraConfig config;

    private CassandraCommonConnector() throws IOException {
        this.config = CassandraConfigReader.getConfig();
    }

    private static final class InstanceHolder {
        private static final CassandraCommonConnector instance;

        static {
            try {
                instance = new CassandraCommonConnector();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static CassandraCommonConnector getInstance() {
        return InstanceHolder.instance;
    }


    public CqlSession connect() throws ConnectionException {
        try {
            this.config = CassandraConfigReader.getConfig();
        } catch (Exception e) {
            throw new ConnectionException("Connection parameters not specified: ", e);
        }
        for(Address address : config.getAddresses()) {
            System.out.printf("%s:%s", address.getAddress(), address.getPort());
        }
        return CqlSession.builder()
                .addContactPoints(convertAddresses(config.getAddresses()))
                .withConfigLoader(DriverConfigLoader.programmaticBuilder()
                    .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, java.time.Duration.ofSeconds(100)) // Increase timeout to 10 seconds
                    .build())
                .withLocalDatacenter("datacenter1")
                .withKeyspace(config.getKeyspace())
                .build();
    }

    public CqlSession connect(int id) throws ConnectionException {
        try {
            this.config = CassandraConfigReader.getConfig();
        } catch (Exception e) {
            throw new ConnectionException("Connection parameters not specified: ", e);
        }
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress(config.getAddresses().get(id).getAddress(), config.getAddresses().get(id).getPort()))
                .withLocalDatacenter("datacenter1")
                .withKeyspace(config.getKeyspace())
                .build();
    }

    private Collection<InetSocketAddress> convertAddresses(List<Address> addresses) {
        return addresses.stream().map(address -> new InetSocketAddress(address.getAddress(), address.getPort())).collect(Collectors.toList());
    }
}
