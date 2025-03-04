package com.heavybox.jtix.collections;

import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.memory.MemoryPool;

import java.util.Arrays;

public class ArrayBoolean implements MemoryPool.Reset {

    public boolean[] items;
    public int       size;
    public boolean   ordered;

    /** Creates an ordered array with a capacity of 16. */
    public ArrayBoolean() {
        this(true, 16);
    }

    /** Creates an ordered array with the specified capacity. */
    public ArrayBoolean(int capacity) {
        this(true, capacity);
    }

    /** @param ordered If false, methods that remove elements may change the order of other elements in the array, which avoids a
     *           memory copy.
     * @param capacity Any elements added beyond this will cause the backing array to be grown. */
    public ArrayBoolean(boolean ordered, int capacity) {
        this.ordered = ordered;
        items = new boolean[capacity];
    }

    /** Creates a new array containing the elements in the specific array. The new array will be ordered if the specific array is
     * ordered. The capacity is set to the number of elements, so any subsequent elements added will cause the backing array to be
     * grown. */
    public ArrayBoolean(ArrayBoolean array) {
        this.ordered = array.ordered;
        size = array.size;
        items = new boolean[size];
        System.arraycopy(array.items, 0, items, 0, size);
    }

    /** Creates a new ordered array containing the elements in the specified array. The capacity is set to the number of elements,
     * so any subsequent elements added will cause the backing array to be grown. */
    public ArrayBoolean(float[] array) {
        this(true, array, 0, array.length);
    }

    /** Creates a new array containing the elements in the specified array. The capacity is set to the number of elements, so any
     * subsequent elements added will cause the backing array to be grown.
     * @param ordered If false, methods that remove elements may change the order of other elements in the array, which avoids a
     *           memory copy. */
    public ArrayBoolean(boolean ordered, float[] array, int startIndex, int count) {
        this(ordered, count);
        size = count;
        System.arraycopy(array, startIndex, items, 0, count);
    }

    public void add (boolean value) {
        boolean[] items = this.items;
        if (size == items.length) items = resize(Math.max(8, (int)(size * 1.75f)));
        items[size++] = value;
    }

    public void add (boolean value1, boolean value2) {
        boolean[] items = this.items;
        if (size + 1 >= items.length) items = resize(Math.max(8, (int)(size * 1.75f)));
        items[size] = value1;
        items[size + 1] = value2;
        size += 2;
    }

    public void add (boolean value1, boolean value2, boolean value3) {
        boolean[] items = this.items;
        if (size + 2 >= items.length) items = resize(Math.max(8, (int)(size * 1.75f)));
        items[size] = value1;
        items[size + 1] = value2;
        items[size + 2] = value3;
        size += 3;
    }

    public void add (boolean value1, boolean value2, boolean value3, boolean value4) {
        boolean[] items = this.items;
        if (size + 3 >= items.length) items = resize(Math.max(8, (int)(size * 1.8f))); // 1.75 isn't enough when size=5.
        items[size] = value1;
        items[size + 1] = value2;
        items[size + 2] = value3;
        items[size + 3] = value4;
        size += 4;
    }

    public void addAll (ArrayBoolean array) {
        addAll(array.items, 0, array.size);
    }

