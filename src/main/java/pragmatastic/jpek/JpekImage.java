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
    
    public void process() {
        String binaryString = IOHelper.toBinaryString(scanData);
        for (int i = 0; i < binaryString.length(); i++) {
            String key = binaryString.substring(0, i+1);
            Integer value = dhtTrees.get("0-0").codes().get(new BinaryString(key));
            if (value != null) {
                System.out.println("Match: " + value);
                return;
            }
        }
    }
}
