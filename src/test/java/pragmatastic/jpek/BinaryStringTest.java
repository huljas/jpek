package pragmatastic.jpek;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author huljas
 */
public class BinaryStringTest {

    @Test
    public void constructFromString() {
        assertEquals("0", new BinaryString("0").toString());
        assertEquals("000", new BinaryString("000").toString());
        assertEquals("011", new BinaryString("011").toString());
    }

    @Test
    public void constructFromInt() {
        assertEquals("0", new BinaryString(0,1).toString());
        assertEquals("00000", new BinaryString(0,5).toString());
        assertEquals("0100", new BinaryString(4,4).toString());
    }

    @Test
    public void equalityHolds() {
        assertEquals(new BinaryString("0"), new BinaryString(0,1));
        assertEquals(new BinaryString("0100"), new BinaryString(4,4));
        assertFalse(new BinaryString("110").equals(new BinaryString(6,4)));
    }

    @Test
    public void substringWorks() {
        BinaryString binaryString = new BinaryString("010011011011");
        assertEquals(new BinaryString("0"), binaryString.substring(0, 1));
        assertEquals(new BinaryString("0100"), binaryString.substring(0, 4));
        assertEquals(new BinaryString("11011"), binaryString.substring(4, 5));
        assertEquals(new BinaryString("011011"), binaryString.substring(6, 6));

    }
}