    public void addAll (ArrayBoolean array, int offset, int length) {
        if (offset + length > array.size)
            throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + array.size);
        addAll(array.items, offset, length);
    }

    public void addAll (boolean... array) {
        addAll(array, 0, array.length);
    }

    public void addAll (boolean[] array, int offset, int length) {
        boolean[] items = this.items;
        int sizeNeeded = size + length;
        if (sizeNeeded > items.length) items = resize(Math.max(Math.max(8, sizeNeeded), (int)(size * 1.75f)));
        System.arraycopy(array, offset, items, size, length);
        size += length;
    }

    public boolean get (int index) {
        if (index >= size) throw new CollectionsException("index can't be >= size: " + index + " >= " + size);
        return items[index];
    }

    public boolean getCyclic(int index) {
        if (size == 0) throw new CollectionsException(Array.class.getSimpleName() + " is empty.");
        if (index >= size) return items[index % size];
        else if (index < 0) return items[index % size + size];
        return items[index];
    }

    public void set (int index, boolean value) {
        if (index >= size) throw new CollectionsException("index can't be >= size: " + index + " >= " + size);
        items[index] = value;
    }

    public void insert (int index, boolean value) {
        if (index > size) throw new CollectionsException("index can't be > size: " + index + " > " + size);
        boolean[] items = this.items;
        if (size == items.length) items = resize(Math.max(8, (int)(size * 1.75f)));
        if (ordered)
            System.arraycopy(items, index, items, index + 1, size - index);
        else
            items[size] = items[index];
        size++;
        items[index] = value;
    }

    /** Inserts the specified number of items at the specified index. The new items will have values equal to the values at those
     * indices before the insertion. */
    public void insertRange(int index, int count) {
        if (index > size) throw new CollectionsException("index can't be > size: " + index + " > " + size);
        int sizeNeeded = size + count;
        if (sizeNeeded > items.length) items = resize(Math.max(Math.max(8, sizeNeeded), (int)(size * 1.75f)));
        System.arraycopy(items, index, items, index + count, size - index);
        size = sizeNeeded;
    }

    public void swap(int first, int second) {
        if (first >= size)  throw new CollectionsException("first can't be >= size: " + first + " >= " + size);
        if (second >= size) throw new CollectionsException("second can't be >= size: " + second + " >= " + size);
        boolean[] items = this.items;
        boolean firstValue = items[first];
        items[first] = items[second];
        items[second] = firstValue;
    }

    public boolean contains (boolean value) {
        int i = size - 1;
        boolean[] items = this.items;
        while (i >= 0)
            if (items[i--] == value) return true;
        return false;
    }

    public int indexOf (boolean value) {
        boolean[] items = this.items;
        for (int i = 0, n = size; i < n; i++)
            if (items[i] == value) return i;
        return -1;
    }

    public int lastIndexOf (boolean value) {
        boolean[] items = this.items;
        for (int i = size - 1; i >= 0; i--)
            if (items[i] == value) return i;
        return -1;
    }

    public boolean removeValue (boolean value) {
        boolean[] items = this.items;
        for (int i = 0, n = size; i < n; i++) {
            if (items[i] == value) {
                removeIndex(i);
                return true;
            }
        }
        return false;
    }

    /** Removes and returns the item at the specified index. */
    public boolean removeIndex (int index) {
        if (index >= size) throw new CollectionsException("index can't be >= size: " + index + " >= " + size);
        boolean[] items = this.items;
        boolean value = items[index];
        size--;
        if (ordered)
            System.arraycopy(items, index + 1, items, index, size - index);
        else
            items[index] = items[size];
        return value;
    }

    /** Removes the items between the specified indices, inclusive. */
    public void removeRange(int start, int end) {
        int n = size;
        if (end >= n)    throw new CollectionsException("end can't be >= size: " + end + " >= " + size);
        if (start > end) throw new CollectionsException("start can't be > end: " + start + " > " + end);
        int count = end - start + 1, lastIndex = n - count;
        if (ordered)
            System.arraycopy(items, start + count, items, start, n - (start + count));
        else {
            int i = Math.max(lastIndex, end + 1);
            System.arraycopy(items, i, items, start, n - i);
        }
        size = n - count;
    }

    public boolean removeAll(ArrayBoolean array) {
        int size = this.size;
        int startSize = size;
        boolean[] items = this.items;
        for (int i = 0, n = array.size; i < n; i++) {
            boolean item = array.get(i);
            for (int ii = 0; ii < size; ii++) {
                if (item == items[ii]) {
                    removeIndex(ii);
                    size--;
                    break;
                }
            }
        }
        return size != startSize;
    }

    /** Removes and returns the last item. */
    public boolean pop() {
        return items[--size];
    }

    /** Returns the last item. */
    public boolean peek() {
        return items[size - 1];
    }

    /** Returns the first item. */
    public boolean first() {
        if (size == 0) throw new IllegalStateException("Array is empty.");
        return items[0];
    }

    /** Returns true if the array has one or more items. */
    public boolean notEmpty() {
        return size > 0;
    }

    /** Returns true if the array is empty. */
    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        size = 0;
    }

    /** Reduces the size of the backing array to the size of the actual items. This is useful to release memory when many items
     * have been removed, or if it is known that more items will not be added.
     * @return {@link #items} */
    public boolean[] pack() {
        if (items.length != size) resize(size);
        return items;
    }

    /** Increases the size of the backing array to accommodate the specified number of additional items. Useful before adding many
     * items to avoid multiple backing array resizes.
     * @return {@link #items} */
    public boolean[] ensureCapacity(int additionalCapacity) {
        if (additionalCapacity < 0) throw new IllegalArgumentException("additionalCapacity must be >= 0: " + additionalCapacity);
        int sizeNeeded = size + additionalCapacity;
        if (sizeNeeded > items.length) resize(Math.max(Math.max(8, sizeNeeded), (int)(size * 1.75f)));
        return items;
    }

    /** Sets the array size, leaving any values beyond the current size undefined.
     * @return {@link #items} */
    public boolean[] setSize(int newSize) {
        if (newSize < 0) throw new IllegalArgumentException("newSize must be >= 0: " + newSize);
        if (newSize > items.length) resize(Math.max(8, newSize));
        size = newSize;
        return items;
    }

    protected boolean[] resize(int newSize) {
        boolean[] newItems = new boolean[newSize];
        boolean[] items = this.items;
        System.arraycopy(items, 0, newItems, 0, Math.min(size, newItems.length));
        this.items = newItems;
        return newItems;
    }

    public void reverse() {
        boolean[] items = this.items;
        for (int i = 0, lastIndex = size - 1, n = size / 2; i < n; i++) {
            int ii = lastIndex - i;
            boolean temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }

    public void reverseInPairs() {
        if (items.length % 2 != 0) throw new CollectionsException("ArrayFloat size must be even.");
        int n = this.size;
        for (int i = 0; i < n / 2; i += 2) {
            int j = n - i - 2;
            boolean temp1 = items[i];
            boolean temp2 = items[i + 1];
            items[i] = items[j];
            items[i + 1] = items[j + 1];
            items[j] = temp1;
            items[j + 1] = temp2;
        }
    }

    public void shuffle() {
        boolean[] items = this.items;
        for (int i = size - 1; i >= 0; i--) {
            int ii = MathUtils.randomUniformInt(0, i);
            boolean temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }

    /** Reduces the size of the array to the specified size. If the array is already smaller than the specified size, no action is
     * taken. */
    public void truncate (int newSize) {
        if (size > newSize) size = newSize;
    }

    /** Returns a random item from the array, or zero if the array is empty. */
    public boolean random() {
        if (size == 0) return false;
        return items[MathUtils.randomUniformInt(0, size)];
    }

    public boolean[] toArray() {
        boolean[] array = new boolean[size];
        System.arraycopy(items, 0, array, 0, size);
        return array;
    }

    @Override
    public void reset() {
        clear();
        this.ordered = true;
    }

    /** Returns false if either array is unordered. */
    @Override
    public boolean equals (Object object) {
        if (object == this) return true;
        if (!ordered) return false;
        if (!(object instanceof ArrayBoolean)) return false;
        ArrayBoolean array = (ArrayBoolean)object;
        if (!array.ordered) return false;
        int n = size;
        if (n != array.size) return false;
        boolean[] items1 = this.items, items2 = array.items;
        for (int i = 0; i < n; i++)
            if (items1[i] != items2[i]) return false;
        return true;
    }

    @Override
    public String toString() {
        if (size == 0) return "[]";
        boolean[] items = this.items;
        StringBuilder buffer = new StringBuilder(32);
        buffer.append('[');
        buffer.append(items[0]);
        for (int i = 1; i < size; i++) {
            buffer.append(", ");
            buffer.append(items[i]);
        }
        buffer.append(']');
        return buffer.toString();
    }

    static public ArrayBoolean with(float... array) {
        return new ArrayBoolean(array);
    }

}
