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
//
//        DriverConfigLoader loader = DriverConfigLoader.programmaticBuilder()
//                .withString(DefaultDriverOption.LOAD_BALANCING_POLICY, "loyalty.database.balancing_policies.RandomLoadBalancingPolicy")
//                .withString(DefaultDriverOption.LOAD_BALANCING_LOCAL_DATACENTER, "datacenter1")
//                .build();

        return CqlSession.builder()
                .addContactPoints(convertAddresses(config.getAddresses()))
                .withLocalDatacenter("datacenter1")
//                .withConfigLoader(loader)
                .withKeyspace(config.getKeyspace())
                .build();
    }

    private Collection<InetSocketAddress> convertAddresses(List<Address> addresses) {
        return addresses.stream().map(address -> new InetSocketAddress(address.getAddress(), address.getPort())).toList();
    }
}
