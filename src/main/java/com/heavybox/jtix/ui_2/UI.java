package com.heavybox.jtix.ui_2;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.memory.MemoryPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

// TODO: add: polygons, rectangles with rounded corners, circles with refinement
public final class UI {

    private static final char[] TEXT_WRAP_SPLIT_CHARS = new char[] { ' ', '-', '\t' };

    private static final Pattern ONE_OR_MORE_SPACES = Pattern.compile("\\s+"); // Match one or more spaces

    private static int widgetsCount = 0;
    public  static boolean debug = true;

    private static final MemoryPool<Vector2> vectors2Pool = new MemoryPool<>(Vector2.class, 4);

    public static int getID() {
        widgetsCount++;
        return widgetsCount - 1;
    }

    public static Region.Polygon regionCreateRectangle(float width, float height) {
        float[] rectangle = new float[] {
          -width, -height,
           width, -height,
           width,  height,
          -width,  height,
        };
        return new Region.Polygon(rectangle);
    }

    public static void regionSetToRectangle(float width, float height, Region.Polygon out) {
        out.pointsOriginal.clear();
        out.pointsTransformed.clear();

        out.pointsOriginal.add(-width, -height);
        out.pointsOriginal.add( width, -height);
        out.pointsOriginal.add( width,  height);
        out.pointsOriginal.add(-width,  height);

        out.pointsOriginal.pack();
        out.pointsTransformed.addAll(out.pointsOriginal);
        out.pointsTransformed.pack();
    }

    public static Region.Polygon regionCreateRectangle(float width, float height, float cornerRadius, int refinement) {
        refinement = Math.max(2, refinement);

        float widthHalf  = width  * 0.5f;
        float heightHalf = height * 0.5f;
        float da = 90.0f / (refinement - 1);
        float[] rectangleRC = new float[refinement * 4 * 2]; // round corners rectangle: 4 corners, 2 components for each vertex (x, y) and 'refinement' vertices for every corner

        Vector2 corner = vectors2Pool.allocate();
        int index = 0;
        // add upper left corner vertices
        for (int i = 0; i < refinement; i++) {
            corner.set(-cornerRadius, 0);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(-widthHalf + cornerRadius, heightHalf - cornerRadius);
            rectangleRC[index] = corner.x;
            rectangleRC[index + 1] = corner.y;
            index += 2;
        }

        // add upper right corner vertices
        for (int i = 0; i < refinement; i++) {
            corner.set(0, cornerRadius);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(widthHalf - cornerRadius, heightHalf - cornerRadius);
            rectangleRC[index] = corner.x;
            rectangleRC[index + 1] = corner.y;
            index += 2;
        }

        // add lower right corner vertices
        for (int i = 0; i < refinement; i++) {
            corner.set(cornerRadius, 0);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(widthHalf - cornerRadius, -heightHalf + cornerRadius);
            rectangleRC[index] = corner.x;
            rectangleRC[index + 1] = corner.y;
            index += 2;
        }

        // add lower left corner vertices
        for (int i = 0; i < refinement; i++) {
            corner.set(0, -cornerRadius);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(-widthHalf + cornerRadius, -heightHalf + cornerRadius);
            rectangleRC[index] = corner.x;
            rectangleRC[index + 1] = corner.y;
            index += 2;
        }

        vectors2Pool.free(corner);
        return new Region.Polygon(rectangleRC);
    }

    public static Region.Polygon regionCreateRectangle(float width, float height,
                                                       float cornerRadiusTopLeft, int refinementTopLeft,
                                                       float cornerRadiusTopRight, int refinementTopRight,
                                                       float cornerRadiusBottomRight, int refinementBottomRight,
                                                       float cornerRadiusBottomLeft, int refinementBottomLeft) {

        refinementTopLeft = Math.max(2, refinementTopLeft);
        refinementTopRight = Math.max(2, refinementTopRight);
        refinementBottomRight = Math.max(2, refinementBottomRight);
        refinementBottomLeft = Math.max(2, refinementBottomLeft);

        int totalRefinement = refinementTopLeft + refinementTopRight
                + refinementBottomRight + refinementBottomLeft;

        float widthHalf  = width  * 0.5f;
        float heightHalf = height * 0.5f;
        float da = 90.0f / (refinementTopLeft - 1);
        float[] rectangleRC = new float[totalRefinement * 2]; // round corners rectangle: 4 corners, 2 components for each vertex (x, y) and 'refinement' vertices for every corner

        Vector2 corner = vectors2Pool.allocate();
        int index = 0;
        // add upper left corner vertices
        for (int i = 0; i < refinementTopLeft; i++) {
            corner.set(-cornerRadiusTopLeft, 0);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(-widthHalf + cornerRadiusTopLeft,heightHalf - cornerRadiusTopLeft);
            rectangleRC[index] = corner.x;
            rectangleRC[index + 1] = corner.y;
            index += 2;
        }

        // add upper right corner vertices
        for (int i = 0; i < refinementTopRight; i++) {
            corner.set(0, cornerRadiusTopRight);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(widthHalf - cornerRadiusTopRight, heightHalf - cornerRadiusTopRight);
            rectangleRC[index] = corner.x;
            rectangleRC[index + 1] = corner.y;
            index += 2;
        }

        // add lower right corner vertices
        for (int i = 0; i < refinementBottomRight; i++) {
            corner.set(cornerRadiusBottomRight, 0);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(widthHalf - cornerRadiusBottomRight, -heightHalf + cornerRadiusBottomRight);
            rectangleRC[index] = corner.x;
            rectangleRC[index + 1] = corner.y;
            index += 2;
        }

        // add lower left corner vertices
        for (int i = 0; i < refinementBottomLeft; i++) {
            corner.set(0, -cornerRadiusBottomLeft);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(-widthHalf + cornerRadiusBottomLeft, -heightHalf + cornerRadiusBottomLeft);
            rectangleRC[index] = corner.x;
            rectangleRC[index + 1] = corner.y;
            index += 2;
        }

        vectors2Pool.free(corner);
        return new Region.Polygon(rectangleRC);
    }

