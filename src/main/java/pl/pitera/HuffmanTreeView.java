package pl.pitera;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class HuffmanTreeView {

    private static final int CIRCLE_DIAMETER = 40;
    private static final int BRANCH_ANGLE = 60;
    private static final int CIRCLE_RADIUS = CIRCLE_DIAMETER / 2;

    public Canvas drawTree(TreeNode tree) {

        int treeHeight = TreeNode.getHeight(tree);

        //init tree position
        double initialMarginBetweenItems = (Math.pow(2, treeHeight - 1) * CIRCLE_RADIUS) / 2;
        double initialTreePositionX = 2 * initialMarginBetweenItems;
        double initialTreePositionY = 50;

        double canvasY = (treeHeight * CIRCLE_DIAMETER) + ((treeHeight - 1) * 60) + initialTreePositionY;
        double canvasX = 4 * initialMarginBetweenItems;

        if (canvasX <= 10240) {
            final Canvas treeCanvas = new Canvas(canvasX, canvasY);
            System.out.println("X " + canvasX);
            System.out.println("Y " + canvasY);


            GraphicsContext graphicsContext = treeCanvas.getGraphicsContext2D();
            graphicsContext.scale(0.75, 0.75);

            drawBranch(tree, initialTreePositionX, initialTreePositionY, graphicsContext, initialMarginBetweenItems);

            return treeCanvas;
        } else {
            canvasX = 10240;
            canvasY = 1000;
            final Canvas treeCanvas = new Canvas(canvasX, canvasY);
            GraphicsContext graphicsContext = treeCanvas.getGraphicsContext2D();
            graphicsContext.scale(0.3, 0.3);

            drawBranch(tree, initialTreePositionX, initialTreePositionY, graphicsContext, initialMarginBetweenItems);
            return treeCanvas;
            // return null
            // is javafx max canvas size error
            // when size is bigger 10240px compiler throw nullpointerexception
           // return null;
        }

    }

    private void drawCircle(TreeNode root, double x, double y, GraphicsContext graphicsContext) {

        graphicsContext.setLineWidth(2);
        graphicsContext.setFill(Color.valueOf("#d6d4ff"));
        graphicsContext.fillOval(x, y, CIRCLE_DIAMETER, CIRCLE_DIAMETER);
        graphicsContext.setFill(Color.BLACK);

        //check node char, if is null it is a element with two leaves prob. sum
        if (root.getCharacter() != null) {
            graphicsContext.fillText(Integer.toString(root.getFreq()), (x + CIRCLE_RADIUS) - 5, y + CIRCLE_RADIUS - 5, 10);
            graphicsContext.setFill(Color.BLACK);
            graphicsContext.fillOval((x + (CIRCLE_RADIUS / 2.0)), (y + CIRCLE_RADIUS), 20, 20);
            graphicsContext.setFill(Color.WHITE);

            String character = root.getCharacter().toString();

            if (character.equals(" "))
                graphicsContext.fillText("spc", (x + (CIRCLE_RADIUS / 2.0) + 5), (y + CIRCLE_DIAMETER) - 5, 10);
            if (character.equals("\n"))
                graphicsContext.fillText("\\n", (x + (CIRCLE_RADIUS / 2.0) + 5), (y + CIRCLE_DIAMETER) - 5, 10);
            if (character.equals("\t"))
                graphicsContext.fillText("tab", (x + (CIRCLE_RADIUS / 2.0) + 5), (y + CIRCLE_DIAMETER) - 5, 10);

            graphicsContext.fillText(character, (x + (CIRCLE_RADIUS / 2.0) + 5), (y + CIRCLE_DIAMETER) - 5, 10);

        } else {
            graphicsContext.fillText(Integer.toString(root.getFreq()), (x + CIRCLE_RADIUS) - 4, y + CIRCLE_RADIUS + 5, 10);
        }
    }

    private void drawBranch(TreeNode root, double x, double y, GraphicsContext graphicsContext, double leavesMargin) {

        drawCircle(root, x, y, graphicsContext);

        double x0;
        double x1;
        double y0;
        double y1;
        double branchMarkPositionX;
        double branchMarkPositionY;
        graphicsContext.setFill(Color.BLACK);

        if (root.getLeft() != null) {

            x0 = x + CIRCLE_RADIUS;
            y0 = y + CIRCLE_DIAMETER;
            x1 = x0 - leavesMargin;
            y1 = y0 + BRANCH_ANGLE;

            branchMarkPositionX = ((x0 + x1) / 2) - 7;
            branchMarkPositionY = ((y1 + y0) / 2);
            graphicsContext.fillText("0", branchMarkPositionX, branchMarkPositionY);

            graphicsContext.strokeLine(x0, y0, x1, y1);

            drawBranch(root.getLeft(), x1 - CIRCLE_RADIUS, y1, graphicsContext, (leavesMargin / 2));
        }
        if (root.getRight() != null) {

            x0 = x + CIRCLE_RADIUS;
            y0 = y + CIRCLE_DIAMETER;
            x1 = x0 + leavesMargin;
            y1 = y0 + BRANCH_ANGLE;

            branchMarkPositionX = (x1 + x0) / 2;
            branchMarkPositionY = (y1 + y0) / 2;
            graphicsContext.fillText("1", branchMarkPositionX, branchMarkPositionY);

            graphicsContext.strokeLine(x0, y0, x1, y1);

            drawBranch(root.getRight(), x1 - CIRCLE_RADIUS, y1, graphicsContext, (leavesMargin / 2));
        }

    }

}
