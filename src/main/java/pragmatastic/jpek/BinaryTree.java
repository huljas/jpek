package pragmatastic.jpek;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author exthulja
 */
public class BinaryTree {

    public Branch root;
    public Branch currentBranch;
    public ArrayList<Branch> branches;
    public ArrayList<Leaf> leaves;
    private HashMap<BinaryString,Integer> codes;

    public BinaryTree() {
        this.root = new Branch();
        this.currentBranch = root;
        branches = new ArrayList<Branch>();
        leaves = new ArrayList<Leaf>();
        branches.add(root);
    }

    public void fillLevel() {
        int level = currentBranch.level();
        while (nextBranch().level() == level) {
            addNewBranch();
        }
    }


    public BinaryTree addNewBranch(int count) {
        for (int i = 0; i < count; i++) {
            addNewBranch();
        }
        return this;
    }

    public BinaryTree addNewBranch() {
        Branch branch = new Branch();
        nextBranch().addChild(branch);
        branch.index = branches.size();
        branches.add(branch);
        return this;
    }

    public BinaryTree addLeaf(int code) {
        Leaf leaf = new Leaf(code);
        nextBranch().addChild(leaf);
        leaf.index = leaves.size();
        leaves.add(leaf);
        return this;
    }

    public Map<BinaryString, Integer> codes() {
        if (codes == null) {
            codes = new HashMap<BinaryString, Integer>();
            for(Leaf leaf : this.leaves) {
                codes.put(new BinaryString(leaf.binaryKey()), leaf.code);
            }
        }
        return codes;
    }

    public Branch nextBranch() {
        if (currentBranch.isFilled()) {
            currentBranch = branches.get(currentBranch.index + 1);
        }
        return currentBranch;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Binary tree with " + leaves.size() + " leaves\n");
        builder.append("Leaf nodes:\n");
        int length = 0;
        for (Leaf leaf : leaves) {
            String key = leaf.binaryKey();
            if (length < key.length()) {
                length = key.length();
                builder.append("  Codes with ").append(key.length()).append(" bits:\n");
            }
            builder.append("    ").append(key).append(" = ").append("0x" + Integer.toHexString(leaf.code)).append("\n");

        }
        return builder.toString();
    }

    public static class Leaf extends Node {
        public int code;

        public Leaf(int code) {
            this.code = code;
        }

        @Override
        public boolean isFilled() {
            return true;
        }

        public boolean isLeaf() {
            return true;
        }
    }

    public static class Branch extends Node {
        public Node node0;
        public Node node1;

        public Branch() {
        }

        public void addChild(Node node) {
            if (node0 == null) {
                node0 = node;
            } else if (node1 == null) {
                node1 = node;
            } else {
                throw new IllegalStateException("Node full.");
            }
            node.parent = this;
        }

        public boolean isFilled() {
            return node0 != null && node1 != null;
        }

        public boolean isLeaf() {
            return false;
        }

        public int level() {
            if (parent == null) {
                return 0;
            }
            return parent.level() + 1;
        }
    }

    public static abstract class Node {

        public Branch parent;
        public int index;

        protected Node() {
        }

        public abstract boolean isFilled();

        public abstract boolean isLeaf();

        public String binaryKey() {
            if (parent == null) {
                return "";
            }
            StringBuilder builder = new StringBuilder();
            builder.append(parent.binaryKey());
            if (parent.node0 == this) {
                builder.append("0");
            } else {
                builder.append("1");
            }
            return builder.toString();
        }
    }
}
