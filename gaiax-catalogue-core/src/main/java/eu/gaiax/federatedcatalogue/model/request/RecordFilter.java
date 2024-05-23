package eu.gaiax.federatedcatalogue.model.request;

import com.smartsensesolutions.java.commons.sort.Sort;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RecordFilter {
    @NotNull(message = "{NotNull.RecordFilter.page}")
    private int page;

    @NotNull(message = "{NotNull.RecordFilter.size}")
    @Min(value = 1)
    private int size;

    private List<Sort> sort;
    private QueryRequest query;

    private String prompt;
}
