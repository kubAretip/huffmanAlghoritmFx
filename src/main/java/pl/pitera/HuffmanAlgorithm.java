package pl.pitera;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;


public class HuffmanAlgorithm {


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

    public Map<Character, String> encodeCharacters(TreeNode root) {
        Map<Character, String> huffmanCodes = new HashMap<>();
        encode(root, "", huffmanCodes);
        return huffmanCodes;

    }

    private void encode(TreeNode treeNode, String str, Map<Character, String> huffmanCodes) {

        if (treeNode != null) {

            if (treeNode.getLeft() == null && treeNode.getRight() == null) {
                huffmanCodes.put(treeNode.getCharacter(), str);
            }
            encode(treeNode.getLeft(), str + "0", huffmanCodes);
            encode(treeNode.getRight(), str + "1", huffmanCodes);
        }
    }

    public double calcEntropy(List<CharacterViewModel> characterViewModelList, int textLength) {

        return characterViewModelList.stream()
                .mapToDouble(characterViewModel -> (characterViewModel.getFrequency() / (double) textLength))
                .map(prob -> (prob * CustomMath.customLog(2, (1 / prob)))).sum();

    }


    public double calcAvgWordLength(List<CharacterViewModel> characterViewModelList, int textLength) {
        double sum = 0;

        for (CharacterViewModel characterViewModel : characterViewModelList) {
            var prob = (characterViewModel.getFrequency() / (double) textLength);
            sum += (prob * characterViewModel.getCode().length());
        }

        return sum;
    }


    public int calcInputBits(int textLength) {
        return textLength * 8;
    }

    public double calcCompressionPercent(int inputBits, int outputBits) {
        return 100.0 - ((outputBits * 100.0) / inputBits);
    }

}
