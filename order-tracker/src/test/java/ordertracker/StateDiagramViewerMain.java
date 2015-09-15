package ordertracker;

import java.io.IOException;
import java.io.InputStream;

import com.github.davidmoten.xuml.StateDiagramViewer;

public class StateDiagramViewerMain {

    public static void main(String[] args) throws IOException {
        InputStream is = StateDiagramViewerMain.class.getResourceAsStream("/domains.xml");
        new StateDiagramViewer().start(is, "Ordering");
    }
}
