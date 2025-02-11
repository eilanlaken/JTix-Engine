package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.widgets_4.NodeContainerHorizontal;
import com.heavybox.jtix.widgets_4.NodeImage;
import com.heavybox.jtix.widgets_4.NodeText;

public class ToolButton extends NodeContainerHorizontal {

    public boolean selected = false;

    public ToolButton(TextureRegion iconRegion, String name, String hotkey) {
        boxHeightSizing = Sizing.DYNAMIC;
        boxWidthSizing = Sizing.STATIC;
        boxWidth = 200;
        boxBorderSize = 0;
        boxPaddingLeft = 10;
        boxPaddingRight = 10;
        boxPaddingTop = 5;
        boxPaddingBottom = 5;
        margin = 15;
        boxBackgroudColor = Color.valueOf("1D1D1D");

        NodeImage icon = new NodeImage(iconRegion);
        icon.resizeX = 0.25f;
        icon.resizeY = 0.25f;
        addChild(icon);
        addChild(new NodeText(name));

        NodeText hotkeyNode = new NodeText(hotkey);
        hotkeyNode.size = 14;
        hotkeyNode.color = Color.valueOf("EEEEEE");
        addChild(hotkeyNode);
    }

}
