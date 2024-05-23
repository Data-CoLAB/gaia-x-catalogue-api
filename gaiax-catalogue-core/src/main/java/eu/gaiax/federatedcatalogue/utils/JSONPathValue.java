package eu.gaiax.federatedcatalogue.utils;

public record JSONPathValue(int arrayIndex, Object value, String... path) {
    public static final int NOT_IN_ARRAY = -1;

    public JSONPathValue(Object value, String... path) {
        this(NOT_IN_ARRAY, value, path);
    }

    public boolean isEmpty() {
        return value == null || value.toString().isBlank();
    }

    public boolean isInArray() {
        return arrayIndex != NOT_IN_ARRAY;
    }

    @Override
    public String toString() {
        return String.join(".", path) + "=" + value;
    }
}
