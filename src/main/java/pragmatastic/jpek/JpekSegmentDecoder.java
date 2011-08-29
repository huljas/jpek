package pragmatastic.jpek;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static pragmatastic.jpek.JpekDecoder.*;
/**
 * @author huljas
 */
public abstract class JpekSegmentDecoder {

    public static final JpekSegmentDecoder[] DECODERS = new JpekSegmentDecoder[]{
        new APP_DECODER(),
        new DQT_DECODER(),
        new DHT_DECODER(),
        new SOF_DECODER(),
        new SOS_DECODER(),
        new SOI_DECODER()
    };

    public abstract boolean accept(int marker);

    public abstract void decode(ByteBuffer buffer, JpekImage jpekImage);

    private static class APP_DECODER extends JpekSegmentDecoder {

        @Override
        public boolean accept(int marker) {
             return marker >= APP0 && marker <= APP16;
        }

        @Override
        public void decode(ByteBuffer buffer, JpekImage jpekImage) {
            int length = 0xffff & buffer.getShort();
            debug("  length: %d", length);

            buffer = IOHelper.wrap(buffer, length - 2);
            byte[] ba = new byte[5];
            buffer.get(ba);
            String id = new String(ba);
            debug("  identifier: [%s]", id);
            if (id.equals(JFIF_MARKER)) {
                int major = 0xff & buffer.get();
                int minor = 0xff & buffer.get();
                int units = 0xff & buffer.get();
                int xDensity = 0xffff & buffer.getShort();
                int yDensity = 0xffff & buffer.getShort();
                int xThumb = 0xff & buffer.get();
                int yThumb = 0xff & buffer.get();
                debug("  version = [%d.%d]", major, minor);
                debug("  density = %d x %d", xDensity, yDensity);
                debug("  thumbnail = %d x %d", xThumb, yThumb);
            }
        }
    }

    private static class DQT_DECODER extends JpekSegmentDecoder {

        @Override
        public boolean accept(int marker) {
            return marker == DQT;
        }

        @Override
        public void decode(ByteBuffer buffer, JpekImage jpekImage) {
            int length = 0xffff & buffer.getShort();
            debug("  Segment length = " + length);
            buffer = IOHelper.wrap(buffer, length - 2);
            while (buffer.hasRemaining()) {
                int bits = buffer.get();
                int tableId = IOHelper.bits(bits, 0, 4);
                int precision = IOHelper.bits(bits, 4, 4);
                debug("  Precision = %d bits", precision);
                debug("  Destination ID = %d", tableId);
                List<Integer> data = new ArrayList<Integer>(64);
                for (int i = 0; i < QTABLE_SIZE; i++) {
                    data.add(buffer.get(buffer.position() + zigzag[i]) & 0xff);
                }
                buffer.position(buffer.position() + 64);
                jpekImage.qTables.put(tableId, data);
                for (int i = 0; i < 8; i++) {
                    StringBuilder message = new StringBuilder("    DQT, Row #").append(i).append(" ");
                    for (int j = 0; j < 8; j++) {
                        message.append(" " + data.get(i * 8 + j));
                    }
                    debug(message.toString());
                }
            }
        }
    }

    private static class SOF_DECODER extends JpekSegmentDecoder {

        @Override
        public boolean accept(int marker) {
            return marker == SOF;
        }

        @Override
        public void decode(ByteBuffer buffer, JpekImage jpekImage) {
            int length = 0xffff & buffer.getShort();
            debug("  Segment length = %d", length);
            buffer = IOHelper.wrap(buffer, length - 2);
            int precision = 0xff & buffer.get();
            int height = 0xffff & buffer.getShort();
            int width = 0xffff & buffer.getShort();
            int nOfComponents = 0xff & buffer.get();
            debug("  Precision = %d", precision);
            debug("  Height = %d, Width = %d", height, width);
            debug("  Number of components = %d", nOfComponents);
            for (int i = 0; i < nOfComponents; i++) {
                int componentId = 0xff & buffer.get();
                int samplingFactors = 0xff & buffer.get();
                int qTableNo = 0xff & buffer.get();
                debug("  *** Component %d : sampling factor %x Q Table %d", componentId, samplingFactors, qTableNo);
            }
        }
    }

