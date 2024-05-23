package eu.gaiax.federatedcatalogue.service.neo4j.participant;

import eu.gaiax.federatedcatalogue.credential.CredentialSubject;
import eu.gaiax.federatedcatalogue.credential.CredentialsSet;
import eu.gaiax.federatedcatalogue.entity.neo4j.Participant;
import eu.gaiax.federatedcatalogue.repository.neo4j.ParticipantRepository;
import eu.gaiax.federatedcatalogue.service.AbstractTypedIngestionService;
import eu.gaiax.federatedcatalogue.service.neo4j.model.participant.ParticipantDTO;
import eu.gaiax.federatedcatalogue.service.neo4j.model.participant.RegistrationNumberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static eu.gaiax.federatedcatalogue.utils.SelfDescriptionConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipantService implements AbstractTypedIngestionService {
    private final ParticipantRepository participantRepository;
    private final RegistrationNumberService registrationNumberService;
    private final AddressService addressService;
    private final TermsAndConditionService termsAndConditionService;

    public ParticipantDTO createParticipantDto(CredentialSubject cs, CredentialsSet credentials) {
        if (Objects.isNull(cs) || !cs.hasType(GX_LEGAL_PARTICIPANT)) {
            return null;
        }
        ParticipantDTO participantDto = new ParticipantDTO();
        participantDto.setCredentialSubjectId(cs.getId());
        participantDto.setIssuer(cs.getString("issuer"));
        participantDto.setLegalName(cs.getString(GX_LEGAL_NAME));
        participantDto.setHeadQuarterAddress(cs.getString(GX_HEADQUARTER_ADDRESS, GX_COUNTRY_SUBDIVISION_CODE));
        participantDto.setLegalAddress(cs.getString(GX_LEGAL_ADDRESS, GX_COUNTRY_SUBDIVISION_CODE));

        var lrnOpt = credentials.getBySubjectId(cs.getString(GX_REGISTRATION_NUMBER, ID));
        lrnOpt.ifPresent(lrn -> participantDto.setRegistrationNumbers(extractRegistrationNumbers(lrn)));
        this.create(participantDto);
        return participantDto;
    }

    private List<RegistrationNumberDto> extractRegistrationNumbers(CredentialSubject subject) {
        List<RegistrationNumberDto> lrnList = new ArrayList<>();
        for (String field : Set.of(GX_LEI_CODE, GX_VAT_ID, GX_TAX_ID, GX_EORI, GX_EUID)) {
            String value = subject.getString(field);
            if (!value.isEmpty()) {
                RegistrationNumberDto lrnDto = new RegistrationNumberDto(field, value);
                lrnList.add(lrnDto);
            }
        }
        return lrnList;
    }

    public Participant create(ParticipantDTO dto) {
        if (dto == null) {
            return null;
        }
        Participant participant = this.create(dto.getCredentialSubjectId(), dto.getIssuer(),
                dto.getLegalName(), dto.getRegistrationNumbers(),
                dto.getHeadQuarterAddress(), dto.getLegalAddress(),
                dto.getTncHash());
        for (ParticipantDTO organization : dto.getParentOrganizations()) {
            if (CollectionUtils.isEmpty(participant.getParentOrganization())) {
                participant.setParentOrganization(new HashSet<>());
            }
            participant.getParentOrganization().add(this.create(organization.getCredentialSubjectId(), organization.getIssuer(),
                    organization.getLegalName(), organization.getRegistrationNumbers(),
                    organization.getHeadQuarterAddress(), organization.getLegalAddress(),
                    organization.getTncHash()));
        }
        for (ParticipantDTO organization : dto.getSubOrganizations()) {
            if (CollectionUtils.isEmpty(participant.getSubOrganization())) {
                participant.setSubOrganization(new HashSet<>());
            }
            participant.getSubOrganization().add(this.create(organization.getCredentialSubjectId(), organization.getIssuer(),
                    organization.getLegalName(), organization.getRegistrationNumbers(),
                    organization.getHeadQuarterAddress(), organization.getLegalAddress(),
                    organization.getTncHash()));
        }
        return this.participantRepository.save(participant);
    }

    private Participant create(String credentialSubjectId, String did, String legalName, List<RegistrationNumberDto> registrationNumbers,
                               String headQuarterCountry, String legalAddressCountry,
                               String tncHash) {
        Participant participant = this.participantRepository.getByCredentialSubjectId(credentialSubjectId);
        UUID id = participant == null ? null : participant.getId();
        participant = Participant.builder()
                .credentialSubjectId(credentialSubjectId)
                .did(did)
                .registrationNumber(this.registrationNumberService.create(registrationNumbers))
                .headQuarterAddress(this.addressService.create(headQuarterCountry))
                .legalAddress(this.addressService.create(legalAddressCountry))
                .termsAndCondition(this.termsAndConditionService.create(tncHash))
                .build();
        participant.setId(id);
        participant.setName(legalName);
        participant.setCreatedAt(new Date());
        return this.participantRepository.save(participant);
    }

    @Override
    public void ingest(CredentialSubject credentialSubject, CredentialsSet credentials) {
        createParticipantDto(credentialSubject, credentials);
    }

    @Override
    public String getAcceptedType() {
        return GX_LEGAL_PARTICIPANT;
    }
}
