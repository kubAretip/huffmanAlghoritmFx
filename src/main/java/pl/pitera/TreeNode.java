package pl.pitera;

public class TreeNode {

    private int freq;
    private char character;
    private TreeNode left;
    private TreeNode right;


    public TreeNode(int freq, char character) {
        this.freq = freq;
        this.character = character;
    }

    public TreeNode(int freq, char character, TreeNode left, TreeNode right) {
        this.freq = freq;
        this.character = character;
        this.left = left;
        this.right = right;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public char getCharacter() {
        return character;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

    public TreeNode getLeft() {
        return left;
    }

    public void setLeft(TreeNode left) {
        this.left = left;
    }

    public TreeNode getRight() {
        return right;
    }

    public void setRight(TreeNode right) {
        this.right = right;
    }
}
