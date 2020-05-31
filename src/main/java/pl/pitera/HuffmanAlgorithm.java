package pl.pitera;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;


public class HuffmanAlgorithm {

    /**
     * build huffman tree structure
     *
     * @param treeNodes queue with tree element. It's a message divided into characters (the characters are wrapped in TreeNode objects) and added to the queue
     * @return tree top element with huffman tree structure
     * @see TreeNode
     */
    public TreeNode buildTree(PriorityQueue<TreeNode> treeNodes) {

        while (treeNodes.size() != 1) {

            TreeNode leftNode = treeNodes.poll();
            TreeNode rightNode = treeNodes.poll();

            if (leftNode != null && rightNode != null) {
                int rootSum = leftNode.getFreq() + rightNode.getFreq();
                treeNodes.add(new TreeNode(rootSum, null, leftNode, rightNode));
            }
        }
        return treeNodes.peek();
    }

    /**
     * @param root the top element of the huffman tree
     * @return characters with codes
     */
    public Map<Character, String> encodeCharacters(TreeNode root) {
        Map<Character, String> huffmanCodes = new HashMap<>();
        encode(root, "", huffmanCodes);
        return huffmanCodes;

    }

    private void encode(TreeNode treeNode, String str, Map<Character, String> huffmanCodes) {

        if (treeNode != null) {

            if (treeNode.getLeftChildren() == null && treeNode.getRightChildren() == null) {
                huffmanCodes.put(treeNode.getCharacter(), str);
            }
            encode(treeNode.getLeftChildren(), str + "0", huffmanCodes);
            encode(treeNode.getRightChildren(), str + "1", huffmanCodes);
        }
    }

    public double calcEntropy(List<CharacterModel> characterModelList, int textLength) {

        return characterModelList.stream()
                .mapToDouble(characterModel -> (characterModel.getFrequency() / (double) textLength))
                .map(prob -> (prob * CustomMath.customLog(2, (1 / prob)))).sum();

    }

    public double calcAvgWordLength(List<CharacterModel> characterModelList, int textLength) {
        double sum = 0;

        for (CharacterModel characterModel : characterModelList) {
            var prob = (characterModel.getFrequency() / (double) textLength);
            sum += (prob * characterModel.getCode().length());
        }

        return sum;
    }


    public int calcInputBits(int textLength) {
        return textLength * 8;
    }

    public double calcCompressionInPercent(int inputBits, int outputBits) {
        return 100 - ((outputBits * 100.0) / inputBits);
    }

}
