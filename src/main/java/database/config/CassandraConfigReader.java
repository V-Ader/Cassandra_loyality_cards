package database.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class CassandraConfigReader {

    private static final String CONFIG_PATH = "src/main/resources/cassandra-config.properties";

    public static CassandraConfig getConfig() throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            properties.load(fis);
        }

        return CassandraConfig.builder()
                .keyspace(properties.getProperty("cassandra.keyspace"))
                .addresses(extractAddresses(properties.getProperty("cassandra.nodes")))
                .build();
    }

    static List<Address> extractAddresses(String configValue) {
        List<String> nodes = Arrays.asList(configValue.split(","));

        List<Address> addresses = new ArrayList<>();

        for (String node : nodes) {
            String[] parts = node.split(":");
            if (parts.length == 2) {
                String address = parts[0].trim();
                Integer port = Integer.parseInt(parts[1].trim());
                addresses.add(new Address(port, address));
            } else {
                throw new IllegalArgumentException("Invalid address format: " + node);
            }
        }

        return addresses;
    }

    private CassandraConfigReader() {}
}
