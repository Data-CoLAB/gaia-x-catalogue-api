package eu.gaiax.federatedcatalogue.service.neo4j.participant;

import eu.gaiax.federatedcatalogue.entity.neo4j.Address;
import eu.gaiax.federatedcatalogue.repository.neo4j.AddressRepository;
import eu.gaiax.federatedcatalogue.service.neo4j.model.resource.LocationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {
    private final AddressRepository addressRepository;

    public Address create(LocationDTO dto) {
        return this.create(dto.countrySubdivisionCode());
    }

    public Address create(String countrySubdivisionCode) {
        Address address = this.addressRepository.getByName(countrySubdivisionCode);
        if (Objects.nonNull(address)) {
            return address;
        }
        address = Address.builder()
                .countrySubdivisionCode(countrySubdivisionCode)
                .build();
        address.setName(countrySubdivisionCode);
        return this.addressRepository.save(address);
    }
}
