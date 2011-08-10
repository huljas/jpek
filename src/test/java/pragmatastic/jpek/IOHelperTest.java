package pragmatastic.jpek;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author huljas
 */
public class IOHelperTest {

    @Test
    public void howDidTheBitwiseOperandsWork() {
        assertEquals(0, 0 << 0);
        assertEquals(1, 1 << 0);
        assertEquals(2, 1 << 1);
        assertEquals(0, 0 >> 0);
        assertEquals(1, 1 >> 0);
        assertEquals(0, 1 >> 1);
        assertEquals(1, 16 >> 4);
        assertEquals(0x3, 0x31 >> 4);
        assertEquals(0x1, (0x31 >> 0) << (32 - 4) >> (32 - 4));
        assertEquals(0x1, (0x1 << (32-4) >> (32-4)));
        assertEquals(1, (1 << 16) >> 16);
        assertEquals(1, (2 << 16) >> 17);
        assertEquals(1, (1 << 32));
        assertEquals(1, (1 << 31) >>> 31);
        assertEquals(0, (2 << 31) >>> 31);
        assertEquals(1, (2 << 30) >>> 31);
        assertEquals(1, (4 << 29) >>> 31);
        assertEquals(2, (4 << 29) >>> 30);
    }

    @Test
    public void bitsFromTheStart() {
        assertEquals(1, IOHelper.bits(1, 0, 1));
        assertEquals(1, IOHelper.bits(1, 0, 2));
        assertEquals(1, IOHelper.bits(1, 0, 10));
        assertEquals(1, IOHelper.bits(3, 0, 1));
        assertEquals(3, IOHelper.bits(3, 0, 10));
    }

    @Test
    public void bitsFromMiddle() {
        assertEquals(1, IOHelper.bits(2, 1, 1));
        assertEquals(1, IOHelper.bits(2, 1, 10));
        assertEquals(1, IOHelper.bits(3, 1, 10));
        assertEquals(3, IOHelper.bits(6, 1, 2));
        assertEquals(1, IOHelper.bits(6, 2, 2));
    }

    @Test
    public void splittingByte() {
        assertEquals(0xf, IOHelper.bits(0xf3, 4, 4));
        assertEquals(0x3, IOHelper.bits(0xf3, 0, 4));
    }

    @Test
    public void truncateSmallNumber() {
        assertEquals(0, IOHelper.bits(1, 1, 10));
    }

    @Test
    public void getHigherBits() {
        assertEquals(1, IOHelper.bits(17, 4, 4));
        assertEquals(1, IOHelper.bits(17, 4, 8));
    }
}
