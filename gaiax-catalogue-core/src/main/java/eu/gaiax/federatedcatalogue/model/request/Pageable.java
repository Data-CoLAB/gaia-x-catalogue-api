package eu.gaiax.federatedcatalogue.model.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public final class Pageable {
    private long page;
    private long size;
}
