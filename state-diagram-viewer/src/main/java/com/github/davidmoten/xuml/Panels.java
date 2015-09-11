package com.github.davidmoten.xuml;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public final class Panels {

    public static void saveImage(JPanel panel, File file) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            saveImage(panel, fos);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveImage(JPanel panel, OutputStream os) {
        BufferedImage bi = new BufferedImage(panel.getPreferredSize().width,
                panel.getPreferredSize().height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bi.createGraphics();
        panel.setDoubleBuffered(false);
        panel.paint(g2);
        panel.setDoubleBuffered(true);
        g2.dispose();
        try {
            ImageIO.write(bi, "png", os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void print(final JPanel panel) throws PrinterException {
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

}