    public static Region.Polygon regionCreateCircle(float r, int refinement) {
        refinement = Math.max(refinement, 3);

        float[] circle = new float[refinement * 2];
        float da = 360f / refinement;
        for (int i = 0; i < refinement; i++) {
            circle[2*i]     = r * MathUtils.cosDeg(da * i);
            circle[2*i + 1] = r * MathUtils.sinDeg(da * i);
        }

        return new Region.Polygon(circle);
    }





    public static void calculateLineBreakdown(final String line, final float boundaryWidth, final Style style, Array<String> out) {
        out.clear();
        ArrayInt splitPoints = new ArrayInt();
        calculateLineWrapIndices(line, boundaryWidth, style, splitPoints);

        if (splitPoints.size == 0) {
            out.add(line);
            return;
        }

        int start = 0;
        for (int i = 0; i < splitPoints.size; i++) {
            int index = splitPoints.get(i);
            // Ensure the index is valid and does not exceed the string length
            if (index < 0 || index > line.length()) {
                throw new IllegalArgumentException("Index out of bounds: " + index);
            }
            // Split from the current start to the current index
            out.add(line.substring(start, index));
            start = index; // Move the start to the next position after the last split
        }

        // Add the remaining part of the string after the last index
        if (start < line.length()) {
            out.add(line.substring(start));
        }
    }

    public static void calculateLineWrapIndices(final String line, float boundaryWidth, final Style style, ArrayInt out) {
        out.clear();
        //calculateStringWrapIndices(str,boundaryWidth, style, 0, out);
        boundaryWidth = boundaryWidth - style.fontSize;
        int currentIndex = 0;

        while (currentIndex < line.length()) {
            float currentWidth = Renderer2D.calculateStringLineWidth(line, currentIndex, line.length(), style.font, style.fontSize, style.fontAntialiasing);

            int firstWhitespaceIndex = -1;
            for (int i = currentIndex; i < line.length(); i++ ){
                if (Character.isWhitespace(line.charAt(i))){
                    firstWhitespaceIndex = i;
                    break;
                }
            }

            boolean containsWhiteSpace = firstWhitespaceIndex != -1;
            if (currentWidth <= boundaryWidth) break; // we don't need to break the String
            if (!containsWhiteSpace) break; // we can't break the String

            // string overflows, we need to find a suitable index to split the String line.
            int splitIndex = line.length() - 1;
            for (int i = line.length() - 1; i > currentIndex; i--) {
                char c = line.charAt(i);
                if (!Character.isWhitespace(c)) continue;
                float remainderWidth = Renderer2D.calculateStringLineWidth(line, currentIndex, line.length(), style.font, style.fontSize, style.fontAntialiasing);
                if (currentWidth - remainderWidth <= boundaryWidth) { // found the split-index
                    splitIndex = i;
                    break;
                }
            }

            if (splitIndex == line.length() - 1) { // we could not split the string
                splitIndex = firstWhitespaceIndex;
            }
            if (splitIndex == currentIndex) return;
            currentIndex = splitIndex;
            out.add(currentIndex);
        }
    }


    /*
    SpaceLeft := LineWidth
for each Word in Text
    if (Width(Word) + SpaceWidth) > SpaceLeft
        insert line break before Word in Text
        SpaceLeft := LineWidth - Width(Word)
    else
        SpaceLeft := SpaceLeft - (Width(Word) + SpaceWidth)
     */
    public static void wordWrap(final String line, float boundaryWidth, final Style style) {
        String trimmed = line.trim();
        String[] words = trimmed.split("\\s+");
        int[] wordStartIndices = new int[words.length];
        int tracker = 0; // To track the current position in the string
        for (int i = 0; i < words.length; i++) {
            // Find the index of the current word starting from the current position
            tracker = trimmed.indexOf(words[i], tracker);
            wordStartIndices[i] = tracker;
            tracker += words[i].length(); // Move the index past the current word
        }
        final float space_width = Renderer2D.calculateStringLineWidth(" ", 0, 1, style.font, style.fontSize, style.fontAntialiasing);
        StringBuilder builder = new StringBuilder(trimmed);
        ArrayInt linebreakIndices = new ArrayInt();

        float spaceLeft = boundaryWidth;
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            float width = Renderer2D.calculateStringLineWidth(word, 0, word.length(), style.font, style.fontSize, style.fontAntialiasing);
            if (width + space_width > spaceLeft) { // overflow
                linebreakIndices.add(wordStartIndices[i]);
                spaceLeft = boundaryWidth - width;
            } else {
                spaceLeft = spaceLeft - (width + space_width);
            }
        }

        // Insert '\n' at each index, shifting as we go
        for (int i = 0; i < linebreakIndices.size; i++) {
            int adjustedIndex = linebreakIndices.get(i) + i; // Account for previous insertions shifting the string
            if (adjustedIndex <= builder.length()) {
                builder.insert(adjustedIndex, '\n');
            } else {
                throw new IllegalArgumentException("Index out of bounds: " + linebreakIndices.get(i));
            }
        }

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.ANY_KEY)) {
            System.out.println(builder);
        }

    }


}
