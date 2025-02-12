package com.heavybox.jtix.zzz;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.widgets_4.NodeContainer;
import com.heavybox.jtix.widgets_4.NodeContainerVertical;
import com.heavybox.jtix.widgets_4.NodeText;

public class ToolBar extends NodeContainerVertical {

    // state
    ToolsMenuItem selected = null;
    Array<ToolsMenuItem> menuItems = new Array<>();

    ToolBar() {
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
        ToolsMenuItem select = new ToolsMenuItem(icons.getRegion("assets/app-icons/select.png"), "Select", "press S");
        ToolsMenuItem move = new ToolsMenuItem(icons.getRegion("assets/app-icons/move.png"), "Move", "press M");
        ToolsMenuItem terrain = new ToolsMenuItem(icons.getRegion("assets/app-icons/terrain.png"), "Terrain", "press T");
        ToolsMenuItem brush = new ToolsMenuItem(icons.getRegion("assets/app-icons/brush.png"), "Brush", "press B");
        ToolsMenuItem path = new ToolsMenuItem(icons.getRegion("assets/app-icons/path.png"), "Path", "press P");
        ToolsMenuItem text = new ToolsMenuItem(icons.getRegion("assets/app-icons/text.png"), "Text", "press T");
        ToolsMenuItem export = new ToolsMenuItem(icons.getRegion("assets/app-icons/export.png"), "Export", "press E");

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

        addChild(new ToolView("select", 75));
    }

    protected void select(ToolsMenuItem item) {
        selected = item;
        item.boxBackgroudColor = ToolsMenuItem.COLOR_SELECTED;
        for (ToolsMenuItem menuItem : menuItems) {
            if (selected == menuItem) continue;
            menuItem.boxBackgroudColor = ToolsMenuItem.COLOR_UNSELECTED;
        }
    }

}
