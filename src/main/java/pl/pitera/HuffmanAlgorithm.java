package pl.pitera;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.*;


public class HuffmanAlgorithm {

    private static final int CIRCLE_DIAMETER = 40;
    private static final int BRANCH_ANGLE = 60;

    private static final int LEAVES_MARGIN = 50;


    private static final int CIRCLE_RADIUS = CIRCLE_DIAMETER / 2;

    private Canvas treeCanvas;

    public void buildTree(List<CharCode> charCodeList) {


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

            TreeNode root = treeNodes.peek();
            Map<Character, String> huffmanCodes = new HashMap<>();
            encode(root, "", huffmanCodes);
            updateList(charCodeList, huffmanCodes);
            buildTreeView(root);

        }

    }


    private void buildTreeView(TreeNode tree) {

        int treeHeight = TreeNode.getHeight(tree);

        this.treeCanvas = new Canvas(4000, 2000);
        GraphicsContext graphicsContext = treeCanvas.getGraphicsContext2D();

        //gc.setFont(new Font("Arial", 14));

         //double initialMarginBetweenItems = LEAVES_MARGIN *treeHeight;
        double initialMarginBetweenItems = LEAVES_MARGIN * Math.pow(2, (treeHeight - 2));
        //  double initialTreePositionX = Math.pow(2, (treeHeight )) * 20;
        double initialTreePositionX = treeHeight * 100;
        double initialTreePositionY = 50;

        display(tree, initialTreePositionX, initialTreePositionY, graphicsContext, initialMarginBetweenItems);
    }

    public Canvas getTreeView() {
        return treeCanvas;
    }


    private void updateList(List<CharCode> charCodeList, Map<Character, String> huffmanCodes) {
        charCodeList.forEach(charCode -> {
            huffmanCodes.forEach((character, s) -> {
                if (charCode.getCharacter().equals(character.toString())) {
                    charCode.setCode(s);
                }
            });
        });
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

    private static double customLog(double base, double logNumber) {
        return Math.log(logNumber) / Math.log(base);
    }

    public double avgWordLength(List<CharCode> charCodeList, int textLength) {
        double sum = 0;

        for (CharCode charCode : charCodeList) {
            var prob = (charCode.getFrequency() / (double) textLength);
            sum += (prob * charCode.getCode().length());
        }

        return sum;
    }

    public static void drawCircle(TreeNode root, double x, double y, GraphicsContext graphicsContext) {

        //style do zmiany i do osobnej metody dać
        //  gc.setStroke(Color.TRANSPARENT);
        graphicsContext.setLineWidth(2);
        // gc.strokeOval(x, y, 52, 52);
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.fillOval(x, y, CIRCLE_DIAMETER, CIRCLE_DIAMETER);
        // gc.setStroke(Color.BLACK);
        graphicsContext.setFill(Color.BLACK);

        //sprawdzamy jaki aktualnie znak ma element, jeśli null to znaczy że jest to połączenie gałęzi
        if (root.getCharacter() != null) {
            graphicsContext.fillText(Integer.toString(root.getFreq()), (x + CIRCLE_RADIUS) - 5, y + CIRCLE_RADIUS - 5, 10);
            graphicsContext.fillText("(" + root.getCharacter() + ")", (x + CIRCLE_RADIUS) - 7, (y + CIRCLE_DIAMETER) - 10, 10);
        } else {
            graphicsContext.fillText(Integer.toString(root.getFreq()), (x + CIRCLE_RADIUS) - 4, y + CIRCLE_RADIUS + 5, 10);
        }
    }

    protected static void display(TreeNode root, double x, double y, GraphicsContext graphicsContext, double leavesMargin) {

        drawCircle(root, x, y, graphicsContext);

        double x0;
        double x1;
        double y0;
        double y1;
        double branchMarkPositionX;
        double branchMarkPositionY;

        if (root.getLeft() != null) {
            x0 = x + CIRCLE_RADIUS;
            y0 = y + CIRCLE_DIAMETER;
            x1 = x0 - leavesMargin;
            y1 = y0 + BRANCH_ANGLE;
            branchMarkPositionX = ((x0 + x1) / 2) - 7;
            branchMarkPositionY = ((y1 + y0) / 2);

            graphicsContext.strokeLine(x0, y0, x1, y1);
            graphicsContext.fillText("0", branchMarkPositionX, branchMarkPositionY);

            display(root.getLeft(), x1 - CIRCLE_RADIUS, y1, graphicsContext, leavesMargin /2);
        }
        if (root.getRight() != null) {
            x0 = x + CIRCLE_RADIUS;
            y0 = y + CIRCLE_DIAMETER;
            x1 = x0 + leavesMargin;
            y1 = y0 + BRANCH_ANGLE;
            branchMarkPositionX = (x1 + x0) / 2;
            branchMarkPositionY = (y1 + y0) / 2;

            graphicsContext.strokeLine(x0, y0, x1, y1);
            graphicsContext.fillText("1", branchMarkPositionX, branchMarkPositionY);

            display(root.getRight(), x1 - CIRCLE_RADIUS, y1, graphicsContext, leavesMargin/2 );
        }
        //TODO
        // - poprawienie pozycjonowania i rysowania drzewa
        // - style

    }

}
