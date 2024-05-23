package eu.gaiax.federatedcatalogue.service.neo4j.model.serviceoffering;

import java.util.Set;

public record DataAccountExportDTO(String requestType, String accessType, Set<String> formatType) {
}
