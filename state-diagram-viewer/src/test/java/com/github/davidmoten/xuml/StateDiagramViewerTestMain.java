package com.github.davidmoten.xuml;

import static com.github.davidmoten.xuml.StateDiagramViewer.Edge.of;

import java.io.IOException;
import java.io.InputStream;

import com.github.davidmoten.xuml.StateDiagramViewer.Edge;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import xuml.tools.miuml.metamodel.jaxb.ModeledDomain;
import xuml.tools.model.compiler.Util;

public class StateDiagramViewerTestMain {

    public static void main(String[] args) throws IOException {
        // StateDiagramViewer.show(createTestGraph());
        StateDiagramViewer viewer = new StateDiagramViewer();
        try (InputStream is = StateDiagramViewerTestMain.class
                .getResourceAsStream("/samples.xml")) {
            viewer.start();
            ModeledDomain domain = Util.getModeledDomain(is, "Bookstore");
            viewer.open(domain);
            Util.getClasses(domain).stream().filter(cls -> cls.getName().equals("Order"))
                    .forEach(c -> viewer.show(c));
        }
    }

    private static Graph<String, Edge> createTestGraph() {
        Graph<String, Edge> g = new DirectedSparseGraph<String, Edge>();
        g.addVertex("Created");
        g.addVertex("Inside");
        g.addVertex("Entered");
        g.addVertex("Never Outside");
        g.addVertex("Outside");
        g.addEdge(of("In"), "Created", "Never Outside", EdgeType.DIRECTED);
        g.addEdge(of("In"), "Entered", "Inside", EdgeType.DIRECTED);
        g.addEdge(of("In"), "Inside", "Inside", EdgeType.DIRECTED);
        g.addEdge(of("In"), "Never Outside", "Never Outside", EdgeType.DIRECTED);
        g.addEdge(of("In"), "Outside", "Entered", EdgeType.DIRECTED);
        g.addEdge(of("Out"), "Created", "Outside", EdgeType.DIRECTED);
        g.addEdge(of("Out"), "Entered", "Outside", EdgeType.DIRECTED);
        g.addEdge(of("Out"), "Inside", "Outside", EdgeType.DIRECTED);
        g.addEdge(of("Out"), "Never Outside", "Outside", EdgeType.DIRECTED);
        g.addEdge(of("Out"), "Outside", "Outside", EdgeType.DIRECTED);
        return g;
    }

}
