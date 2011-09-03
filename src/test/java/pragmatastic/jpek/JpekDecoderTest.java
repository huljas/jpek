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

    }

}
