package database.config;

import database.ConnectionException;

import java.io.IOException;

public class CassandraConfigService {

    private CassandraConfig config;
    private static CassandraConfigService instance;

    private CassandraConfigService() throws IOException {
        this.config = CassandraConfigReader.getConfig();
    }


    public static CassandraConfigService getInstance() throws ConnectionException {
        if (instance == null) {
           synchronized (CassandraConfigService.class) {
               try {
                   if (instance == null) {
                       instance = new CassandraConfigService();
                   }
               } catch (IOException e) {
                   throw new ConnectionException(e);
               }
           }
        }
        return instance;
    }

    public CassandraConfig getConfig() {
        return this.config;
    }
}
