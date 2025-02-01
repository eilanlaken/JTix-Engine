package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.collections.Array;

public class NodeContainerVertical extends NodeContainer {

    public float margin = 5;

    @Override
    protected void setChildrenOffset(final Array<Node> children) {

        //float position_y = getWidth() * 0.5f + boxPaddingBottom - (boxPaddingBottom + boxPaddingTop) * 0.5f;
        float position_y = getHeight() * 0.5f - boxBorderSize - boxPaddingTop;
        System.out.println(position_y);
        for (Node child : children) {
            float child_height = child.getHeight();
            child.offsetX = boxPaddingLeft - (boxPaddingLeft + boxPaddingRight) * 0.5f;
            child.offsetY = position_y - child_height * 0.5f;
            position_y -= child_height + margin;
        }
    }

    @Override
    protected float getContentHeight(final Array<Node> children) {
        float height = 0;
        for (Node child : children) {
            height += child.getHeight();
        }
        height += Math.max(0f, margin * (children.size - 1));
        return height;
    }

}
