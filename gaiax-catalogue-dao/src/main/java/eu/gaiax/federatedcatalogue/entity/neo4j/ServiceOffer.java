package eu.gaiax.federatedcatalogue.entity.neo4j;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static eu.gaiax.federatedcatalogue.entityhelper.relation.Neo4jNodeRelationConstant.*;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Node(labels = N_SERVICE_OFFERING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceOffer extends BaseEntity {
    private String credentialSubjectId;
    private String vc;
    private String description;
    private Set<String> policy = new HashSet<>();
    private String labelLevel;
    @Relationship(value = DATA_ACCOUNT_EXPORT, direction = OUTGOING)
    private DataAccountExport dataAccountExport;
    @Relationship(value = TERMS_AND_CONDITIONS, direction = OUTGOING)
    private TermsAndCondition termsAndCondition;
    @Relationship(value = PROVIDED_BY, direction = OUTGOING)
    private Participant participant;
    @Relationship(value = DATA_PROTECTION_REGIME, direction = OUTGOING)
    private Set<DataProtectionRegime> protectionRegime = new HashSet<>();
    @Relationship(value = AGGREGATION_OF, direction = OUTGOING)
    private Set<Resource> resources = new HashSet<>();
    @Relationship(value = DEPENDS_ON, direction = OUTGOING)
    private Set<ServiceOffer> dependedServices = new HashSet<>();
    @Relationship(value = LOCATED_IN, direction = OUTGOING)
    private Set<Address> locations = new HashSet<>();
    private Date createdDate;
    private Double veracity;
    private Double transparency;
    private Double trustIndex;

    public String getTnCUrl() {
        return this.termsAndCondition != null ? this.termsAndCondition.getUrl() : null;
    }
}
