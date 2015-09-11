package ordertracker;

import java.io.IOException;
import java.io.InputStream;

import com.github.davidmoten.xuml.StateDiagramViewer;

import xuml.tools.model.compiler.Util;

public class StateDiagramViewerMain {

    public static void main(String[] args) throws IOException {
        // StateDiagramViewer.show(createTestGraph());

        try (InputStream is = StateDiagramViewerMain.class.getResourceAsStream("/domains.xml")) {
            Util.getClasses(Util.getModeledDomain(is, "Ordering")).stream().forEach(c -> {
                System.out.println(c.getName());
                StateDiagramViewer.show(c);
            });
        }
    }
}
