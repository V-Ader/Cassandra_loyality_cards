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
                .port(Integer.getInteger(properties.getProperty("cassandra.port")))
                .addresses(extractAddresses(properties.getProperty("cassandra.nodes")))
                .build();
    }

    static List<String> extractAddresses(String configValue) {
        return new ArrayList<>(Arrays.asList(configValue.split(",")));
    }

    private CassandraConfigReader() {}
}
