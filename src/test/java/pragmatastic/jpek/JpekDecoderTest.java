package pragmatastic.jpek;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author huljas
 */
public class JpekDecoderTest {

    @Test
    public void shouldParseTheImageDataCorrectly() throws IOException {
        JpekImage jpekImage = JpekDecoder.decode(IOUtils.toByteArray(new FileInputStream("simple0.jpg")));
        assertTrue("Should have correct scan data", Arrays.equals(new byte[]{(byte)0xfc, (byte)0xff, (byte)0xe2, (byte)0xaf, (byte)0xef, (byte)0xf3, (byte)0x15, (byte)0x7f}, jpekImage.scanData));
        assertEquals("Should have correct huffmann tables", 4, jpekImage.dhtTrees.size());
        assertEquals("Should have correct q tables", 2, jpekImage.qTables.size());
        assertEquals(new Integer(0x4), jpekImage.dhtTrees.get("0-0").codes().get(new BinaryString("000")));
        assertEquals(new Integer(0x8), jpekImage.dhtTrees.get("0-0").codes().get(new BinaryString("11110")));
        assertEquals(new Integer(0x7), jpekImage.dhtTrees.get("1-0").codes().get(new BinaryString("11110")));
        assertEquals(new Integer(0x71), jpekImage.dhtTrees.get("1-1").codes().get(new BinaryString("11110111")));
        assertEquals(8, jpekImage.scanData.length);
        assertEquals(0xfc, 0xff & jpekImage.scanData[0]);
        assertEquals(0xff, 0xff & jpekImage.scanData[1]);
        assertEquals(0xe2, 0xff & jpekImage.scanData[2]);
        assertEquals(0xaf, 0xff & jpekImage.scanData[3]);
        assertEquals(0xef, 0xff & jpekImage.scanData[4]);
        assertEquals(0xf3, 0xff & jpekImage.scanData[5]);
        assertEquals(0x15, 0xff & jpekImage.scanData[6]);
        assertEquals(0x7f, 0xff & jpekImage.scanData[7]);
    }

    
    @Test
    public void decodeDC() {
        assertEquals(0, JpekDecoder.decodeDC(""));
        assertEquals(-1, JpekDecoder.decodeDC("0"));
        assertEquals(1, JpekDecoder.decodeDC("1"));
        assertEquals(-3, JpekDecoder.decodeDC("00"));
        assertEquals(-2, JpekDecoder.decodeDC("01"));
        assertEquals(2, JpekDecoder.decodeDC("10"));
        assertEquals(3, JpekDecoder.decodeDC("11"));
        assertEquals(-8, JpekDecoder.decodeDC("0111"));
        assertEquals(5, JpekDecoder.decodeDC("101"));

    }
}
