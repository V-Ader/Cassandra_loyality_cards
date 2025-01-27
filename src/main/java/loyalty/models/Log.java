package loyalty.models;

import lombok.Builder;

import java.time.Instant;


@Builder
public class Log {
    private String issuerEmail;
    private String clientEmail;
    private Instant changeTimestamp;
    private int previousValue;
    private int newValue;

    @Override
    public String toString() {
        return "Log{" +
                "issuerEmail='" + issuerEmail + '\'' +
                ", clientEmail='" + clientEmail + '\'' +
                ", changeTimestamp=" + changeTimestamp +
                ", previousValue=" + previousValue +
                ", newValue=" + newValue +
                "}\n";
    }
}

