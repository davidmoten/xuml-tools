package com.github.davidmoten.xuml;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import xuml.tools.miuml.metamodel.jaxb.Class;
import xuml.tools.miuml.metamodel.jaxb.ModeledDomain;
import xuml.tools.model.compiler.Util;

public class StateDiagramViewer {

    private final JFrame frame = new JFrame();
    private final JPanel vvContainer = createVvContainer();
    private volatile JMenu classMenu;

    private final static ThreadLocal<DrawingState> state = new ThreadLocal<>();

    private void open(ModeledDomain domain) {
        if (classMenu == null)
            throw new RuntimeException("must call start() before calling open(domain)");
        classMenu.removeAll();
        List<Class> classes = Util.getClasses(domain);
        classes.stream().forEach(cls -> {
            JMenuItem m = new JMenuItem(cls.getName());
            m.setEnabled(cls.getLifecycle() != null);
            m.addActionListener(evt -> show(cls));
            classMenu.add(m);
        });
        // show the first class with a lifecyle
        classes.stream().filter(cls -> cls.getLifecycle() != null).limit(1)
                .forEach(cls -> show(cls));
    }

    public void show(Class c) {
        System.out.println("showing " + c.getName());
        Graph<String, Edge> g = new DirectedSparseGraph<String, Edge>();
        if (c.getLifecycle() != null) {
            c.getLifecycle().getState().stream().forEach(state -> g.addVertex(state.getName()));
            c.getLifecycle().getTransition().stream().forEach(transition -> {
                String fromState = transition.getState();
                String toState = transition.getDestination();
                String eventName = c.getLifecycle().getEvent().stream().map(ev -> ev.getValue())
                        .filter(event -> event.getID().equals(transition.getEventID()))
                        .map(event -> event.getName()).findAny().get();
                g.addEdge(new Edge(eventName), fromState, toState);
            });
        }
        if (!g.getVertices().isEmpty()) {
            VisualizationViewer<String, Edge> vv = createVisualizationViewer(g);
            EventQueue.invokeLater(() -> {
                vvContainer.removeAll();
                vvContainer.add(vv);
                vvContainer.setFocusable(true);
                vvContainer.repaint();
                vvContainer.revalidate();
                System.out.println("added vv");
                frame.setTitle(c.getName());
            });
        }
    }

    private void start() {
        EventQueue.invokeLater(() -> {
            // Creates a menubar for a JFrame
            JMenuBar menuBar = new JMenuBar();
            // Add the menubar to the frame
            frame.setJMenuBar(menuBar);
            // Define and add two drop down menu to the menubar
            JMenu fileMenu = new JMenu("File");
            classMenu = new JMenu("Class");
            JMenu editMenu = new JMenu("Edit");
            menuBar.add(fileMenu);
            menuBar.add(editMenu);
            menuBar.add(classMenu);
            JMenuItem openMenuItem = new JMenuItem("Open");
            JMenuItem printMenuItem = new JMenuItem("Print...");
            JMenuItem saveAsImageMenuItem = new JMenuItem("Save as PNG...");
            JMenuItem exitMenuItem = new JMenuItem("Exit");
            fileMenu.add(openMenuItem);
            fileMenu.add(printMenuItem);
            fileMenu.add(saveAsImageMenuItem);
            fileMenu.add(exitMenuItem);
            openMenuItem.addActionListener(System.out::println);
            exitMenuItem.addActionListener(e -> System.exit(0));
            printMenuItem.addActionListener(e -> print());
            saveAsImageMenuItem.addActionListener(e -> saveAsImage());

            frame.getContentPane().add(vvContainer);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(new Dimension(1000, 800));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

    }

    private void saveAsImage() {
        JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(new ImageFilter());
        int returnVal = fc.showSaveDialog(vvContainer);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if (!file.getName().toUpperCase().endsWith(".PNG"))
                file = new File(file.getAbsolutePath() + ".PNG");
            Color bg = vvContainer.getBackground();
            try {
                vvContainer.setPreferredSize(vvContainer.getSize());
                vvContainer.setBackground(Color.white);
                Panels.saveImage(vvContainer, file);
            } catch (RuntimeException e1) {
                e1.printStackTrace();
            } finally {
                vvContainer.setBackground(bg);
            }
        }
    }

    private void print() {
        Color bg = vvContainer.getBackground();
        try {
            vvContainer.setPreferredSize(vvContainer.getSize());
            vvContainer.setBackground(Color.white);
            Panels.print(vvContainer);
        } catch (PrinterException e1) {
            e1.printStackTrace();
        } finally {
            vvContainer.setBackground(bg);
        }
    }

    private static JPanel createVvContainer() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(800, 600));
        // panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(new GridLayout(0, 1));
        return panel;
    }

    public static final class Edge {
        final String name;

        Edge(String name) {
            this.name = name;
        }

        public static Edge of(String name) {
            return new Edge(name);
        }
    }

    private static class DrawingState {

    }

    private static VisualizationViewer<String, Edge> createVisualizationViewer(
            Graph<String, Edge> graph) {
        FRLayout<String, Edge> layout = new FRLayout<String, Edge>(graph, new Dimension(800, 600));
        while (!layout.done())
            layout.step();

        VisualizationViewer<String, Edge> vv = new VisualizationViewer<String, Edge>(layout,
                new Dimension(800, 600)) {

            private static final long serialVersionUID = -3546358007558213875L;

            @Override
            public void paintComponents(Graphics g) {
                try {
                    state.set(new DrawingState());
                    super.paintComponents(g);
                } finally {
                    state.remove();
                }
            }

        };
        vv.setBackground(Color.white);
        vv.getRenderContext().setVertexLabelTransformer(s -> s);
        vv.getRenderContext().setVertexFillPaintTransformer(vertex -> vertex.equals("Created")
                ? Color.decode("#B5D9E6") : Color.decode("#FFF1BC"));
        vv.getRenderContext().setVertexDrawPaintTransformer(vertex -> Color.black);
        vv.getRenderContext().setVertexShapeTransformer(createVertexShapeTransformer(layout));
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        vv.getRenderContext().setEdgeLabelTransformer(edge -> " " + edge.name + " ");
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.QuadCurve<String, Edge>());
        vv.getRenderer().setEdgeLabelRenderer(new MyEdgeLabelRenderer<>(-10, Color.white));

        // The following code adds capability for mouse picking of
        // vertices/edges. Vertices can even be moved!
        final DefaultModalGraphMouse<String, Number> graphMouse = new DefaultModalGraphMouse<String, Number>();
        vv.setGraphMouse(graphMouse);
        graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
        return vv;
    }

    private static Transformer<String, Shape> createVertexShapeTransformer(
            Layout<String, Edge> layout) {
        return vertex -> {
            int w = 150;
            int margin = 5;
            int ascent = 12;
            int descent = 5;
            Shape shape = new Rectangle(-w / 2 - margin, -ascent - margin, w + 2 * margin,
                    (ascent + descent) + 2 * margin);
            return shape;
        };
    }

    private static class ImageFilter extends FileFilter {

        // Accept all directories and all gif, jpg, tiff, or png files.
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            return f.getName().toUpperCase().endsWith(".PNG");
        }

        // The description of this filter
        @Override
        public String getDescription() {
            return "PNG files";
        }
    }

    public void start(String resource, String domainName) {
        start(StateDiagramViewer.class.getResourceAsStream(resource), domainName);
    }

    public void start(InputStream input, String domainName) {
        try (InputStream is = input) {
            start();
            ModeledDomain domain = Util.getModeledDomain(is, domainName);
            open(domain);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}