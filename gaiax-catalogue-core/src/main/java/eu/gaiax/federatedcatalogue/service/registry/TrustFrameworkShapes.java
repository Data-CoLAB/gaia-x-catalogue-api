package eu.gaiax.federatedcatalogue.service.registry;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.engine.constraint.MinCount;
import org.apache.jena.shacl.engine.constraint.ShNode;
import org.apache.jena.shacl.parser.PropertyShape;
import org.apache.jena.shacl.parser.Shape;
import org.apache.jena.sparql.path.P_Link;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Getter
public class TrustFrameworkShapes {
    private final Shapes shapes;

    public TrustFrameworkShapes(String shapesString) {
        Model shapesModel = ModelFactory.createDefaultModel();
        RDFDataMgr.read(shapesModel, new ByteArrayInputStream(shapesString.getBytes(StandardCharsets.UTF_8)), Lang.TURTLE);
        shapes = Shapes.parse(shapesModel);
    }

    public String expand(String prefixedName) {
        var expanded = shapes.getPrefixMap().expand(prefixedName);
        return expanded == null ? prefixedName : expanded;
    }

    public Optional<Shape> getShape(String type) {
        String expanded = expand(type);
        return shapes.getTargetShapes()
                .stream()
                .filter(s -> s.getTargets().stream().anyMatch(t -> t.getObject().hasURI(expanded)))
                .findAny();
    }

    public boolean isMandatory(String type, String... path) {
        var constraints = getShape(type)
                .flatMap(s -> findPropShape(s.getPropertyShapes(), path))
                .map(PropertyShape::getConstraints)
                .orElseGet(Collections::emptyList);
        return constraints
                .stream()
                .anyMatch(c -> c instanceof MinCount min && min.getMinCount() > 0);
    }

    public Optional<PropertyShape> findPropShape(Collection<PropertyShape> shapes, String... path) {
        if (path.length == 0) {
            return Optional.empty();
        }
        var pLink = new P_Link(new PathNode(expand(path[0])));
        for (var prop : shapes) {
            if (prop.getPath().equals(pLink)) {
                return findPropShape(prop, path);
            }
        }
        return Optional.empty();
    }

    public Optional<PropertyShape> findPropShape(PropertyShape prop, String... path) {
        var subPath = Stream
                .of(subArray(path, 1, path.length - 1))
                .filter(p -> !"@id".equals(p) && !"@type".equals(p))
                .toArray(String[]::new);
        if (subPath.length == 0) {
            return Optional.of(prop);
        }
        var children = new ArrayList<>(prop.getPropertyShapes());
        for (var constraint : prop.getConstraints()) {
            if (constraint instanceof ShNode shNode && shNode.getOther() != null) {
                children.addAll(shNode.getOther().getPropertyShapes());
            }
        }
        return findPropShape(children, subPath);
    }

    private static class PathNode extends Node_URI {
        public PathNode(String uri) {
            super(uri);
        }
    }

    private static String[] subArray(String[] arr, int startIndex, int endIndex) {
        int subarrayLength = endIndex - startIndex + 1;
        String[] subarray = new String[subarrayLength];
        System.arraycopy(arr, startIndex, subarray, 0, subarrayLength);
        return subarray;
    }

}
