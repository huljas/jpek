package pragmatastic.jpek;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

/**
 * @author huljas
 */
public class JpekImage {

    public HashMap<Integer,List<Integer>> qTables = new HashMap<Integer, List<Integer>>();
    public HashMap<String,BinaryTree> dhtTrees = new HashMap<String,BinaryTree>();
    public byte[] scanData;

    public void addDHTTree(int destinationId, int clazz, BinaryTree binaryTree) {
        dhtTrees.put(destinationId + "-" + clazz, binaryTree);
    }
}
