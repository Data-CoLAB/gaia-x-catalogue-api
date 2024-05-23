package eu.gaiax.federatedcatalogue.model.response;

import eu.gaiax.federatedcatalogue.entity.neo4j.Address;
import eu.gaiax.federatedcatalogue.entity.neo4j.DataAccountExport;
import eu.gaiax.federatedcatalogue.entity.neo4j.DataProtectionRegime;
import eu.gaiax.federatedcatalogue.entity.neo4j.ServiceOffer;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@Setter
@AllArgsConstructor
public class ServiceListResponse {
    private UUID id;
    private String credentialSubjectId;
    private String name;
    private Set<String> policy = new HashSet<>();
    private DataAccountExport dataAccountExport;
    private Set<DataProtectionRegime> protectionRegime = new HashSet<>();
    private String providedBy;
    private String labelLevel;
    private Set<Address> locations = new HashSet<>();
    private Double veracity;
    private Double transparency;
    private Double trustIndex;
    private Date createdAt;
}