    private static class DHT_DECODER extends JpekSegmentDecoder {

        @Override
        public boolean accept(int marker) {
            return marker == DHT;
        }

        @Override
        public void decode(ByteBuffer buffer, JpekImage jpekImage) {
            int length = 0xffff & buffer.getShort();
            debug("Huffman table length = %d", length);
            buffer = IOHelper.wrap(buffer, length - 2);
            while (buffer.hasRemaining()) {
                byte bits = buffer.get();
                int destinationId = bits & 0xf;
                int clazz = bits >>> 4;
                debug("Destination ID = %d", destinationId);
                debug("Class = %d", clazz);
                List<Integer> codeLengths = new ArrayList<Integer>();
                for (int i = 0; i < 16; i++) {
                    int count = buffer.get() & 0xff;
                    codeLengths.add(count);
                }
                List<List<Integer>> tree = new ArrayList<List<Integer>>();
                int len = 1;
                BinaryTree binaryTree = new BinaryTree();
                for (int i = 0; i < codeLengths.size(); i++) {
                    len = len * 2;
                    int codeLength = codeLengths.get(i);
                    List<Integer> level = new ArrayList<Integer>();
                    tree.add(level);
                    for (int j = 0; j < codeLength; j++) {
                        int code = buffer.get() & 0xff;
                        level.add(code);
                        binaryTree.addLeaf(code);
                    }
                    if (i < codeLengths.size() - 1) {
                        binaryTree.fillLevel();
                    }
                }
                int i = 1;
                for (List<Integer> codes : tree) {
                    debug("  Codes of length " + i + " bits (" + codes.size() + " total): ");
                    StringBuilder message = new StringBuilder("    ");
                    for (int code : codes) {
                        message.append("0x").append(Integer.toHexString(code)).append(" ");
                    }
                    debug(message.toString());
                    i++;
                }
                debug(binaryTree.toString());
            }
        }
    }

    private static class SOS_DECODER extends JpekSegmentDecoder {


        @Override
        public boolean accept(int marker) {
            return marker == SOS;
        }

        @Override
        public void decode(ByteBuffer buffer, JpekImage jpekImage) {
            int length = 0xffff & buffer.getShort();
            ByteBuffer orig = buffer;
            buffer = IOHelper.wrap(buffer, length - 2);
            int nOfComponents = 0xff & buffer.get();
            debug("  length = %d", length);
            debug("  number of components = %d", nOfComponents);
            for (int i = 0; i < nOfComponents; i++) {
                int componentId = 0xff & buffer.get();
                int huffTable = 0xff & buffer.get();
                int acTable = IOHelper.bits(huffTable, 0, 4);
                int dcTable = IOHelper.bits(huffTable, 4, 4);
                debug("  Component %d : Huff table [AC = %d, DC = %d]", componentId, acTable, dcTable);
            }
            readScanData(orig);
        }

        private void readScanData(ByteBuffer buffer) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream(buffer.remaining());
            while (buffer.hasRemaining()) {
                int b = 0xff & buffer.get();
                if (b == 0xff) {
                    int marker = 0xff & buffer.get();
                    if (marker == 0x00) {
                        bytes.write(b);
                    } else if (marker == EOI) {
                        debug("*** Marker EOI ***");
                        debug("OFFSET: %d", (buffer.position() - 2));
                        break;
                    } else {
                        throw new IllegalStateException(String.format("Unexpected marker at scan data %x", marker));
                    }
                } else {
                    bytes.write(b);
                }
            }
            debug("  Read %d bytes of data", bytes.toByteArray().length);
            debug("  Read data: %s", IOHelper.toString(bytes.toByteArray()));
        }
    }

    private static class SOI_DECODER extends JpekSegmentDecoder {
        @Override
        public boolean accept(int marker) {
            return marker == SOI;
        }

        @Override
        public void decode(ByteBuffer buffer, JpekImage jpekImage) {
        }
    }
}
