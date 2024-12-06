package com.heavybox.jtix.input_2;

import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.graphics.TextureBinder;
import com.heavybox.jtix.memory.MemoryResourceHolder;
import nu.pattern.OpenCV;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.nio.ByteBuffer;

public class Webcam implements MemoryResourceHolder {


    private boolean      active  = false;
    private boolean      init    = false;
    private VideoCapture capture = null;
    private Mat          mat     = null;

    private Texture feed;
    private ByteBuffer buffer;

    private byte[] rgbData;

    // move to separate thread.
    public void init() {
        if (init) return;

        OpenCV.loadShared();
        this.capture = new VideoCapture(0);
        this.mat = new Mat();

        init = true;
    }

    public Texture getFeed() {
        if (!init) init();
        if (capture.read(mat)) {
            int width = mat.width();
            int height = mat.height();
            if (rgbData == null) {
                rgbData = new byte[width * height * 3]; // Example RGB data
                this.buffer = ByteBuffer.allocateDirect(640 * 480 * 3);
                this.feed = new Texture(width, height, buffer,
                        Texture.FilterMag.NEAREST, Texture.FilterMin.NEAREST,
                        Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE,1,false);
            }
            mat.get(0,0, rgbData);

            ByteBuffer rgbBuffer = ByteBuffer.allocateDirect(width * height * 3);
            rgbBuffer.put(rgbData);
            rgbBuffer.flip();
            TextureBinder.bind(feed);
            GL11.glTexSubImage2D(
                    GL11.GL_TEXTURE_2D, // Target
                    0,                  // Mipmap level
                    0,
                    0,
                    width,
                    height,
                    GL11.GL_RGB,
                    GL11.GL_UNSIGNED_BYTE,
                    rgbBuffer          // Data
            );
        }

        return feed;
    }

    @Override
    public void deleteAll() {
        capture.release();
        feed.delete();
    }

}
