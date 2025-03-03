package eu.gaiax.federatedcatalogue.service.neo4j.model.participant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class ParticipantDTO {
    private String credentialSubjectId;
    private String issuer;
    private String legalName;
    private String headQuarterAddress;
    private String legalAddress;
    private String tncHash;
    private List<RegistrationNumberDto> registrationNumbers;
    private Set<ParticipantDTO> parentOrganizations = new HashSet<>();
    private Set<ParticipantDTO> subOrganizations = new HashSet<>();
}
