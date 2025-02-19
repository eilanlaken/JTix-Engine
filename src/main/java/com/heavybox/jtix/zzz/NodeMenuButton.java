package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.widgets_4.NodeContainerHorizontal;
import com.heavybox.jtix.widgets_4.NodeText;

public class NodeMenuButton extends NodeContainerHorizontal {

    public static final int SIZE = 12;

    public NodeMenuButton(String name) {
        boxPaddingLeft = 4;
        boxPaddingRight = 4;
        boxPaddingTop = 1;
        boxPaddingBottom = 1;
        boxBackgroudColor = Color.valueOf("1D1D1D");
        boxWidthSizing = Sizing.DYNAMIC;
        boxHeightSizing = Sizing.DYNAMIC;
        boxBorderSize = 0;
        NodeText text = new NodeText(name);
        text.size = SIZE;
        addChild(text);
    }

}
