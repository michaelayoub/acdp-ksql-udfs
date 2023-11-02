package io.ayoub.acdp.neighbor;

import io.ayoub.acdp.model.Ace3DDistance;
import io.ayoub.acdp.model.AcePositionWithName;
import io.github.zabuzard.closy.external.Metric;
import io.github.zabuzard.closy.external.NearestNeighborComputation;
import io.github.zabuzard.closy.external.NearestNeighborComputations;

import java.util.List;

public class NeighborSearch {
    private final Metric<AcePositionWithName> metric = new Ace3DDistance();
    private final NearestNeighborComputation<AcePositionWithName> algo = NearestNeighborComputations.of(metric);
    public NeighborSearch(List<AcePositionWithName> positions) {
        positions.forEach(algo::add);
    }

    public NearestNeighborComputation<AcePositionWithName> getAlgo() { return algo; }
}
