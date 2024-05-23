package eu.gaiax.federatedcatalogue.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class QueryRequest {
    public String node;
    public String edge;
    public List<String> value;
    public String operator;
    public String key;
    public boolean neg;
    private List<Or> or;
    private List<And> and;
}
