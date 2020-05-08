package pl.pitera;

import java.util.*;

public class HuffmanAlgorithm {


    public void buildTree(List<CharCode> charCodeList) {

        if (charCodeList.size() > 1) {
            PriorityQueue<TreeNode> treeNodes = new PriorityQueue<>(Comparator.comparingInt(TreeNode::getFreq));

            charCodeList.forEach(charCode -> treeNodes.add(new TreeNode(charCode.getFrequency(), charCode.getCharacter().toCharArray()[0])));

            while (treeNodes.size() != 1) {

                TreeNode leftNode = treeNodes.poll();
                TreeNode rightNode = treeNodes.poll();

                if (leftNode != null && rightNode != null) {
                    int rootSum = leftNode.getFreq() + rightNode.getFreq();
                    treeNodes.add(new TreeNode(rootSum, '-', leftNode, rightNode));
                }
            }

            TreeNode root = treeNodes.peek();

            Map<Character, String> huffmanCodes = new HashMap<>();
            encode(root, "", huffmanCodes);
            updateList(charCodeList, huffmanCodes);

        }

    }

    private void updateList(List<CharCode> charCodeList, Map<Character, String> huffmanCodes) {
        charCodeList.forEach(charCode -> {
            huffmanCodes.forEach((character, s) ->{
                if(charCode.getCharacter().equals(character.toString())){
                    charCode.setCode(s);
                }
            });
        });
    }


    public void encode(TreeNode treeNode, String str, Map<Character, String> huffmanCodes) {

        if (treeNode != null) {

            if (treeNode.getLeft() == null && treeNode.getRight() == null) {
                huffmanCodes.put(treeNode.getCharacter(), str);
            }
            encode(treeNode.getLeft(), str + "0", huffmanCodes);
            encode(treeNode.getRight(), str + "1", huffmanCodes);
        }
    }


}
