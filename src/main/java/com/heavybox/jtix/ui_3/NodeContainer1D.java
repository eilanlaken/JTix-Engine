package com.heavybox.jtix.ui_3;

public class NodeContainer1D extends NodeContainer {

    public boolean vertical = true;

    @Override
    public void setChildrenBoxes() {

    }

    @Override
    protected int getContentWidth() {
        float maxWidth = Float.NEGATIVE_INFINITY;
        if (vertical) {
            for (Node child : children) {
                // TODO.
                if (child.style.transform == Style.Transform.ABSOLUTE) continue;
            }
        }
        return 0;
    }

    @Override
    protected int getContentHeight() {
        return 0;
    }

}
