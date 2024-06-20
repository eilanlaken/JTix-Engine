package org.example.engine.core.graphics.fonts_tmp;

import org.example.engine.core.graphics.GraphicsException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class FileHandle {

    protected File file;
    protected FileType type;

    protected FileHandle () {
    }

    public FileHandle (String fileName) {
        this.file = new File(fileName);
        this.type = FileType.Absolute;
    }

    protected FileHandle (String fileName, FileType type) {
        this.type = type;
        file = new File(fileName);
    }

    protected FileHandle (File file, FileType type) {
        this.file = file;
        this.type = type;
    }

    /** @return the path of the file as specified on construction, e.g. Gdx.files.internal("dir/file.png") -> dir/file.png.
     *         backward slashes will be replaced by forward slashes. */
    public String path () {
        return file.getPath().replace('\\', '/');
    }

    /** @return the name of the file, without any parent paths. */
    public String name () {
        return file.getName();
    }

    /** Returns the file extension (without the dot) or an empty string if the file name doesn't contain a dot. */
    public String extension () {
        String name = file.getName();
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1) return "";
        return name.substring(dotIndex + 1);
    }

    /** @return the name of the file, without parent paths or the extension. */
    public String nameWithoutExtension () {
        String name = file.getName();
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1) return name;
        return name.substring(0, dotIndex);
    }

    /** @return the path and filename without the extension, e.g. dir/dir2/file.png -> dir/dir2/file. backward slashes will be
     *         returned as forward slashes. */
    public String pathWithoutExtension () {
        String path = file.getPath().replace('\\', '/');
        int dotIndex = path.lastIndexOf('.');
        if (dotIndex == -1) return path;
        return path.substring(0, dotIndex);
    }

    public FileType type () {
        return type;
    }

    public File file () {
        if (type == FileType.External) return new File(file.getPath());
        return file;
    }

    /** Returns a stream for reading this file as bytes.
     * @throws RuntimeException if the file handle represents a directory, doesn't exist, or could not be read. */
    public InputStream read () {
        if (type == FileType.Classpath || (type == FileType.Internal && !file().exists())
                || (type == FileType.Local && !file().exists())) {
            InputStream input = FileHandle.class.getResourceAsStream("/" + file.getPath().replace('\\', '/'));
            if (input == null) throw new RuntimeException("File not found: " + file + " (" + type + ")");
            return input;
        }
        try {
            return new FileInputStream(file());
        } catch (Exception ex) {
            if (file().isDirectory())
                throw new RuntimeException("Cannot open a stream to a directory: " + file + " (" + type + ")", ex);
            throw new RuntimeException("Error reading file: " + file + " (" + type + ")", ex);
        }
    }

    public BufferedInputStream read (int bufferSize) {
        return new BufferedInputStream(read(), bufferSize);
    }

    public Reader reader () {
        return new InputStreamReader(read());
    }

    public Reader reader (String charset) {
        InputStream stream = read();
        try {
            return new InputStreamReader(stream, charset);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Error reading file: " + this, ex);
        }
    }

    public BufferedReader reader (int bufferSize) {
        return new BufferedReader(new InputStreamReader(read()), bufferSize);
    }

    public BufferedReader reader (int bufferSize, String charset) {
        try {
            return new BufferedReader(new InputStreamReader(read(), charset), bufferSize);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Error reading file: " + this, ex);
        }
    }

    /** Returns a stream for writing to this file. Parent directories will be created if necessary.
     * @param append If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @throws RuntimeException if this file handle represents a directory, if it is a {@link FileType#Classpath} or
     *            {@link FileType#Internal} file, or if it could not be written. */
    public OutputStream write (boolean append) {
        if (type == FileType.Classpath) throw new RuntimeException("Cannot write to a classpath file: " + file);
        if (type == FileType.Internal) throw new RuntimeException("Cannot write to an internal file: " + file);
        parent().mkdirs();
        try {
            return new FileOutputStream(file(), append);
        } catch (Exception ex) {
            if (file().isDirectory())
                throw new RuntimeException("Cannot open a stream to a directory: " + file + " (" + type + ")", ex);
            throw new RuntimeException("Error writing file: " + file + " (" + type + ")", ex);
        }
    }

    /** Returns a buffered stream for writing to this file. Parent directories will be created if necessary.
     * @param append If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @param bufferSize The size of the buffer.
     * @throws RuntimeException if this file handle represents a directory, if it is a {@link FileType#Classpath} or
     *            {@link FileType#Internal} file, or if it could not be written. */
    public OutputStream write (boolean append, int bufferSize) {
        return new BufferedOutputStream(write(append), bufferSize);
    }

    /** Reads the remaining bytes from the specified stream and writes them to this file. The stream is closed. Parent directories
     * will be created if necessary.
     * @param append If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @throws RuntimeException if this file handle represents a directory, if it is a {@link FileType#Classpath} or
     *            {@link FileType#Internal} file, or if it could not be written. */
    public void write (InputStream input, boolean append) {
        OutputStream output = null;
        try {
            output = write(append);
            //StreamUtils.copyStream(input, output);
        } catch (Exception ex) {
            throw new RuntimeException("Error stream writing to file: " + file + " (" + type + ")", ex);
        } finally {
            //StreamUtils.closeQuietly(input);
            //StreamUtils.closeQuietly(output);
        }

    }

    /** Returns a writer for writing to this file using the default charset. Parent directories will be created if necessary.
     * @param append If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @throws RuntimeException if this file handle represents a directory, if it is a {@link FileType#Classpath} or
     *            {@link FileType#Internal} file, or if it could not be written. */
    public Writer writer (boolean append) {
        return writer(append, null);
    }

    /** Returns a writer for writing to this file. Parent directories will be created if necessary.
     * @param append If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @param charset May be null to use the default charset.
     * @throws RuntimeException if this file handle represents a directory, if it is a {@link FileType#Classpath} or
     *            {@link FileType#Internal} file, or if it could not be written. */
    public Writer writer (boolean append, String charset) {
        if (type == FileType.Classpath) throw new RuntimeException("Cannot write to a classpath file: " + file);
        if (type == FileType.Internal) throw new RuntimeException("Cannot write to an internal file: " + file);
        parent().mkdirs();
        try {
            FileOutputStream output = new FileOutputStream(file(), append);
            if (charset == null)
                return new OutputStreamWriter(output);
            else
                return new OutputStreamWriter(output, charset);
        } catch (IOException ex) {
            if (file().isDirectory())
                throw new RuntimeException("Cannot open a stream to a directory: " + file + " (" + type + ")", ex);
            throw new RuntimeException("Error writing file: " + file + " (" + type + ")", ex);
        }
    }

    /** Writes the specified string to the file using the default charset. Parent directories will be created if necessary.
     * @param append If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @throws RuntimeException if this file handle represents a directory, if it is a {@link FileType#Classpath} or
     *            {@link FileType#Internal} file, or if it could not be written. */
    public void writeString (String string, boolean append) {
        writeString(string, append, null);
    }

    /** Writes the specified string to the file using the specified charset. Parent directories will be created if necessary.
     * @param append If false, this file will be overwritten if it exists, otherwise it will be appended.
     * @param charset May be null to use the default charset.
     * @throws RuntimeException if this file handle represents a directory, if it is a {@link FileType#Classpath} or
     *            {@link FileType#Internal} file, or if it could not be written. */
    public void writeString (String string, boolean append, String charset) {
        Writer writer = null;
        try {
            writer = writer(append, charset);
            writer.write(string);
        } catch (Exception ex) {
            throw new RuntimeException("Error writing file: " + file + " (" + type + ")", ex);
        } finally {
            //StreamUtils.closeQuietly(writer);
        }
    }


    /** Returns the paths to the children of this directory. Returns an empty list if this file handle represents a file and not a
     * directory. On the desktop, an {@link FileType#Internal} handle to a directory on the classpath will return a zero length
     * array.
     * @throws RuntimeException if this file is an {@link FileType#Classpath} file. */
    public FileHandle[] list () {
        if (type == FileType.Classpath) throw new RuntimeException("Cannot list a classpath directory: " + file);
        String[] relativePaths = file().list();
        if (relativePaths == null) return new FileHandle[0];
        FileHandle[] handles = new FileHandle[relativePaths.length];
        for (int i = 0, n = relativePaths.length; i < n; i++)
            handles[i] = child(relativePaths[i]);
        return handles;
    }

    /** Returns the paths to the children of this directory that satisfy the specified filter. Returns an empty list if this file
     * handle represents a file and not a directory. On the desktop, an {@link FileType#Internal} handle to a directory on the
     * classpath will return a zero length array.
     * @param filter the {@link FileFilter} to filter files
     * @throws RuntimeException if this file is an {@link FileType#Classpath} file. */
    public FileHandle[] list (FileFilter filter) {
        if (type == FileType.Classpath) throw new RuntimeException("Cannot list a classpath directory: " + file);
        File file = file();
        String[] relativePaths = file.list();
        if (relativePaths == null) return new FileHandle[0];
        FileHandle[] handles = new FileHandle[relativePaths.length];
        int count = 0;
        for (int i = 0, n = relativePaths.length; i < n; i++) {
            String path = relativePaths[i];
            FileHandle child = child(path);
            if (!filter.accept(child.file())) continue;
            handles[count] = child;
            count++;
        }
        if (count < relativePaths.length) {
            FileHandle[] newHandles = new FileHandle[count];
            System.arraycopy(handles, 0, newHandles, 0, count);
            handles = newHandles;
        }
        return handles;
    }

    /** Returns the paths to the children of this directory that satisfy the specified filter. Returns an empty list if this file
     * handle represents a file and not a directory. On the desktop, an {@link FileType#Internal} handle to a directory on the
     * classpath will return a zero length array.
     * @param filter the {@link FilenameFilter} to filter files
     * @throws RuntimeException if this file is an {@link FileType#Classpath} file. */
    public FileHandle[] list (FilenameFilter filter) {
        if (type == FileType.Classpath) throw new RuntimeException("Cannot list a classpath directory: " + file);
        File file = file();
        String[] relativePaths = file.list();
        if (relativePaths == null) return new FileHandle[0];
        FileHandle[] handles = new FileHandle[relativePaths.length];
        int count = 0;
        for (int i = 0, n = relativePaths.length; i < n; i++) {
            String path = relativePaths[i];
            if (!filter.accept(file, path)) continue;
            handles[count] = child(path);
            count++;
        }
        if (count < relativePaths.length) {
            FileHandle[] newHandles = new FileHandle[count];
            System.arraycopy(handles, 0, newHandles, 0, count);
            handles = newHandles;
        }
        return handles;
    }

    /** Returns the paths to the children of this directory with the specified suffix. Returns an empty list if this file handle
     * represents a file and not a directory. On the desktop, an {@link FileType#Internal} handle to a directory on the classpath
     * will return a zero length array.
     * @throws RuntimeException if this file is an {@link FileType#Classpath} file. */
    public FileHandle[] list (String suffix) {
        if (type == FileType.Classpath) throw new RuntimeException("Cannot list a classpath directory: " + file);
        String[] relativePaths = file().list();
        if (relativePaths == null) return new FileHandle[0];
        FileHandle[] handles = new FileHandle[relativePaths.length];
        int count = 0;
        for (int i = 0, n = relativePaths.length; i < n; i++) {
            String path = relativePaths[i];
            if (!path.endsWith(suffix)) continue;
            handles[count] = child(path);
            count++;
        }
        if (count < relativePaths.length) {
            FileHandle[] newHandles = new FileHandle[count];
            System.arraycopy(handles, 0, newHandles, 0, count);
            handles = newHandles;
        }
        return handles;
    }

    /** Returns true if this file is a directory. Always returns false for classpath files. On Android, an
     * {@link FileType#Internal} handle to an empty directory will return false. On the desktop, an {@link FileType#Internal}
     * handle to a directory on the classpath will return false. */
    public boolean isDirectory () {
        if (type == FileType.Classpath) return false;
        return file().isDirectory();
    }

    /** Returns a handle to the child with the specified name. */
    public FileHandle child (String name) {
        if (file.getPath().length() == 0) return new FileHandle(new File(name), type);
        return new FileHandle(new File(file, name), type);
    }

    /** Returns a handle to the sibling with the specified name.
     * @throws RuntimeException if this file is the root. */
    public FileHandle sibling (String name) {
        if (file.getPath().length() == 0) throw new RuntimeException("Cannot get the sibling of the root.");
        return new FileHandle(new File(file.getParent(), name), type);
    }

    public FileHandle parent () {
        File parent = file.getParentFile();
        if (parent == null) {
            if (type == FileType.Absolute)
                parent = new File("/");
            else
                parent = new File("");
        }
        return new FileHandle(parent, type);
    }

    /** @throws RuntimeException if this file handle is a {@link FileType#Classpath} or {@link FileType#Internal} file. */
    public void mkdirs () {
        if (type == FileType.Classpath) throw new RuntimeException("Cannot mkdirs with a classpath file: " + file);
        if (type == FileType.Internal) throw new RuntimeException("Cannot mkdirs with an internal file: " + file);
        file().mkdirs();
    }

    /** Returns true if the file exists. On Android, a {@link FileType#Classpath} or {@link FileType#Internal} handle to a
     * directory will always return false. Note that this can be very slow for internal files on Android! */
    public boolean exists () {
        switch (type) {
            case Internal:
                if (file().exists()) return true;
                // Fall through.
            case Classpath:
                return FileHandle.class.getResource("/" + file.getPath().replace('\\', '/')) != null;
        }
        return file().exists();
    }

    /** Deletes this file or empty directory and returns success. Will not delete a directory that has children.
     * @throws RuntimeException if this file handle is a {@link FileType#Classpath} or {@link FileType#Internal} file. */
    public boolean delete () {
        if (type == FileType.Classpath) throw new RuntimeException("Cannot delete a classpath file: " + file);
        if (type == FileType.Internal) throw new RuntimeException("Cannot delete an internal file: " + file);
        return file().delete();
    }

    /** Deletes this file or directory and all children, recursively.
     * @throws RuntimeException if this file handle is a {@link FileType#Classpath} or {@link FileType#Internal} file. */
    public boolean deleteDirectory () {
        if (type == FileType.Classpath) throw new RuntimeException("Cannot delete a classpath file: " + file);
        if (type == FileType.Internal) throw new RuntimeException("Cannot delete an internal file: " + file);
        return deleteDirectory(file());
    }

    /** Deletes all children of this directory, recursively.
     * @throws RuntimeException if this file handle is a {@link FileType#Classpath} or {@link FileType#Internal} file. */
    public void emptyDirectory () {
        emptyDirectory(false);
    }

    /** Deletes all children of this directory, recursively. Optionally preserving the folder structure.
     * @throws RuntimeException if this file handle is a {@link FileType#Classpath} or {@link FileType#Internal} file. */
    public void emptyDirectory (boolean preserveTree) {
        if (type == FileType.Classpath) throw new RuntimeException("Cannot delete a classpath file: " + file);
        if (type == FileType.Internal) throw new RuntimeException("Cannot delete an internal file: " + file);
        emptyDirectory(file(), preserveTree);
    }

    /** Copies this file or directory to the specified file or directory. If this handle is a file, then 1) if the destination is a
     * file, it is overwritten, or 2) if the destination is a directory, this file is copied into it, or 3) if the destination
     * doesn't exist, {@link #mkdirs()} is called on the destination's parent and this file is copied into it with a new name. If
     * this handle is a directory, then 1) if the destination is a file, RuntimeException is thrown, or 2) if the destination is
     * a directory, this directory is copied into it recursively, overwriting existing files, or 3) if the destination doesn't
     * exist, {@link #mkdirs()} is called on the destination and this directory is copied into it recursively.
     * @throws RuntimeException if the destination file handle is a {@link FileType#Classpath} or {@link FileType#Internal}
     *            file, or copying failed. */
    public void copyTo (FileHandle dest) {
        if (!isDirectory()) {
            if (dest.isDirectory()) dest = dest.child(name());
            copyFile(this, dest);
            return;
        }
        if (dest.exists()) {
            if (!dest.isDirectory()) throw new RuntimeException("Destination exists but is not a directory: " + dest);
        } else {
            dest.mkdirs();
            if (!dest.isDirectory()) throw new RuntimeException("Destination directory cannot be created: " + dest);
        }
        copyDirectory(this, dest.child(name()));
    }

    /** Moves this file to the specified file, overwriting the file if it already exists.
     * @throws RuntimeException if the source or destination file handle is a {@link FileType#Classpath} or
     *            {@link FileType#Internal} file. */
    public void moveTo (FileHandle dest) {
        switch (type) {
            case Classpath:
                throw new RuntimeException("Cannot move a classpath file: " + file);
            case Internal:
                throw new RuntimeException("Cannot move an internal file: " + file);
            case Absolute:
            case External:
                // Try rename for efficiency and to change case on case-insensitive file systems.
                if (file().renameTo(dest.file())) return;
        }
        copyTo(dest);
        delete();
        if (exists() && isDirectory()) deleteDirectory();
    }

    /** Returns the length in bytes of this file, or 0 if this file is a directory, does not exist, or the size cannot otherwise be
     * determined. */
    public long length () {
        if (type == FileType.Classpath || (type == FileType.Internal && !file.exists())) {
            InputStream input = read();
            try {
                return input.available();
            } catch (Exception ignored) {
            } finally {
                //StreamUtils.closeQuietly(input);
            }
            return 0;
        }
        return file().length();
    }

    public boolean equals (Object obj) {
        if (!(obj instanceof FileHandle)) return false;
        FileHandle other = (FileHandle)obj;
        return type == other.type && path().equals(other.path());
    }

    public int hashCode () {
        int hash = 1;
        hash = hash * 37 + type.hashCode();
        hash = hash * 67 + path().hashCode();
        return hash;
    }

    public String toString () {
        return file.getPath().replace('\\', '/');
    }

    static private void emptyDirectory (File file, boolean preserveTree) {
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (int i = 0, n = files.length; i < n; i++) {
                    if (!files[i].isDirectory())
                        files[i].delete();
                    else if (preserveTree)
                        emptyDirectory(files[i], true);
                    else
                        deleteDirectory(files[i]);
                }
            }
        }
    }

    static private boolean deleteDirectory (File file) {
        emptyDirectory(file, false);
        return file.delete();
    }

    static private void copyFile (FileHandle source, FileHandle dest) {
        try {
            dest.write(source.read(), false);
        } catch (Exception ex) {

        }
    }

    static private void copyDirectory (FileHandle sourceDir, FileHandle destDir) {
        destDir.mkdirs();
        FileHandle[] files = sourceDir.list();
        for (int i = 0, n = files.length; i < n; i++) {
            FileHandle srcFile = files[i];
            FileHandle destFile = destDir.child(srcFile.name());
            if (srcFile.isDirectory())
                copyDirectory(srcFile, destFile);
            else
                copyFile(srcFile, destFile);
        }
    }

    enum FileType {
        Classpath,
        Internal,
        Absolute,
        External,
        Local
    }

}
