package pl.pitera;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;


public class HuffmanTreeView {

    private static final int CIRCLE_DIAMETER = 40;
    private static final int BRANCH_ANGLE = 60;
    private static final int CIRCLE_RADIUS = CIRCLE_DIAMETER / 2;

    public AnchorPane drawTree(TreeNode tree) {

        final AnchorPane treeContainer = new AnchorPane();
        int treeHeight = TreeNode.getHeight(tree);

        //init tree position
        double maxBranchSize = (Math.pow(2, treeHeight - 1) * CIRCLE_RADIUS) / 2;
        double initialMarginBetweenItems = maxBranchSize + 10;
        double initialTreePositionX = 2 * maxBranchSize;
        double initialTreePositionY = 50;

        double maxPaneTreeWidth = (maxBranchSize * 4) + CIRCLE_DIAMETER;
        double maxPaneTreeHeight = (treeHeight * CIRCLE_DIAMETER) + ((treeHeight - 1) * BRANCH_ANGLE) + initialTreePositionY;

        treeContainer.setMaxHeight(maxPaneTreeHeight);
        treeContainer.setMaxWidth(maxPaneTreeWidth);

        drawBranch(tree, initialTreePositionX, initialTreePositionY, treeContainer, initialMarginBetweenItems);

        return treeContainer;

    }

    private void drawCircle(TreeNode root, double x, double y, AnchorPane treeAnchorPane) {

        //tree element
        Circle circle = new Circle(CIRCLE_RADIUS);
        circle.setStroke(Color.TRANSPARENT);
        circle.setFill(Color.valueOf("#d6d4ff"));
        circle.setLayoutX((x + (CIRCLE_RADIUS / 2.0)));
        circle.setLayoutY((y + CIRCLE_RADIUS));

        Label freqLabel = new Label();
        freqLabel.setText(Integer.toString(root.getFreq()));

        treeAnchorPane.getChildren().add(circle);
        treeAnchorPane.getChildren().add(freqLabel);
        freqLabel.setLayoutX(x + 5);

        //check node char, if is null it is a element with two leaves prob. sum
        if (root.getCharacter() != null) {

            freqLabel.setLayoutY(y);

            String characterValue = root.getCharacter().toString();
            Label characterLabel = new Label(characterValue);

            if (characterValue.equals(" ")) {
                characterLabel.setText("spc");
            }

            if (characterValue.equals("\n")) {
                characterLabel.setText("\\n");
            }

            if (characterValue.equals("\t")) {
                characterLabel.setText("tab");
            }

            Line lineInCircle = new Line();
            lineInCircle.setStartX(x - 5);
            lineInCircle.setStartY(y + 20);
            lineInCircle.setEndX(x + CIRCLE_RADIUS + 5);
            lineInCircle.setEndY(y + 20);

            characterLabel.setLayoutX(x + 5);
            characterLabel.setLayoutY(y + 20);

            treeAnchorPane.getChildren().add(lineInCircle);
            treeAnchorPane.getChildren().add(characterLabel);


        } else {
            freqLabel.setText(Integer.toString(root.getFreq()));
            freqLabel.setLayoutY(y + 10);
        }


    }

    private void drawBranch(TreeNode root, double x, double y, AnchorPane graphicsContext, double leavesMargin) {

        drawCircle(root, x, y, graphicsContext);

        double x0 = x + CIRCLE_RADIUS;
        double y0 = y + CIRCLE_DIAMETER;
        double x1;
        double y1 = y0 + BRANCH_ANGLE;
        double branchMarkPositionX;
        double branchMarkPositionY;

        if (root.getLeftChildren() != null) {

            x1 = x0 - leavesMargin;
            branchMarkPositionX = ((x0 + x1) / 2) - 20;
            branchMarkPositionY = ((y1 + y0) / 2) - 15;

            setBranchSignature(graphicsContext, x0, y0, x1, y1, branchMarkPositionX, branchMarkPositionY, new Label("0"));

            drawBranch(root.getLeftChildren(), x1 - CIRCLE_RADIUS, y1, graphicsContext, (leavesMargin / 2));
        }

        if (root.getRightChildren() != null) {

            x1 = x0 + leavesMargin;
            branchMarkPositionX = ((x1 + x0) / 2) - 10;
            branchMarkPositionY = ((y1 + y0) / 2) - 15;

            setBranchSignature(graphicsContext, x0, y0, x1, y1, branchMarkPositionX, branchMarkPositionY, new Label("1"));

            drawBranch(root.getRightChildren(), x1 - CIRCLE_RADIUS, y1, graphicsContext, (leavesMargin / 2));
        }

    }

    private void setBranchSignature(AnchorPane graphicsContext, double x0, double y0, double x1, double y1,
                                    double branchMarkPositionX, double branchMarkPositionY, Label branchSignatureLabel) {
        branchSignatureLabel.setLayoutX(branchMarkPositionX);
        branchSignatureLabel.setLayoutY(branchMarkPositionY);
        graphicsContext.getChildren().add(branchSignatureLabel);

        Line branchLine = new Line();
        branchLine.setStartX(x0 - 10);
        branchLine.setStartY(y0);

        branchLine.setEndX(x1 - 10);
        branchLine.setEndY(y1);
        graphicsContext.getChildren().add(branchLine);
    }

}

