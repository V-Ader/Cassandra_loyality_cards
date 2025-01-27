package loyalty.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CardId {
    private String issuerEmail;
    private String clientEmail;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardId cardId = (CardId) o;
        return Objects.equals(issuerEmail, cardId.issuerEmail) &&
                Objects.equals(clientEmail, cardId.clientEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(issuerEmail, clientEmail);
    }
}
