package eu.gaiax.federatedcatalogue.model.response;

import eu.gaiax.federatedcatalogue.entity.neo4j.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class ServiceDetailResponse {

    private UUID id;

    private String credentialSubjectId;

    private String name;

    private String description;

    private String labelLevel;

    private Set<DataProtectionRegime> protectionRegime = new HashSet<>();

    private Set<Address> locations = new HashSet<>();

    private Set<AggregateAndDependantDto> dependedServices = new HashSet<>();

    private Set<AggregateAndDependantDto> resources = new HashSet<>();

    private Double veracity;

    private Double transparency;

    private Double trustIndex;

    private DataAccountExport dataAccountExport;

    private String tnCUrl;

    private ServiceProviderDto participant;

}
