package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.collections.Array;

public class NodeContainerVertical extends NodeContainer {

    public float margin = 5;

    @Override
    protected void setChildrenLayout(final Array<Node> children) {
        float position_y = screenY + getContentHeight(children) * 0.5f;
        for (Node child : children) {
            float child_height = child.getHeight();
            child.refZIndex = screenZIndex;
            child.refOffsetX = boxPaddingLeft - (boxPaddingLeft + boxPaddingRight) * 0.5f;
            child.refOffsetY = position_y - child_height * 0.5f + boxPaddingBottom - (boxPaddingBottom + boxPaddingTop) * 0.5f;
            child.refX = screenX;// + boxPaddingLeft - (boxPaddingLeft + boxPaddingRight) * 0.5f;;
            child.refY = screenY;
            child.refDeg = screenDeg;
            child.refSclX = screenSclX;
            child.refSclY = screenSclY;
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
