package eu.gaiax.federatedcatalogue.service.neo4j.participant;

import eu.gaiax.federatedcatalogue.entity.neo4j.TermsAndCondition;
import eu.gaiax.federatedcatalogue.repository.neo4j.TermsAndConditionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TermsAndConditionService {

    private final TermsAndConditionRepository termsAndConditionRepository;

    public TermsAndCondition create(String hash) {
        return this.termsAndConditionRepository.save(TermsAndCondition.builder()
                .hash(hash)
                .build());
    }
}
