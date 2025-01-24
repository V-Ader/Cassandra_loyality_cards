package loyalty.database.balancing_policies;

import com.datastax.oss.driver.api.core.context.DriverContext;
import com.datastax.oss.driver.api.core.loadbalancing.LoadBalancingPolicy;
import com.datastax.oss.driver.api.core.loadbalancing.NodeDistance;
import com.datastax.oss.driver.api.core.metadata.Node;
import com.datastax.oss.driver.api.core.metadata.NodeState;
import com.datastax.oss.driver.api.core.session.Request;
import com.datastax.oss.driver.api.core.session.Session;
import com.datastax.oss.driver.api.core.tracker.RequestTracker;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class RandomLoadBalancingPolicy implements LoadBalancingPolicy {
    private final List<Node> nodes = new ArrayList<>();

    public RandomLoadBalancingPolicy(DriverContext context, String profileName) {
        // Initialization if needed
    }

    @NonNull
    @Override
    public Optional<RequestTracker> getRequestTracker() {
        return LoadBalancingPolicy.super.getRequestTracker();
    }

    @Override
    public void init(Map<UUID, Node> nodes, DistanceReporter distanceReporter) {
        // Add all nodes that are UP to the list
        this.nodes.addAll(nodes.values().stream()
                .filter(node -> node.getState() == NodeState.UP)
                .collect(Collectors.toList()));

        // Report distance for each node
        this.nodes.forEach(node -> distanceReporter.setDistance(node, NodeDistance.LOCAL));
    }

    @NonNull
    @Override
    public Queue<Node> newQueryPlan(@Nullable Request request, @Nullable Session session) {
        // Randomly shuffle the nodes and return as a queue
        Collections.shuffle(this.nodes, ThreadLocalRandom.current());
        return new ArrayDeque<>(this.nodes);
    }

    @Override
    public void onAdd(Node node) {
        if (node.getState() == NodeState.UP) {
            nodes.add(node);
        }
    }

    @Override
    public void onUp(Node node) {
        if (!nodes.contains(node)) {
            nodes.add(node);
        }
    }

    @Override
    public void onDown(Node node) {
        nodes.remove(node);
    }

    @Override
    public void onRemove(Node node) {
        nodes.remove(node);
    }

    @Override
    public void close() {
        nodes.clear();
    }
}
