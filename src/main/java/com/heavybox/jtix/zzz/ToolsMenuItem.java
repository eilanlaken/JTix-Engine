package com.heavybox.jtix.zzz;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.widgets_4.Node;
import com.heavybox.jtix.widgets_4.NodeContainerHorizontal;
import com.heavybox.jtix.widgets_4.NodeImage;
import com.heavybox.jtix.widgets_4.NodeText;

public class ToolsMenuItem extends NodeContainerHorizontal {

    public static final Color COLOR_UNSELECTED = Color.valueOf("1D1D1D");
    public static final Color COLOR_SELECTED = Color.RED;
    public static final Color TEXT_COLOR_SELECTED = Color.WHITE;
    public static final Color TEXT_COLOR_UNSELECTED = Color.WHITE;

    protected NodeImage icon;
    protected NodeText nameNode;
    protected NodeText hotkeyNode;

    public ToolsMenuItem(TextureRegion iconRegion, String name, String hotkey) {
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

        icon = new NodeImage(iconRegion);
        icon.resizeX = 0.25f;
        icon.resizeY = 0.25f;

        nameNode = new NodeText(name);
        nameNode.size = 15;

        hotkeyNode = new NodeText(hotkey);
        hotkeyNode.size = 12;
        hotkeyNode.color = Color.valueOf("EEEEEE");

        addChild(icon);
        addChild(nameNode);
        addChild(hotkeyNode);

        onClick = () -> {
            ToolBar toolBar = (ToolBar) container;
            toolBar.select(this);
        };

        onMouseOver = () -> {
            ToolBar toolBar = (ToolBar) container;
            if (toolBar.selected == this) return;
            boxBackgroudColor = Color.valueOf("2D2D4D");
        };

        onMouseOut = () -> {
            ToolBar toolBar = (ToolBar) container;
            if (toolBar.selected == this) return;
            boxBackgroudColor = Color.valueOf("1D1D1D");
        };
    }

    // I want the last item to stick to the end. I can hard code it.
    @Override
    protected void setChildrenOffset(final Array<Node> children) {
        super.setChildrenOffset(children);
        Node hotkey = children.get(2);
        hotkey.offsetX = (calculateWidth() * 0.5f - boxBorderSize - boxPaddingRight - hotkey.calculateWidth() * 0.5f) * screenSclX;
    }

}
