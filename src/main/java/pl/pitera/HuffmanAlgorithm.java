package pl.pitera;

import java.util.*;


public class HuffmanAlgorithm {

    /**
     * @param charCodeList list with characters
     * @return return tree root node
     * @see CharCode
     */
    public TreeNode buildTree(List<CharCode> charCodeList) {

        if (charCodeList.size() > 1) {

            PriorityQueue<TreeNode> treeNodes = new PriorityQueue<>(Comparator.comparingInt(TreeNode::getFreq));
            charCodeList.forEach(charCode -> treeNodes.add(new TreeNode(charCode.getFrequency(), charCode.getCharacter().toCharArray()[0])));

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
        return null;
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

    public double calcEntropy(List<CharCode> charCodeList, int textLength) {

        return charCodeList.stream()
                .mapToDouble(charCode -> (charCode.getFrequency() / (double) textLength))
                .map(prob -> (prob * customLog(2, (1 / prob)))).sum();

    }


    public double avgWordLength(List<CharCode> charCodeList, int textLength) {
        double sum = 0;

        for (CharCode charCode : charCodeList) {
            var prob = (charCode.getFrequency() / (double) textLength);
            sum += (prob * charCode.getCode().length());
        }

        return sum;
    }

    private static double customLog(double base, double logNumber) {
        return Math.log(logNumber) / Math.log(base);
    }

}
