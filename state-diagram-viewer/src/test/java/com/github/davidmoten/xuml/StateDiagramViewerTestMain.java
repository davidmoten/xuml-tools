package com.github.davidmoten.xuml;

import java.io.IOException;

public class StateDiagramViewerTestMain {

    public static void main(String[] args) throws IOException {
        new StateDiagramViewer().start("/detection.xml", "detection-domain");
    }

}