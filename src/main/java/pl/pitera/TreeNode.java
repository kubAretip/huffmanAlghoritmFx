package pl.pitera;


public class TreeNode {

    private int freq;
    private Character character;
    private TreeNode leftChildren;
    private TreeNode rightChildren;

    public TreeNode(int freq, Character character) {
        this.freq = freq;
        this.character = character;
    }

    public TreeNode(int freq, Character character, TreeNode leftChildren, TreeNode rightChildren) {
        this.freq = freq;
        this.character = character;
        this.leftChildren = leftChildren;
        this.rightChildren = rightChildren;
    }

    public static int getHeight(TreeNode root) {
        if (root == null)
            return 0;

        int heightLeft = getHeight(root.getLeftChildren());
        int heightRight = getHeight(root.getRightChildren());

        return 1 + Math.max(heightLeft, heightRight);
    }

    public int getFreq() {
        return freq;
    }

    public Character getCharacter() {
        return character;
    }

    public TreeNode getLeftChildren() {
        return leftChildren;
    }

    public TreeNode getRightChildren() {
        return rightChildren;
    }

}
