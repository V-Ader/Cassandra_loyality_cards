package loyalty_benchamrk;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class BenchmarkResult {
    public int executions;
    public int accepted;
    public int rejected;
    public long duration;

    BenchmarkResult() {
        executions = 0;
        accepted = 0;
        rejected = 0;
        duration = 0;
    }

    synchronized void updateResults(boolean result) {
        executions += 1;
        if (result) {
            accepted += 1;
        } else {
            rejected += 1;
        }
    }

    synchronized void merge(BenchmarkResult result) {
        executions += result.executions;
        accepted += result.accepted;
        rejected += result.rejected;
        duration += result.duration;
    }

    @Override
    public String toString() {
        return "executions:" + this.executions + '\n' +
                "accepted:" + this.accepted + '\n' +
                "rejected:" + this.rejected + '\n' +
                "duration:" + this.duration + '\n';
    }


}
