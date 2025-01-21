package com.heavybox.jtix.widgets;

public class NodeContainer1D extends NodeContainer {

    public boolean vertical = true;

    @Override
    public void setChildrenBoxes() {

    }

    @Override
    protected int getContentWidth() {
        float maxWidth = 0;
        if (vertical) {
            for (Node child : children) {
                // TODO.
                if (child.style.transform == Style.Transform.ABSOLUTE) continue;

            }
        } else {

        }
        return 0;
    }

    @Override
    protected int getContentHeight() {
        return 0;
    }

}
