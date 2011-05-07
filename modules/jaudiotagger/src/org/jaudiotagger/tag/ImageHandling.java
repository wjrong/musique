package org.jaudiotagger.tag;

import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.id3.valuepair.ImageFormats;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * User: paul
 * Date: 11-Dec-2008
 */
public class ImageHandling {
    /**
     * Resize the image until the total size require to store the image is less than maxsize
     *
     * @param artwork
     * @param maxSize
     * @throws IOException
     */
    public static void reduceQuality(Artwork artwork, int maxSize) throws IOException {
        while (artwork.getBinaryData().length > maxSize) {
            Image srcImage = artwork.getImage();
            int w = srcImage.getWidth(null);
            int newSize = w / 2;
            makeSmaller(artwork, newSize);
        }
    }

    /**
     * Resize image using Java 2D
     *
     * @param artwork
     * @param size
     * @throws java.io.IOException
     */
    private static void makeSmaller(Artwork artwork, int size) throws IOException {
        Image srcImage = artwork.getImage();

        int w = srcImage.getWidth(null);
        int h = srcImage.getHeight(null);

        // Determine the scaling required to get desired result.
        float scaleW = (float) size / (float) w;
        float scaleH = (float) size / (float) h;

        //Create an image buffer in which to paint on, create as an opaque Rgb type image, it doesnt matter what type
        //the original image is we want to convert to the best type for displaying on screen regardless
        BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);

        // Set the scale.
        AffineTransform tx = new AffineTransform();
        tx.scale(scaleW, scaleH);

        // Paint image.
        Graphics2D g2d = bi.createGraphics();
        g2d.drawImage(srcImage, tx, null);
        g2d.dispose();


        if (artwork.getMimeType() != null && isMimeTypeWritable(artwork.getMimeType())) {
            artwork.setBinaryData(writeImage(bi, artwork.getMimeType()));
        } else {
            artwork.setBinaryData(writeImageAsPng(bi));
        }
    }

    public static boolean isMimeTypeWritable(String mimeType) {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(mimeType);
        return writers.hasNext();
    }

    /**
     * Write buffered image as required format
     *
     * @param bi
     * @param mimeType
     * @return
     * @throws IOException
     */
    public static byte[] writeImage(BufferedImage bi, String mimeType) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(mimeType);
        if (writers.hasNext()) {
            ImageWriter writer = writers.next();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            writer.setOutput(ImageIO.createImageOutputStream(baos));
            writer.write(bi);
            return baos.toByteArray();
        }
        throw new IOException("Cannot write to this mimetype");
    }

    /**
     * @param bi
     * @return
     * @throws IOException
     */
    public static byte[] writeImageAsPng(BufferedImage bi) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, ImageFormats.MIME_TYPE_PNG, baos);
        return baos.toByteArray();
    }

    /**
     * Show read formats
     * <p/>
     * On Windows supports png/jpeg/bmp/gif
     */
    public static void showReadFormats() {
        String[] formats = ImageIO.getReaderMIMETypes();
        for (String f : formats) {
            System.out.println("r" + f);
        }
    }

    /**
     * Show write formats
     * <p/>
     * On Windows supports png/jpeg/bmp
     */
    public static void showWriteFormats() {
        String[] formats = ImageIO.getWriterMIMETypes();
        for (String f : formats) {
            System.out.println(f);
        }
    }

}
