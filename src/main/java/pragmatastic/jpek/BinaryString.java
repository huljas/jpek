package pragmatastic.jpek;

import org.apache.commons.lang.StringUtils;

/**
 * @author huljas
 */
public class BinaryString {

    public int value;
    public int lengthInBits;

    public BinaryString(String sValue) {
        this.value = Integer.parseInt(sValue, 2);
        this.lengthInBits = sValue.length();
    }

    public BinaryString(int value, int lengthInBits) {
        this.value = value;
        this.lengthInBits = lengthInBits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BinaryString that = (BinaryString) o;

        if (lengthInBits != that.lengthInBits) return false;
        if (value != that.value) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value;
        result = 31 * result + lengthInBits;
        return result;
    }

    public String toString() {
        String s = Integer.toBinaryString(value);
        return StringUtils.repeat("0", lengthInBits - s.length()) + s;
    }

    public BinaryString substring(int startIndex, int length) {
        return new BinaryString(IOHelper.bitsLeft(value, (32-lengthInBits) + startIndex, length), length);
    }
}
