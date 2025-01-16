package loyalty.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class Card {
    private String issuerEmail;
    private String ownerEmail;
    private String status;

    @Override
    public String toString() {
        return "Card{" +
                "issuerEmail='" + issuerEmail + '\'' +
                ", ownerEmail='" + ownerEmail + '\'' +
                ", status=" + status +
                '}';
    }
}
