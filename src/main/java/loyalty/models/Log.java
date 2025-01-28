package loyalty.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


@Builder
@Getter
@Setter
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

