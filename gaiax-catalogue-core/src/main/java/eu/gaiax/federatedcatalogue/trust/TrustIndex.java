package eu.gaiax.federatedcatalogue.trust;

import java.util.Map;

public record TrustIndex(Map<SubIndex, Double> subIndexes, double value) {
    public enum SubIndex {
        VERACITY, TRANSPARENCY, COMPOSABILITY, SEMANTIC_MATCH
    }
}
