package com.github.davidmoten.graph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.renderers.Renderer;

public class StateDiagram {
    public static void main(String[] args) {
        Graph<String, String> g = createTestGraph();
        FRLayout<String, String> layout = new FRLayout<String, String>(g, new Dimension(800, 600));
        while (!layout.done())
            layout.step();

        VisualizationViewer<String, String> vv = new VisualizationViewer<String, String>(layout,
                new Dimension(800, 600));
        vv.getRenderContext().setVertexLabelTransformer(s -> s);
        vv.getRenderContext().setVertexFillPaintTransformer(vertex -> vertex.equals("Created")
                ? Color.decode("#B5D9E6") : Color.decode("#FFF1BC"));
        vv.getRenderContext().setVertexDrawPaintTransformer(vertex -> Color.black);
        vv.getRenderContext().setVertexShapeTransformer(createVertexShapeTransformer(layout));
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        vv.getRenderContext().setEdgeLabelTransformer(s -> s.substring(0, s.length() - 1));
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.QuadCurve<String, String>());

        // The following code adds capability for mouse picking of
        // vertices/edges. Vertices can even be moved!
        final DefaultModalGraphMouse<String, Number> graphMouse = new DefaultModalGraphMouse<String, Number>();
        vv.setGraphMouse(graphMouse);
        graphMouse.setMode(ModalGraphMouse.Mode.PICKING);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            JPanel surround = new JPanel();
            surround.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            surround.add(vv);
            createMenu(vv);
            frame.getContentPane().add(surround);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.pack();
            frame.setVisible(true);
        });
    }

    private static Transformer<String, Shape> createVertexShapeTransformer(
            Layout<String, String> layout) {
        return vertex -> {
            int w = 100;
            int margin = 5;
            int ascent = 12;
            int descent = 5;
            Shape shape = new Rectangle(-w / 2 - margin, -ascent - margin, w + 2 * margin,
                    (ascent + descent) + 2 * margin);
            return shape;
        };
    }

    public static Graph<String, String> createTestGraph() {
        Graph<String, String> g = new DirectedSparseGraph<String, String>();
        g.addVertex("Created");
        g.addVertex("Inside");
        g.addVertex("Entered");
        g.addVertex("Never Outside");
        g.addVertex("Outside");
        g.addEdge("In1", "Created", "Never Outside", EdgeType.DIRECTED);
        g.addEdge("In2", "Entered", "Inside", EdgeType.DIRECTED);
        g.addEdge("In3", "Inside", "Inside", EdgeType.DIRECTED);
        g.addEdge("In4", "Never Outside", "Never Outside", EdgeType.DIRECTED);
        g.addEdge("In5", "Outside", "Entered", EdgeType.DIRECTED);
        g.addEdge("Out1", "Created", "Outside", EdgeType.DIRECTED);
        g.addEdge("Out2", "Entered", "Outside", EdgeType.DIRECTED);
        g.addEdge("Out3", "Inside", "Outside", EdgeType.DIRECTED);
        g.addEdge("Out4", "Never Outside", "Outside", EdgeType.DIRECTED);
        g.addEdge("Out5", "Outside", "Outside", EdgeType.DIRECTED);
        return g;
    }

    private static void print(final JPanel panel) throws PrinterException {
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setJobName("State Diagram");
        pj.setCopies(1);
        PageFormat format = pj.defaultPage();
        if (panel.getPreferredSize().getWidth() > panel.getPreferredSize().getHeight())
            format.setOrientation(PageFormat.LANDSCAPE);
        else
            format.setOrientation(PageFormat.PORTRAIT);

        pj.setPrintable(new Printable() {
            @Override
            public int print(Graphics pg, PageFormat pf, int pageNum) {
                if (pageNum > 0)
                    return Printable.NO_SUCH_PAGE;
                Graphics2D g2 = (Graphics2D) pg;
                double w;
                double h;
                if (pf.getOrientation() == PageFormat.LANDSCAPE) {
                    w = pf.getPaper().getImageableHeight();
                    h = pf.getPaper().getImageableWidth();
                } else {
                    w = pf.getPaper().getImageableWidth();
                    h = pf.getPaper().getImageableHeight();
                }
                double scalex = w / panel.getPreferredSize().getWidth();
                double scaley = h / panel.getPreferredSize().getHeight();
                double scale = Math.min(scalex, scaley);

                g2.translate(pf.getImageableX(), pf.getImageableY());
                g2.scale(scale, scale);
                panel.setDoubleBuffered(false);
                panel.paint(g2);
                panel.setDoubleBuffered(true);
                return Printable.PAGE_EXISTS;
            }
        });
        if (pj.printDialog() == false)
            return;
        pj.print();
    }

    public static void saveImageFromGui(JPanel panel, File file) throws IOException {
        FileOutputStream fos;
        fos = new FileOutputStream(file);
        saveImageFromGui(panel, fos);
        fos.close();
    }

    private static void saveImageFromGui(JPanel panel, OutputStream os) throws IOException {
        BufferedImage bi = new BufferedImage(panel.getPreferredSize().width,
                panel.getPreferredSize().height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bi.createGraphics();
        panel.setDoubleBuffered(false);
        panel.paint(g2);
        panel.setDoubleBuffered(true);
        g2.dispose();
        ImageIO.write(bi, "png", os);
    }

    private static void createMenu(JPanel panel) {
        final JPopupMenu menu = new JPopupMenu();
        JMenuItem m = new JMenuItem("Print...");
        menu.add(m);
        m.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    print(panel);
                } catch (PrinterException e1) {
                    e1.printStackTrace();
                }
            }
        });
        m = new JMenuItem("Save image as PNG...");
        menu.add(m);
        m.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.addChoosableFileFilter(new ImageFilter());
                int returnVal = fc.showSaveDialog(panel);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    if (!file.getName().toUpperCase().endsWith(".PNG"))
                        file = new File(file.getAbsolutePath() + ".PNG");
                    try {
                        saveImageFromGui(panel, file);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3)
                    menu.show((Component) e.getSource(), e.getX(), e.getY());
            }
        });
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

}