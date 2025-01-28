package loyalty.actors.watcher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import loyalty.models.CardId;
import loyalty.models.Log;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WatcherInspectionResult {
    private CardId cardId;
    private long invalidValue;
    private List<Log> reportedLogs;

    public void addLogs(List<Log> newLogs){
        this.reportedLogs.addAll(newLogs);
    }
}
