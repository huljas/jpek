package pragmatastic.jpek;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author huljas
 */
public class JpekImage {

    public HashMap<Integer,List<Integer>> qTables = new HashMap<Integer, List<Integer>>();
    public HashMap<String,BinaryTree> dhtTrees = new HashMap<String,BinaryTree>();
    public byte[] scanData;

    public MCU[] mcus = new MCU[2];
    
    public void addDHTTree(int destinationId, int clazz, BinaryTree binaryTree) {
        dhtTrees.put(destinationId + "-" + clazz, binaryTree);
    }
    
    public BufferedImage process() {
        String binaryString = IOHelper.toBinaryString(scanData);
        
        System.out.println("binaryString " + binaryString);
        
        int start = 0;
        int mcuIndex = 0;
        MCU mcu = new MCU();
        for (int i = 0; i < binaryString.length(); i++) {
            String key = binaryString.substring(start, i+1);
            Integer offset = dhtTrees.get(mcu.tableId()).codes().get(new BinaryString(key));
            if (offset != null) {
                System.out.println("key " + key);
                String s = binaryString.substring(i+1,i+1+offset);
                System.out.println("value " + s);
                start = i+1+offset;
                i = start;
                int value = 0;
                if (s.length() > 0) {
                    value = JpekDecoder.decodeDC(s);
                }
                mcu.push(value);
                if (mcu.done()) {
                    mcus[mcuIndex++] = mcu;
                    mcu = new MCU();
                }
            }
            if (mcuIndex >= mcus.length) {
                break;
            }
        }
        System.out.println("MCUS: ");
        for (MCU m : mcus) {
            System.out.println(m);
        }
        
        for (int i = 0; i < mcus.length; i++) {
            if (i > 0) {
                mcus[i].components[0].dc = mcus[i].components[0].dc + mcus[i-1].components[0].dc;
            }
        }

        System.out.println("Absolute MCUS: ");
        for (MCU m : mcus) {
            System.out.println(m);
        }

        BufferedImage image = new BufferedImage(16, 8, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0,0,8,8,mcus[0].rgbData(), 0, 0);
        image.setRGB(8,0,8,8,mcus[1].rgbData(), 0, 0);

        return image;
    }

    private class MCU {
        private int index = 0;
        private MCUComponent[] components = new MCUComponent[3];

        private MCU() {
            for (int i = 0; i < components.length; i++) {
                components[i] = new MCUComponent();
            }
        }

        public void push(int value) {
            components[index].push(value);
            if (components[index].done()) {
                index++;
            }
        }

        public boolean done() {
            return index >= components.length;
        }

        public String tableId() {
            if (index == 0) {
                return "0-" + components[index].tableId();
            }
            return "1-" + components[index].tableId();
        }

        @Override
        public String toString() {
            return "MCU: Y[" + components[0] + "] CB[" + components[1] + "] CR[" + components[2] + "]";
        }

        public int[] rgbData() {
            int[] ycbcr = new int[3];
            for (int i = 0; i < ycbcr.length; i++) {
                ycbcr[i] = components[i].dc / 4;
            }
            ycbcr[0] = ycbcr[0] + 128;
            int base = JpekDecoder.colorConvert(ycbcr);
            System.out.println("Base color: #" + Integer.toHexString(base));
            int[] data = new int[8*8];
            for (int i = 0; i < data.length; i++) {
                data[i] = base;
            }
            return data;
        }
    }

    private class MCUComponent {
        
        private int dc = Integer.MIN_VALUE;
        private int ac = Integer.MIN_VALUE;
        
        public void push(int value) {
            if (dc == Integer.MIN_VALUE) {
                dc = value;
            } else {
                ac = value;
            }
        }
        
        public boolean done() {
            return dc != Integer.MIN_VALUE && ac != Integer.MIN_VALUE;
        }

        public String tableId() {
            if (dc == Integer.MIN_VALUE) {
                return "0";
            }
            return "1";
        }

        @Override
        public String toString() {
            return "[" +
                    "dc=" + dc +
                    ", ac=" + ac +
                    ']';
        }
    }
}
