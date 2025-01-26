package loyalty.database;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import loyalty.database.config.Address;
import loyalty.database.config.CassandraConfig;
import loyalty.database.config.CassandraConfigReader;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CassandraInitConnector {
    private CassandraConfig config;

    private CassandraInitConnector() throws IOException {
        this.config = CassandraConfigReader.getConfig();
    }

    private static final class InstanceHolder {
        private static final CassandraInitConnector instance;

        static {
            try {
                instance = new CassandraInitConnector();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static CassandraInitConnector getInstance() {
        return InstanceHolder.instance;
    }

    public CqlSession getInitSession(int id) throws  ConnectionException  {
        try {
            this.config = CassandraConfigReader.getConfig();
        } catch (Exception e) {
            throw new ConnectionException("Connection parameters not specified: ", e);
        }
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress(config.getAddresses().get(id).getAddress(), config.getAddresses().get(id).getPort()))
                .withLocalDatacenter("datacenter1")
                .build();
    }

    private Collection<InetSocketAddress> convertAddresses(List<Address> addresses) {
        return addresses.stream().map(address -> new InetSocketAddress(address.getAddress(), address.getPort())).collect(Collectors.toList());
    }
}
