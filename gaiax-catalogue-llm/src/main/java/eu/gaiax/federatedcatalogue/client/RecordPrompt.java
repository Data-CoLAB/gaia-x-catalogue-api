package eu.gaiax.federatedcatalogue.client;

import eu.gaiax.federatedcatalogue.model.request.RecordFilter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecordPrompt extends RecordFilter {
    private String prompt;
}
