package pl.pitera;

public class TreeNode {

    private int freq;
    private Character character;
    private TreeNode left;
    private TreeNode right;


    public TreeNode(int freq, Character character) {
        this.freq = freq;
        this.character = character;
    }

    public TreeNode(int freq, Character character, TreeNode left, TreeNode right) {
        this.freq = freq;
        this.character = character;
        this.left = left;
        this.right = right;
    }

    public static int getHeight(TreeNode root) {
        if (root == null)
            return 0;

        int heightLeft = getHeight(root.getLeft());
        int heightRight = getHeight(root.getRight());

        return 1 + Math.max(heightLeft, heightRight);
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
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

    @Override
    public String toString() {
        return "TreeNode{" +
                "freq=" + freq +
                ", character=" + character +
                ", left=" + left +
                ", right=" + right +
                '}';
    }
}
