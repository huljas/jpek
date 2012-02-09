package pragmatastic.jpek;

/**
 * Class which lets you read a byte array bit by bit.
 * @author huljas
 */
public class BinaryBuffer {
    private byte[] bytes;
    private int mark = 0;

    public BinaryBuffer(byte[] bytes) {
        this.bytes = bytes;
    }


}
