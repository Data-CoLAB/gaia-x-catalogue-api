package eu.gaiax.federatedcatalogue.credential;

import eu.gaiax.federatedcatalogue.utils.JSONWrapper;
import eu.gaiax.federatedcatalogue.utils.SelfDescriptionConstant;
import lombok.Getter;
import org.json.JSONObject;

import java.util.Arrays;

@Getter
public abstract class Credential extends JSONWrapper {

    private final String id;

    protected Credential(JSONObject json) {
        super(json);
        this.id = SelfDescriptionConstant.getId(json);
    }

    protected Credential() {
        super();
        id = "";
    }

    public String getType() {
        return SelfDescriptionConstant.getType(json);
    }

    public boolean hasType(String type) {
        return getType().equals(type);
    }

    public boolean hasAnyType(String... types) {
        return Arrays.stream(types).anyMatch(this::hasType);
    }

    @Override
    public String toString() {
        return String.format("%s %s", getType(), id);
    }
}
