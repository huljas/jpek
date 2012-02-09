package pragmatastic.jpek;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * @author huljas
 */
public class JpekDecoder {

    public static final int DQT = 0xFFDB; /* Define Quantization Table */
    public static final int SOF = 0xFFC0; /* Start of Frame (size information) */
    public static final int DHT = 0xFFC4; /* Huffman Table */
    public static final int SOI = 0xFFD8; /* Start of Image */
    public static final int SOS = 0xFFDA; /* Start of Scan */
    public static final int RST = 0xFFD0; /* Reset Marker d0 -> .. */
    public static final int RST7 = 0xFFD7; /* Reset Marker .. -> d7 */
    public static final int EOI = 0xD9; /* End of Image */
    public static final int DRI = 0xFFDD; /* Define Restart Interval */
    public static final int APP0 = 0xFFE0;
    public static final int APP16 = 0xFFEF;

    public static final String JFIF_MARKER = "JFIF\u0000";

    public static final int[] zigzag = {
            0, 1, 5, 6, 14, 15, 27, 28,
            2, 4, 7, 13, 16, 26, 29, 42,
            3, 8, 12, 17, 25, 30, 41, 43,
            9, 11, 18, 24, 31, 40, 44, 53,
            10, 19, 23, 32, 39, 45, 52, 54,
            20, 22, 33, 38, 46, 51, 55, 60,
            21, 34, 37, 47, 50, 56, 59, 61,
            35, 36, 48, 49, 57, 58, 62, 63
    };
    public static final int QTABLE_SIZE = 64;

    public static void debug(String message, Object ... args) {
        System.out.println(String.format(message, args));
    }

    public static JpekImage decode(byte[] rawImage) {
        JpekImage jpekImage = new JpekImage();
        ByteBuffer buffer = ByteBuffer.wrap(rawImage);
        for (int i = 0; buffer.position() <= buffer.capacity() - 2; i++) {
            int marker = 0xffff & buffer.getShort();
            debug("*** Marker: %x, Position: %x ***", marker, buffer.position() - 2);
            for (JpekSegmentDecoder decoder : JpekSegmentDecoder.DECODERS) {
                if (decoder.accept(marker)) {
                    decoder.decode(buffer, jpekImage);
                    break;
                }
            }
        }
        return jpekImage;
    }

    public static void main(String[] args) throws IOException {
        JpekImage image = decode(IOUtils.toByteArray(new FileInputStream("simple0.jpg")));
        System.out.println("DHTTrees: " + image.dhtTrees);
        System.out.println("QTables: " + image.qTables);
        System.out.println("scandata: " + image.scanData);
        image.process();
    }
}
