package com.heavybox.jtix.collections;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MapObjectInt<K> implements Iterable<MapObjectInt.Entry<K>> {

    public int size;
    protected K[] keyTable;
    protected int[] valueTable;
    protected float loadFactor;
    protected int threshold;
    protected int shift;
    protected int mask;
    transient Entries entries1, entries2;
    transient Values values1, values2;
    transient Keys keys1, keys2;

    /** Creates a new map with an initial capacity of 51 and a load factor of 0.8. */
    public MapObjectInt() {
        this(51, 0.8f);
    }

    /** Creates a new map with a load factor of 0.8.
     * @param initialCapacity The backing array size is initialCapacity / loadFactor, increased to the next power of two. */
    public MapObjectInt(int initialCapacity) {
        this(initialCapacity, 0.8f);
    }

    /** Creates a new map with the specified initial capacity and load factor. This map will hold initialCapacity items before
     * growing the backing table.
     * @param initialCapacity The backing array size is initialCapacity / loadFactor, increased to the next power of two. */
    public MapObjectInt(int initialCapacity, float loadFactor) {
        if (loadFactor <= 0f || loadFactor >= 1f)
            throw new IllegalArgumentException("loadFactor must be > 0 and < 1: " + loadFactor);
        this.loadFactor = loadFactor;

        int tableSize = Collections.tableSize(initialCapacity, loadFactor);
        threshold = (int)(tableSize * loadFactor);
        mask = tableSize - 1;
        shift = Long.numberOfLeadingZeros(mask);

        keyTable = (K[])new Object[tableSize];
        valueTable = new int[tableSize];
    }

    /** Creates a new map identical to the specified map. */
    public MapObjectInt(MapObjectInt<? extends K> map) {
        this((int)(map.keyTable.length * map.loadFactor), map.loadFactor);
        System.arraycopy(map.keyTable, 0, keyTable, 0, map.keyTable.length);
        System.arraycopy(map.valueTable, 0, valueTable, 0, map.valueTable.length);
        size = map.size;
    }

    /** Returns an index >= 0 and <= {@link #mask} for the specified {@code item}.
     * <p>
     * The default implementation uses Fibonacci hashing on the item's {@link Object#hashCode()}: the hashcode is multiplied by a
     * long constant (2 to the 64th, divided by the golden ratio) then the uppermost bits are shifted into the lowest positions to
     * obtain an index in the desired range. Multiplication by a long may be slower than int (eg on GWT) but greatly improves
     * rehashing, allowing even very poor hashcodes, such as those that only differ in their upper bits, to be used without high
     * collision rates. Fibonacci hashing has increased collision rates when all or most hashcodes are multiples of larger
     * Fibonacci numbers (see <a href=
     * "https://probablydance.com/2018/06/16/fibonacci-hashing-the-optimization-that-the-world-forgot-or-a-better-alternative-to-integer-modulo/">Malte
     * Skarupke's blog post</a>).
     * <p>
     * This method can be overriden to customizing hashing. This may be useful eg in the unlikely event that most hashcodes are
     * Fibonacci numbers, if keys provide poor or incorrect hashcodes, or to simplify hashing if keys provide high quality
     * hashcodes and don't need Fibonacci hashing: {@code return item.hashCode() & mask;} */
    protected int place(K item) {
        return (int)(item.hashCode() * 0x9E3779B97F4A7C15L >>> shift);
    }

    /** Returns the index of the key if already present, else -(index + 1) for the next empty index. This can be overridden in this
     * pacakge to compare for equality differently than {@link Object#equals(Object)}. */
    int locateKey(K key) {
        if (key == null) throw new IllegalArgumentException("key cannot be null.");
        K[] keyTable = this.keyTable;
        for (int i = place(key);; i = i + 1 & mask) {
            K other = keyTable[i];
            if (other == null) return -(i + 1); // Empty space is available.
            if (other.equals(key)) return i; // Same key was found.
        }
    }

    public void put(K key, int value) {
        int i = locateKey(key);
        if (i >= 0) { // Existing key was found.
            valueTable[i] = value;
            return;
        }
        i = -(i + 1); // Empty space was found.
        keyTable[i] = key;
        valueTable[i] = value;
        if (++size >= threshold) resize(keyTable.length << 1);
    }

    /** Returns the old value associated with the specified key, or the specified default value. */
    public int put(K key, int value, int defaultValue) {
        int i = locateKey(key);
        if (i >= 0) { // Existing key was found.
            int oldValue = valueTable[i];
            valueTable[i] = value;
            return oldValue;
        }
        i = -(i + 1); // Empty space was found.
        keyTable[i] = key;
        valueTable[i] = value;
        if (++size >= threshold) resize(keyTable.length << 1);
        return defaultValue;
    }

    public void putAll(MapObjectInt<? extends K> map) {
        ensureCapacity(map.size);
        K[] keyTable = map.keyTable;
        int[] valueTable = map.valueTable;
        K key;
        for (int i = 0, n = keyTable.length; i < n; i++) {
            key = keyTable[i];
            if (key != null) put(key, valueTable[i]);
        }
    }

    /** Skips checks for existing keys, doesn't increment size. */
    private void putResize(K key, int value) {
        K[] keyTable = this.keyTable;
        for (int i = place(key);; i = (i + 1) & mask) {
            if (keyTable[i] == null) {
                keyTable[i] = key;
                valueTable[i] = value;
                return;
            }
        }
    }

    /** Returns the value for the specified key, or the default value if the key is not in the map. */
    public int get(K key, int defaultValue) {
        int i = locateKey(key);
        return i < 0 ? defaultValue : valueTable[i];
    }

    /** Returns the key's current value and increments the stored value. If the key is not in the map, defaultValue + increment is
     * put into the map and defaultValue is returned. */
    public int getAndIncrement(K key, int defaultValue, int increment) {
        int i = locateKey(key);
        if (i >= 0) { // Existing key was found.
            int oldValue = valueTable[i];
            valueTable[i] += increment;
            return oldValue;
        }
        i = -(i + 1); // Empty space was found.
        keyTable[i] = key;
        valueTable[i] = defaultValue + increment;
        if (++size >= threshold) resize(keyTable.length << 1);
        return defaultValue;
    }

    /** Returns the value for the removed key, or the default value if the key is not in the map. */
    public int remove(K key, int defaultValue) {
        int i = locateKey(key);
        if (i < 0) return defaultValue;
        K[] keyTable = this.keyTable;
        int[] valueTable = this.valueTable;
        int oldValue = valueTable[i];
        int mask = this.mask, next = i + 1 & mask;
        while ((key = keyTable[next]) != null) {
            int placement = place(key);
            if ((next - placement & mask) > (i - placement & mask)) {
                keyTable[i] = key;
                valueTable[i] = valueTable[next];
                i = next;
            }
            next = next + 1 & mask;
        }
        keyTable[i] = null;
        size--;
        return oldValue;
    }

    /** Returns true if the map has one or more items. */
    public boolean notEmpty() {
        return size > 0;
    }

    /** Returns true if the map is empty. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Reduces the size of the backing arrays to be the specified capacity / loadFactor, or less. If the capacity is already less,
     * nothing is done. If the map contains more items than the specified capacity, the next highest power of two capacity is used
     * instead. */
    public void shrink(int maximumCapacity) {
        if (maximumCapacity < 0) throw new IllegalArgumentException("maximumCapacity must be >= 0: " + maximumCapacity);
        int tableSize = Collections.tableSize(maximumCapacity, loadFactor);
        if (keyTable.length > tableSize) resize(tableSize);
    }

    /** Clears the map and reduces the size of the backing arrays to be the specified capacity / loadFactor, if they are larger. */
    public void clear(int maximumCapacity) {
        int tableSize = Collections.tableSize(maximumCapacity, loadFactor);
        if (keyTable.length <= tableSize) {
            clear();
            return;
        }
        size = 0;
        resize(tableSize);
    }

    public void clear() {
        if (size == 0) return;
        size = 0;
        Arrays.fill(keyTable, null);
    }

    /** Returns true if the specified value is in the map. Note this traverses the entire map and compares every value, which may
     * be an expensive operation. */
    public boolean containsValue(int value) {
        K[] keyTable = this.keyTable;
        int[] valueTable = this.valueTable;
        for (int i = valueTable.length - 1; i >= 0; i--)
            if (keyTable[i] != null && valueTable[i] == value) return true;
        return false;
    }

    public boolean containsKey(K key) {
        return locateKey(key) >= 0;
    }

    /** Returns the key for the specified value, or null if it is not in the map. Note this traverses the entire map and compares
     * every value, which may be an expensive operation. */
    public K findKey(int value) {
        K[] keyTable = this.keyTable;
        int[] valueTable = this.valueTable;
        for (int i = valueTable.length - 1; i >= 0; i--) {
            K key = keyTable[i];
            if (key != null && valueTable[i] == value) return key;
        }
        return null;
    }

    /** Increases the size of the backing array to accommodate the specified number of additional items / loadFactor. Useful before
     * adding many items to avoid multiple backing array resizes. */
    public void ensureCapacity (int additionalCapacity) {
        int tableSize = Collections.tableSize(size + additionalCapacity, loadFactor);
        if (keyTable.length < tableSize) resize(tableSize);
    }

    final void resize(int newSize) {
        int oldCapacity = keyTable.length;
        threshold = (int)(newSize * loadFactor);
        mask = newSize - 1;
        shift = Long.numberOfLeadingZeros(mask);

        K[] oldKeyTable = keyTable;
        int[] oldValueTable = valueTable;

        keyTable = (K[])new Object[newSize];
        valueTable = new int[newSize];

        if (size > 0) {
            for (int i = 0; i < oldCapacity; i++) {
                K key = oldKeyTable[i];
                if (key != null) putResize(key, oldValueTable[i]);
            }
        }
    }

    public int hashCode() {
        int h = size;
        K[] keyTable = this.keyTable;
        int[] valueTable = this.valueTable;
        for (int i = 0, n = keyTable.length; i < n; i++) {
            K key = keyTable[i];
            if (key != null) h += key.hashCode() + valueTable[i];
        }
        return h;
    }

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof MapObjectInt)) return false;
        MapObjectInt other = (MapObjectInt)obj;
        if (other.size != size) return false;
        K[] keyTable = this.keyTable;
        int[] valueTable = this.valueTable;
        for (int i = 0, n = keyTable.length; i < n; i++) {
            K key = keyTable[i];
            if (key != null) {
                int otherValue = other.get(key, 0);
                if (otherValue == 0 && !other.containsKey(key)) return false;
                if (otherValue != valueTable[i]) return false;
            }
        }
        return true;
    }

    public String toString(String separator) {
        return toString(separator, false);
    }

    public String toString() {
        return toString(", ", true);
    }

    private String toString(String separator, boolean braces) {
        if (size == 0) return braces ? "{}" : "";
        java.lang.StringBuilder buffer = new java.lang.StringBuilder(32);
        if (braces) buffer.append('{');
        K[] keyTable = this.keyTable;
        int[] valueTable = this.valueTable;
        int i = keyTable.length;
        while (i-- > 0) {
            K key = keyTable[i];
            if (key == null) continue;
            buffer.append(key);
            buffer.append('=');
            buffer.append(valueTable[i]);
            break;
        }
        while (i-- > 0) {
            K key = keyTable[i];
            if (key == null) continue;
            buffer.append(separator);
            buffer.append(key);
            buffer.append('=');
            buffer.append(valueTable[i]);
        }
        if (braces) buffer.append('}');
        return buffer.toString();
    }

    public Entries<K> iterator() {
        return entries();
    }

    public Entries<K> entries() {
        if (entries1 == null) {
            entries1 = new Entries(this);
            entries2 = new Entries(this);
        }
        if (!entries1.valid) {
            entries1.reset();
            entries1.valid = true;
            entries2.valid = false;
            return entries1;
        }
        entries2.reset();
        entries2.valid = true;
        entries1.valid = false;
        return entries2;
    }

    public Values values() {
        if (values1 == null) {
            values1 = new Values(this);
            values2 = new Values(this);
        }
        if (!values1.valid) {
            values1.reset();
            values1.valid = true;
            values2.valid = false;
            return values1;
        }
        values2.reset();
        values2.valid = true;
        values1.valid = false;
        return values2;
    }

    public Keys<K> keys() {
        if (keys1 == null) {
            keys1 = new Keys(this);
            keys2 = new Keys(this);
        }
        if (!keys1.valid) {
            keys1.reset();
            keys1.valid = true;
            keys2.valid = false;
            return keys1;
        }
        keys2.reset();
        keys2.valid = true;
        keys1.valid = false;
        return keys2;
    }

    public static class Entry<K> {
        public K key;
        public int value;

        public String toString() {
            return key + "=" + value;
        }
    }

    private static class MapIterator<K> {
        public boolean hasNext;

        final MapObjectInt<K> map;
        int nextIndex, currentIndex;
        boolean valid = true;

        public MapIterator(MapObjectInt<K> map) {
            this.map = map;
            reset();
        }

        public void reset() {
            currentIndex = -1;
            nextIndex = -1;
            findNextIndex();
        }

        void findNextIndex() {
            K[] keyTable = map.keyTable;
            for (int n = keyTable.length; ++nextIndex < n;) {
                if (keyTable[nextIndex] != null) {
                    hasNext = true;
                    return;
                }
            }
            hasNext = false;
        }

        public void remove() {
            int i = currentIndex;
            if (i < 0) throw new IllegalStateException("next must be called before remove.");
            K[] keyTable = map.keyTable;
            int[] valueTable = map.valueTable;
            int mask = map.mask, next = i + 1 & mask;
            K key;
            while ((key = keyTable[next]) != null) {
                int placement = map.place(key);
                if ((next - placement & mask) > (i - placement & mask)) {
                    keyTable[i] = key;
                    valueTable[i] = valueTable[next];
                    i = next;
                }
                next = next + 1 & mask;
            }
            keyTable[i] = null;
            map.size--;
            if (i != currentIndex) --nextIndex;
            currentIndex = -1;
        }
    }

    static public class Entries<K> extends MapIterator<K> implements Iterable<Entry<K>>, Iterator<Entry<K>> {
        Entry<K> entry = new Entry<K>();

        public Entries(MapObjectInt<K> map) {
            super(map);
        }

        public Entry<K> next() {
            if (!hasNext) throw new NoSuchElementException();
            if (!valid) throw new RuntimeException("#iterator() cannot be used nested.");
            K[] keyTable = map.keyTable;
            entry.key = keyTable[nextIndex];
            entry.value = map.valueTable[nextIndex];
            currentIndex = nextIndex;
            findNextIndex();
            return entry;
        }

        public boolean hasNext() {
            if (!valid) throw new RuntimeException("#iterator() cannot be used nested.");
            return hasNext;
        }

        public Entries<K> iterator() {
            return this;
        }
    }

    static public class Values extends MapIterator<Object> {
        public Values (MapObjectInt<?> map) {
            super((MapObjectInt<Object>)map);
        }

        public boolean hasNext() {
            if (!valid) throw new RuntimeException("#iterator() cannot be used nested.");
            return hasNext;
        }

        public int next() {
            if (!hasNext) throw new NoSuchElementException();
            if (!valid) throw new RuntimeException("#iterator() cannot be used nested.");
            int value = map.valueTable[nextIndex];
            currentIndex = nextIndex;
            findNextIndex();
            return value;
        }

        public Values iterator() {
            return this;
        }

    }

    public static class Keys<K> extends MapIterator<K> implements Iterable<K>, Iterator<K> {
        public Keys(MapObjectInt<K> map) {
            super(map);
        }

        public boolean hasNext() {
            if (!valid) throw new RuntimeException("#iterator() cannot be used nested.");
            return hasNext;
        }

        public K next() {
            if (!hasNext) throw new NoSuchElementException();
            if (!valid) throw new RuntimeException("#iterator() cannot be used nested.");
            K key = map.keyTable[nextIndex];
            currentIndex = nextIndex;
            findNextIndex();
            return key;
        }

        public Keys<K> iterator() {
            return this;
        }

    }
}