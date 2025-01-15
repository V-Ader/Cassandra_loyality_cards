package database.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
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
                .port(Integer.getInteger(properties.getProperty("cassandra.port")))
                .addresses(extractAddresses(properties.getProperty("cassandra.nodes")))
                .build();
    }

    static List<String> extractAddresses(String configValue) {
        List<String> nodeList = new ArrayList<>();
        for (String node : configValue.split(",")) {
            String[] parts = node.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid node format: " + node);
            }
            String host = parts[0].trim();
            nodeList.add(host);
        }
        return nodeList;
    }

    private CassandraConfigReader() {}
}
