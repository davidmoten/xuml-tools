package com.github.davidmoten.xuml;

import java.io.IOException;
import java.io.InputStream;

public class StateDiagramViewerTestMain {

    public static void main(String[] args) throws IOException {
        new StateDiagramViewer().start(openInputStream(), "detection-domain");
    }

    private static InputStream openInputStream() {
        return StateDiagramViewerTestMain.class.getResourceAsStream("/detection.xml");
    }

}