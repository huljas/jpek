package pragmatastic.jpek;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * @author huljas
 */
public class JpekDecoder {

    public static final int DQT = 0xDB; /* Define Quantization Table */
    public static final int SOF = 0xC0; /* Start of Frame (size information) */
    public static final int DHT = 0xC4; /* Huffman Table */
    public static final int SOI = 0xD8; /* Start of Image */
    public static final int SOS = 0xDA; /* Start of Scan */
    public static final int RST = 0xD0; /* Reset Marker d0 -> .. */
    public static final int RST7 = 0xD7; /* Reset Marker .. -> d7 */
    public static final int EOI = 0xD9; /* End of Image */
    public static final int DRI = 0xDD; /* Define Restart Interval */
    public static final int APP0 = 0xE0;
    public static final int APP16 = 0xEF;

    public static final String JFIF_MARKER = "JFIF\u0000";

    private static final int[] zigzag = {
            0, 1, 5, 6, 14, 15, 27, 28,
            2, 4, 7, 13, 16, 26, 29, 42,
            3, 8, 12, 17, 25, 30, 41, 43,
            9, 11, 18, 24, 31, 40, 44, 53,
            10, 19, 23, 32, 39, 45, 52, 54,
            20, 22, 33, 38, 46, 51, 55, 60,
            21, 34, 37, 47, 50, 56, 59, 61,
            35, 36, 48, 49, 57, 58, 62, 63
    };
    private static final int QTABLE_SIZE = 64;

    private static Map<Integer, List<Integer>> qTables = new HashMap<Integer, List<Integer>>();


    public static void decode(byte[] rawImage) {
        ByteBuffer buffer = ByteBuffer.wrap(rawImage);
        System.out.println("Decoding jpeg from buffer " + buffer);
        for (int i = 0; buffer.position() < buffer.capacity(); i++) {
            int b = 0xff & buffer.get();
            if (b == 0xff) {
                int header = 0xff & buffer.get();
                switch (header) {
                    case DQT: {
                        readDQT(buffer);
                        break;
                    }
                    case SOF: {
                        readSOF(buffer);
                        break;
                    }
                    case DHT: {
                        readDHT(buffer);
                        break;
                    }
                    case SOI: {
                        readSOI(buffer);
                        break;
                    }
                    case SOS: {
                        readSOS(buffer);
                        break;
                    }
                    case RST: {
                        readRST(buffer);
                        break;
                    }
                    case RST7: {
                        readRST7(buffer);
                        break;
                    }
                    case EOI: {
                        readEOI(buffer);
                        break;
                    }
                    case DRI: {
                        readDRI(buffer);
                        break;
                    }
                }
                if (header >= APP0 && header <= APP16) {
                    readAPP(header, buffer);
                }
            }
        }


    }


    private static void readSOI(ByteBuffer buffer) {
        System.out.println("SOI at " + buffer.position());
    }

    private static void readSOS(ByteBuffer buffer) {
        System.out.println("SOS at " + buffer.position());
    }

    private static void readRST(ByteBuffer buffer) {
        System.out.println("RST at " + buffer.position());
    }

    private static void readRST7(ByteBuffer buffer) {
        System.out.println("RST7 at " + buffer.position());
    }

    private static void readEOI(ByteBuffer buffer) {
        System.out.println("EOI at " + buffer.position());
    }

    private static void readDRI(ByteBuffer buffer) {
        System.out.println("DRI at " + buffer.position());
    }

    private static void readAPP(int header, ByteBuffer buffer) {
        System.out.println("*** Marker: APP" + (header - APP0) + " ***");
        int length = 0xffff & buffer.getShort();
        System.out.println(String.format("  length: %1$d", length));

        byte[] bytes = new byte[length - 2];
        buffer.get(bytes);
        buffer = ByteBuffer.wrap(bytes);

        byte[] ba = new byte[5];
        buffer.get(ba);
        String id = new String(ba);
        System.out.println(String.format("  identifier: [%1$s]", id));
        if (id.equals(JFIF_MARKER)) {
            int major = 0xff & buffer.get();
            int minor = 0xff & buffer.get();
            int units = 0xff & buffer.get();
            int xDensity = 0xffff & buffer.getShort();
            int yDensity = 0xffff & buffer.getShort();
            int xThumb = 0xff & buffer.get();
            int yThumb = 0xff & buffer.get();
            System.out.println(String.format("  version = [%1$d.%2$d]", major, minor));
            System.out.println(String.format("  density = %1$d x %2$d", xDensity, yDensity));
            System.out.println(String.format("  thumbnail = %1$d x %2$d", xThumb, yThumb));
        }
    }

    private static void readSOF(ByteBuffer buffer) {
        System.out.println("SOF at " + buffer.position());
    }

    private static void readDQT(ByteBuffer buffer) {
        System.out.println("DQT at " + buffer.position());

        int length = 0xffff & buffer.getShort();
        buffer = wrap(buffer, length-2);
        byte bits = buffer.get();
        int tableId = bits & 0xf;
        int precision = bits >>> 4;
        List<Integer> data = new ArrayList<Integer>(64);
        for (int i = 0; i < QTABLE_SIZE; i++) {
            data.add(buffer.get(buffer.position() + zigzag[i]) & 0xff);
        }
        System.out.println("DQT length " + length);
        System.out.println("DQT tableId " + tableId);
        System.out.println("DQT precision " + precision);
        System.out.println("DQT table " + data);
        qTables.put(tableId, data);
    }

    private static ByteBuffer wrap(ByteBuffer buffer, int length) {
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return ByteBuffer.wrap(bytes);
    }


    private static void readDHT(ByteBuffer buffer) {
        System.out.println("DHT at " + buffer.position());
//u8	0xff
//u8	0xc4 (type of segment)
        int length = 0xffff & buffer.getShort();
        System.out.println("length " + length);
        byte bits = buffer.get();
        int classB = bits & 0xf;
        int tableId = bits >>> 4;
        System.out.println("class " + classB);
        System.out.println("tableId " + tableId);
        List<Integer> depthCounts = new ArrayList<Integer>();
        int countTotal = 0;
        for (int i = 0; i < 16; i++) {
            int count = buffer.get() & 0xff;
            depthCounts.add(count);
            countTotal += count;
        }
        int remaining = length - 1 - 16;
        if (countTotal > remaining) {
            throw new IllegalStateException("Count total > remaining bytes " + countTotal + " > " + remaining);
        }

        for (int i = 0; i < depthCounts.size(); i++) {
            int count = depthCounts.get(i);
            System.out.println("  Codes of length " + (i+1) + ": " + count);
        }
        List<List<Integer>> tree = new ArrayList<List<Integer>>();
        for (int i = 0; i < depthCounts.size(); i++) {
            int count = depthCounts.get(i);
            List<Integer> level = new ArrayList<Integer>();
            tree.add(level);
            for (int j = 0; j < count; j++) {
                level.add(buffer.get() & 0xff);
            }
        }
        System.out.println(tree);
//u16 be	length of segment
//4-bitss	class (0 is DC, 1 is AC, more on this later)
//4-bits	table id
//array of 16 u8	number of elements for each of 16 depths
//array of u8	elements, in order of depth

    }

    public static void main(String[] args) throws IOException {
        decode(IOUtils.toByteArray(new FileInputStream("simple0.jpg")));
    }
}
