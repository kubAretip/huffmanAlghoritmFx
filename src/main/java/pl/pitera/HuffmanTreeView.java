package pl.pitera;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class HuffmanTreeView {

    private static final int CIRCLE_DIAMETER = 40;
    private static final int BRANCH_ANGLE = 60;
    private static final int CIRCLE_RADIUS = CIRCLE_DIAMETER / 2;

    public Canvas buildTreeView(TreeNode tree) {

        int treeHeight = TreeNode.getHeight(tree);

        //max canvas size is 4096x4096 (this is javafx bug)
        Canvas treeCanvas = new Canvas(4096, 2000);
        GraphicsContext graphicsContext = treeCanvas.getGraphicsContext2D();
        //   graphicsContext.scale(0.25,0.25);

        double initialMarginBetweenItems = (Math.pow(2, treeHeight - 1) * CIRCLE_DIAMETER) / 2;
        double initialTreePositionX = 2 * initialMarginBetweenItems;
        double initialTreePositionY = 50;

        display(tree, initialTreePositionX, initialTreePositionY, graphicsContext, initialMarginBetweenItems);

        return treeCanvas;
    }

    private void drawCircle(TreeNode root, double x, double y, GraphicsContext graphicsContext) {

        graphicsContext.setLineWidth(2);
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.fillOval(x, y, CIRCLE_DIAMETER, CIRCLE_DIAMETER);
        graphicsContext.setFill(Color.BLACK);

        //check node char, if is null it is a element with two leaves prob. sum
        if (root.getCharacter() != null) {
            graphicsContext.fillText(Integer.toString(root.getFreq()), (x + CIRCLE_RADIUS) - 5, y + CIRCLE_RADIUS - 5, 10);
            graphicsContext.setFill(Color.BLACK);
            graphicsContext.fillOval((x + (CIRCLE_RADIUS / 2.0)), (y + CIRCLE_RADIUS), 20, 20);
            graphicsContext.setFill(Color.WHITE);
            graphicsContext.fillText(root.getCharacter().toString(), (x + (CIRCLE_RADIUS / 2.0) + 5), (y + CIRCLE_DIAMETER) - 5, 10);
        } else {
            graphicsContext.fillText(Integer.toString(root.getFreq()), (x + CIRCLE_RADIUS) - 4, y + CIRCLE_RADIUS + 5, 10);
        }
    }

    private void display(TreeNode root, double x, double y, GraphicsContext graphicsContext, double leavesMargin) {

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

            display(root.getLeft(), x1 - CIRCLE_RADIUS, y1, graphicsContext, (leavesMargin / 2));
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

            display(root.getRight(), x1 - CIRCLE_RADIUS, y1, graphicsContext, (leavesMargin / 2));
        }

    }

}
