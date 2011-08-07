package pragmatastic.jpek;

import org.junit.*;
import pragmatastic.jpek.BinaryTree.*;

import static org.junit.Assert.*;

/**
 * @author exthulja
 */
public class BinaryTreeTest {

    @Test
    public void simpleTreeWithTwoNodes() {
        BinaryTree tree = new BinaryTree();
        tree.addLeaf(1).addLeaf(2);
        assertEquals("0", tree.leaves.get(0).binaryKey());
        assertEquals("1", tree.leaves.get(1).binaryKey());
    }

    @Test
    public void moreComplicatedTreeWithBasicOperations() {
        BinaryTree tree = new BinaryTree();
        tree.addNewBranch(2).addLeaf(1).addLeaf(2).addNewBranch(2);
        tree.addLeaf(3).addNewBranch(3);
        tree.addLeaf(11).addLeaf(4).addLeaf(0).addNewBranch(3);
        tree.addLeaf(5).addLeaf(21).addLeaf(12);
        assertEquals("00", tree.leaves.get(0).binaryKey());
        assertEquals("01", tree.leaves.get(1).binaryKey());
        assertEquals("100", tree.leaves.get(2).binaryKey());
        assertEquals("1010", tree.leaves.get(3).binaryKey());
        assertEquals("1011", tree.leaves.get(4).binaryKey());
        assertEquals("1100", tree.leaves.get(5).binaryKey());
        assertEquals("11010", tree.leaves.get(6).binaryKey());
        assertEquals("11011", tree.leaves.get(7).binaryKey());
        assertEquals("11100", tree.leaves.get(8).binaryKey());
    }

    @Test
    public void fillLevelWithSimpleTree() {
        BinaryTree tree = new BinaryTree();
        tree.fillLevel();
        assertEquals(3, tree.branches.size());
        tree.addLeaf(1).fillLevel();
        assertEquals(3 + 3, tree.branches.size());

    }

    @Test
    public void moreComplicatedTreeWithFillLevel() {
        BinaryTree tree = new BinaryTree();
        tree.addNewBranch(2).addLeaf(1).addLeaf(2).fillLevel();
        tree.addLeaf(3).fillLevel();
        tree.addLeaf(11).addLeaf(4).addLeaf(0).fillLevel();
        tree.addLeaf(5).addLeaf(21).addLeaf(12);
        assertEquals("00", tree.leaves.get(0).binaryKey());
        assertEquals("01", tree.leaves.get(1).binaryKey());
        assertEquals("100", tree.leaves.get(2).binaryKey());
        assertEquals("1010", tree.leaves.get(3).binaryKey());
        assertEquals("1011", tree.leaves.get(4).binaryKey());
        assertEquals("1100", tree.leaves.get(5).binaryKey());
        assertEquals("11010", tree.leaves.get(6).binaryKey());
        assertEquals("11011", tree.leaves.get(7).binaryKey());
        assertEquals("11100", tree.leaves.get(8).binaryKey());
    }
}
