package com.heavybox.jtix.application;

public class ApplicationWindowAttributes {

    public int     posX                   = -1;
    public int     posY                   = -1;
    public int     width                  = 640*2;
    public int     height                 = 480*2;
    public int     minWidth               = -1;
    public int     minHeight              = -1;
    public int     maxWidth               = -1;
    public int     maxHeight              = -1;
    public boolean autoMinimized          = true;
    public boolean resizable              = true;
    public boolean decorated              = true;
    public boolean minimized              = false;
    public boolean maximized              = false;
    public String  iconPath               = null;
    public boolean visible                = true;
    public boolean fullScreen             = false;
    public String  title                  = "JTix Game";
    public boolean initialVisible         = true;
    public boolean vSyncEnabled           = false;
    public boolean transparentFrameBuffer = false;

}
