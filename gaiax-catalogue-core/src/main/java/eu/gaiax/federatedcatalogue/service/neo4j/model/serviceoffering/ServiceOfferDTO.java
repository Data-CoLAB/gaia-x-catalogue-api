package eu.gaiax.federatedcatalogue.service.neo4j.model.serviceoffering;

import eu.gaiax.federatedcatalogue.entity.neo4j.Address;
import eu.gaiax.federatedcatalogue.service.neo4j.model.participant.ParticipantDTO;
import eu.gaiax.federatedcatalogue.service.neo4j.model.resource.ResourceDTO;
import eu.gaiax.federatedcatalogue.trust.TrustIndex;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class ServiceOfferDTO {

    private String credentialSubjectId;
    private String name;
    private String description;
    private TermsAndConditionDTO tnc;
    private DataAccountExportDTO dataAccountExport;
    private Set<String> dataProtectionRegime = new HashSet<>();
    private ParticipantDTO providedBy;
    private Set<String> policy = new HashSet<>();
    private Set<ServiceOfferDTO> dependsOn = new HashSet<>();
    private Set<ResourceDTO> aggregationOf = new HashSet<>();
    private Set<Address> locations = new HashSet<>();

    private TrustIndex trust;

    private String labelLevel;
}
