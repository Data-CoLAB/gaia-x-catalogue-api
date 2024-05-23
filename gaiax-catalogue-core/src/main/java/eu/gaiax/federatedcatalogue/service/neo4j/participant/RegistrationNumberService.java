package eu.gaiax.federatedcatalogue.service.neo4j.participant;

import eu.gaiax.federatedcatalogue.entity.neo4j.RegistrationNumber;
import eu.gaiax.federatedcatalogue.repository.neo4j.RegistrationNumberRepository;
import eu.gaiax.federatedcatalogue.service.neo4j.model.participant.RegistrationNumberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationNumberService {

    private final RegistrationNumberRepository registrationNumberRepository;

    public Set<RegistrationNumber> create(List<RegistrationNumberDto> registrationNumbers) {
        if (registrationNumbers == null) {
            return new HashSet<>();
        }
        return registrationNumbers.stream().map(r -> this.create(r.type(), r.number())).collect(Collectors.toSet());
    }

    public RegistrationNumber create(String type, String number) {
        return this.registrationNumberRepository.save(RegistrationNumber.builder()
                .type(type)
                .number(number)
                .build());
    }
}
