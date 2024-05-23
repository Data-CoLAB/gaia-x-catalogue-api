package eu.gaiax.federatedcatalogue.service.neo4j;

import eu.gaiax.federatedcatalogue.credential.CredentialsSet;
import eu.gaiax.federatedcatalogue.service.neo4j.model.participant.ParticipantDTO;
import eu.gaiax.federatedcatalogue.service.neo4j.model.resource.LocationDTO;
import eu.gaiax.federatedcatalogue.service.neo4j.model.serviceoffering.TermsAndConditionDTO;
import eu.gaiax.federatedcatalogue.service.neo4j.participant.ParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

import static eu.gaiax.federatedcatalogue.utils.SelfDescriptionConstant.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class JsonProcessorService {

    private final ParticipantService participantService;

    public void processParticipant(JSONArray participants, Set<ParticipantDTO> dto, CredentialsSet credentials) {
        if (Objects.isNull(participants)) {
            return;
        }
        for (Object p : participants) {
            String id = getId((JSONObject) p);
            var participant = credentials
                    .getBySubjectId(id)
                    .map(c -> participantService.createParticipantDto(c, credentials)).orElseGet(() -> {
                ParticipantDTO unverifiedParticipant = new ParticipantDTO();
                unverifiedParticipant.setCredentialSubjectId(id);
                return unverifiedParticipant;
            });
            dto.add(participant);
        }
    }

    public void processLocation(JSONArray locations, Set<LocationDTO> dto) {
        if (Objects.isNull(locations)) {
            return;
        }
        locations.forEach(l -> {
            JSONObject location = (JSONObject) l;
            dto.add(new LocationDTO(location.optString(GX_COUNTRY_CODE)));
        });
    }


    public void processString(JSONArray licenses, Set<String> dto) {
        if (Objects.isNull(licenses)) {
            return;
        }
        licenses.forEach(l -> dto.add((String) l));
    }

    public TermsAndConditionDTO processForTnc(JSONObject tnc) {
        return new TermsAndConditionDTO(tnc.optString(GX_URL), tnc.optString(GX_HASH));
    }

}
