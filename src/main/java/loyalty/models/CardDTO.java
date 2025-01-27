package loyalty.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardDTO {
    private String issuerEmail;
    private String clientEmail;
    private String status;
    private long tokens;

    @Override
    public String toString() {
        return "CarDTOd{" +
                "issuerEmail='" + issuerEmail + '\'' +
                ", clientEmail='" + clientEmail + '\'' +
                ", status=" + status + '\n' +
                ", tokens=" + tokens +
                '}';
    }
}
