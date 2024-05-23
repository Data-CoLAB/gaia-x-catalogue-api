package eu.gaiax.federatedcatalogue.service.registry;

import eu.gaiax.federatedcatalogue.utils.JSONWrapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

import static eu.gaiax.federatedcatalogue.utils.JSONWrapper.add;

@Getter
public class OldTrustFrameworkShapes {
    private final List<JSONWrapper> graph = new ArrayList<>();
    private final Map<String, Shape> shapes;

    public OldTrustFrameworkShapes(JSONObject json) {
        json.getJSONArray("@graph").forEach(o -> graph.add(new JSONWrapper((JSONObject) o)));
        shapes = new ShapeReader(graph).read();
    }

    public boolean isMandatory(String type, String... path) {
        return findProperty(type, path).getMinCount() > 0;
    }

    public Optional<Shape> getShape(String shape) {
        return Optional.ofNullable(shapes.get(shape));
    }

    public Property findProperty(String shape, String... path) {
        //TODO
        return null;
    }

    public record Shape(Map<String, Property> properties) {
    }

    public record Property(JSONWrapper value) {

        public Property() {
            this(new JSONWrapper());
        }

        public int getMinCount() {
            return value.getNumber("sh:minCount", 0).intValue();
        }
    }

    @RequiredArgsConstructor
    private static class ShapeReader {
        private final List<JSONWrapper> graph;
        private final Map<String, Shape> shapes = new HashMap<>();

        public Shape useShape(String name) {
            return shapes.computeIfAbsent(name, x -> {
                var s = new Shape(new HashMap<>());
                shapes.put(name, s);
                return s;
            });
        }

        private Map<String, Shape> read() {
            for (JSONWrapper jsonShape : graph) {
                Shape shape = new Shape(new HashMap<>());
                jsonShape.getArray("sh:targetClass").forEach(child -> {
                    if (child instanceof JSONObject o) {
                        shapes.put(o.getString("@id"), shape);
                    }
                });
                jsonShape.getOptString("@id").ifPresent(id -> shapes.put(id, shape));
                var props = jsonShape.getArray("sh:property");
                props.forEach(p -> {
                    if (p instanceof JSONObject jsonProp) {
                        JSONWrapper prop = new JSONWrapper(jsonProp);
                        prop.getOptString("sh:path")
                                .ifPresent(path -> shape.properties.put(path, new Property(prop)));
                    }
                });
            }

            return shapes;
        }

        private void readProperties(Shape shape, JSONArray properties, String... fullPath) {
            properties.forEach(p -> {
                if (p instanceof JSONObject o) {
                    readProperty(shape, new JSONWrapper(o), fullPath);
                }
            });
        }

        private void readProperty(Shape shape, JSONWrapper json, String... fullPath) {
            json.getOptString("sh:path", "@id").ifPresent(path -> {
                //shape.addProperty(new Property(json), add(fullPath, path));
            });
            //json.getOptString("sh:node", "@id")
            //        .ifPresent(node -> readProperty(useShape(node)));
        }
    }

}
