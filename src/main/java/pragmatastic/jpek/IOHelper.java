package pragmatastic.jpek;

import org.apache.commons.lang.StringUtils;

import java.nio.ByteBuffer;

/**
 * @author huljas
 */
public class IOHelper {

    public static ByteBuffer wrap(ByteBuffer buffer, int length) {
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return ByteBuffer.wrap(bytes);
    }

    public static int bits(int value, int start, int length) {
        int left = 32 - length - start;
        int right = left + start;
        int result = (value << left) >>> right;
        return result;
    }

    public static int bitsLeft(int value, int start, int length) {
        int left = start;
        int right = 32 - length;
        int result = (value << left) >>> right;
        return result;
    }

    public static String toString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format(" length = %d, data = { ", bytes.length));
        for (byte b : bytes) {
            builder.append(String.format("%x ", b));
        }
        builder.append("}");
        return builder.toString();
    }

    public static String toBinaryString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            String s = Integer.toBinaryString(0xff & b);
            builder.append(StringUtils.repeat("0", 8 - s.length())).append(s);
        }
        return builder.toString();
    }
}
