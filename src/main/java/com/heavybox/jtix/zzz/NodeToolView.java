package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.widgets_4.*;

public class NodeToolView extends NodeContainerVertical {

    public NodeToolView(String title, float width) {
        margin = 4;
        boxPaddingLeft = 5;
        boxPaddingRight = 5;
        boxPaddingTop = 5;
        boxPaddingBottom = 5;
        boxBorderSize = 0;
        boxWidthSizing = Sizing.STATIC;
        boxHeightSizing = Sizing.VIEWPORT;
        boxWidth = 200;
        boxHeight = 1;
        boxBackgroudColor = Color.valueOf("1D1D1D");;

        addChild(new Title(width, title));
    }

    private static class Title extends NodeContainerHorizontal {

        private NodeShapeLine lineLeftNode;
        private NodeText textNode;
        private NodeShapeLine lineRightNode;

        public Title(float width, String text) {
            margin = 4;
            boxPaddingLeft = 5;
            boxPaddingRight = 5;
            boxPaddingTop = 5;
            boxBorderSize = 0;
            boxBackgroudColor = Color.valueOf("1D1D1D");;

            lineLeftNode = new NodeShapeLine(width,2, Color.valueOf("AD1D1D"));
            textNode = new NodeText(text);
            textNode.size = 16;
            lineRightNode = new NodeShapeLine(width,2, Color.valueOf("AD1D1D"));

            addChild(lineLeftNode);
            addChild(textNode);
            addChild(lineRightNode);
        }

    }

}
