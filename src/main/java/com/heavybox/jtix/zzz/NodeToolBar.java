package com.heavybox.jtix.zzz;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.widgets_4.NodeContainerVertical;
import com.heavybox.jtix.widgets_4.NodeText;

public class NodeToolBar extends NodeContainerVertical {

    // state
    NodeToolsMenuItem selected = null;
    Array<NodeToolsMenuItem> menuItems = new Array<>();

    NodeToolBar() {
        TexturePack icons = Assets.get("assets/app-texture-packs/icons.yml");

        boxWidthSizing = Sizing.DYNAMIC;
        boxHeightSizing = Sizing.DYNAMIC;
        boxBorderSize = 0;
        boxPaddingTop = 10;
        margin = 0;
        boxPaddingBottom = 5;
        boxBackgroudColor = Color.valueOf("1D1D1D");
        boxBackgroundEnabled = true;

        NodeText title = new NodeText("Tools");
        title.size = 14;
        NodeToolsMenuItem select = new NodeToolsMenuItem(icons.getRegion("assets/app-icons/select.png"), "Select", "press S");
        NodeToolsMenuItem move = new NodeToolsMenuItem(icons.getRegion("assets/app-icons/move.png"), "Move", "press M");
        NodeToolsMenuItem terrain = new NodeToolsMenuItem(icons.getRegion("assets/app-icons/terrain.png"), "Terrain", "press T");
        NodeToolsMenuItem brush = new NodeToolsMenuItem(icons.getRegion("assets/app-icons/brush.png"), "Brush", "press B");
        NodeToolsMenuItem path = new NodeToolsMenuItem(icons.getRegion("assets/app-icons/path.png"), "Path", "press P");
        NodeToolsMenuItem text = new NodeToolsMenuItem(icons.getRegion("assets/app-icons/text.png"), "Text", "press T");
        NodeToolsMenuItem export = new NodeToolsMenuItem(icons.getRegion("assets/app-icons/export.png"), "Export", "press E");

        menuItems.add(select);
        menuItems.add(move);
        menuItems.add(terrain);
        menuItems.add(brush);
        menuItems.add(path);
        menuItems.add(text);
        menuItems.add(export);

        addChild(title);
        addChild(select);
        addChild(move);
        addChild(terrain);
        addChild(brush);
        addChild(path);
        addChild(text);
        addChild(export);

        addChild(new NodeToolView("select", 75));
    }

    protected void select(NodeToolsMenuItem item) {
        selected = item;
        item.boxBackgroudColor = NodeToolsMenuItem.COLOR_SELECTED;
        for (NodeToolsMenuItem menuItem : menuItems) {
            if (selected == menuItem) continue;
            menuItem.boxBackgroudColor = NodeToolsMenuItem.COLOR_UNSELECTED;
        }
    }

}
