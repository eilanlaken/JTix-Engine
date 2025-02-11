package com.heavybox.jtix.zzz;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.widgets_4.Node;
import com.heavybox.jtix.widgets_4.NodeContainerHorizontal;
import com.heavybox.jtix.widgets_4.NodeImage;
import com.heavybox.jtix.widgets_4.NodeText;

public class MenuItem extends NodeContainerHorizontal {

    public boolean selected = false;

    public MenuItem(TextureRegion iconRegion, String name, String hotkey) {
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

        NodeText hotkeyNode = new NodeText(hotkey);
        hotkeyNode.size = 12;
        hotkeyNode.color = Color.valueOf("EEEEEE");

        addChild(icon);
        addChild(new NodeText(name));
        addChild(hotkeyNode);
    }

    // I want the last item to stick to the end. I can hard code it.
    @Override
    protected void setChildrenOffset(final Array<Node> children) {
        super.setChildrenOffset(children);
        Node hotkey = children.get(2);
        hotkey.offsetX = (calculateWidth() * 0.5f - boxBorderSize - boxPaddingRight - hotkey.calculateWidth() * 0.5f) * screenSclX;
    }

}
