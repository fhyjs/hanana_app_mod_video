package org.eu.hanana.reimu.hnnvideomod.videoplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.utils.Array;
import org.eu.hanana.reimu.hnnvideomod.Utils;
import org.eu.hanana.reimu.hnnvideomod.VideoDialog;
import org.eu.hanana.reimu.hnnvideomod.videoplayer.gl.FontRender;
import org.eu.hanana.reimu.hnnvideomod.videoplayer.gl.LibgdxApplication;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import uk.co.caprica.vlcj.player.embedded.OverlayApi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;


public class GlDanmaku implements IDanmaku {
    public static java.util.List<Runnable> runnables = new ArrayList<>();
    public boolean init;
    private final VideoDialog owner;
    // The window handle
    public long window;
    public boolean needResize;
    private boolean running;
    private FontRender font;
    private float time;


    public GlDanmaku(VideoDialog owner){
        this.owner=owner;
        /*
        new Thread(()->{
            init();
            loop();

            // Free the window callbacks and destroy the window
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);

            // Terminate GLFW and free the error callback
            glfwTerminate();
            Objects.requireNonNull(glfwSetErrorCallback(null)).free();
        }).start();
         */
        new Thread(()->{
            Lwjgl3ApplicationConfiguration lwjgl3ApplicationConfiguration = new Lwjgl3ApplicationConfiguration();
            lwjgl3ApplicationConfiguration.setTransparentFramebuffer(true);
            lwjgl3ApplicationConfiguration.setBackBufferConfig(8,8,8,8,16,0,0);
            lwjgl3ApplicationConfiguration.setInitialBackgroundColor(new com.badlogic.gdx.graphics.Color(0,0,0,0));
            new Lwjgl3Application(application=(new LibgdxApplication(this)), lwjgl3ApplicationConfiguration);
        }).start();
        new Thread(()->{
            try {
                while (Gdx.app==null) {
                    Thread.sleep(10);
                }
                Array<Lwjgl3Window> lwjgl3Windows;
                while (true) {
                    Field field = Gdx.app.getClass().getDeclaredField("windows");
                    field.setAccessible(true);
                    lwjgl3Windows = (Array<Lwjgl3Window>) field.get(Gdx.app);
                    if (lwjgl3Windows.notEmpty()) break;
                    Thread.sleep(11);
                }
                window=lwjgl3Windows.get(0).getWindowHandle();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
        jDialog = new JDialog(owner,"OPENGL 弹幕渲染器",false){
            @Override
            public void setVisible(boolean b) {
                if (!b&&false) {
                    if (!windowCreated) {
                        return;
                    }
                    if (hasFocus()) {
                        return;
                    }
                    windowCreated=false;
                }
                Object l;
                try {
                    Field field = OverlayApi.class.getDeclaredField("overlayWindowAdapter");
                    field.setAccessible(true);
                    l=field.get(owner.player.mediaPlayerComponent.mediaPlayer().overlay());
                } catch (NoSuchFieldException | IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
                for (WindowListener windowListener : owner.getWindowListeners()) {
                    if (windowListener==l) {
                        owner.removeWindowListener(windowListener);
                    }
                }
                super.setVisible(b);
            }
        };

        jDialog.setUndecorated(true);
        jDialog.setBackground(new Color(0,0,0,0));
        jDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        jDialog.setLayout(new GridLayout(1,1));
        jDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                windowCreated=true;
            }
        });
        jDialog.add(new Canvas(){
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                if (image != null) {
                    g.drawImage(image, 0, 0, this);
                }
            }
        });
        Timer timer = new Timer(1000/60, new ActionListener() { // 1000 milliseconds = 1 second
            @Override
            public void actionPerformed(ActionEvent e) {
                // This code will be executed every 1 second
                // You can update the image or perform any other actions here
                jDialog.repaint();
            }
        });
        timer.start();

    }
    private void render1() {
        glClearColor(0,0,0,0);
        GL11.glClear(GL_COLOR_BUFFER_BIT);
        //GL11.glColor3i(255,255,255);
        //font.drawString("123",10,30);
        GL11.glBegin(GL_FILL);
        GL11.glVertex2f(0,0);
        GL11.glVertex2f(width,height);
        GL11.glEnd();
        

    }
    public BufferedImage image;
    private boolean windowCreated=true;
    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        GL11.glClearColor(1, 0, 0, .7f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        running=true;
        init=true;
        while ( !glfwWindowShouldClose(window) &&running) {
            try {
                render1();
                glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
                glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_COLOR_BUFFER_BIT, GL_NEAREST);

                ByteBuffer bb = org.lwjgl.system.MemoryUtil.memAlloc(width * height * 4);
                glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
                glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, bb);
                // Test with stb_image to write as jpeg:
                // org.lwjgl.stb.STBImageWrite.stbi_flip_vertically_on_write(true);
                try {
                    image = Utils.createImageFromByteBuffer(bb,width,height);
                }catch (Exception e){
                    System.out.println("Skip a error frame.");
                }

                org.lwjgl.system.MemoryUtil.memFree(bb);
            }catch (Throwable e){
                e.printStackTrace();
            }
            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
            if (needResize){
                reSize((int) getWindow().getSize().getWidth(), (int) getWindow().getSize().getHeight());
                needResize=false;
            }
            java.util.List<Runnable> runnableList= new ArrayList<>(runnables);
            for (Runnable runnable : runnableList) {
                runnable.run();
            }
            runnables.clear();
        }
    }
    public int width,height;
    JDialog jDialog;
    public void reSize(int w,int h) {
        Gdx.graphics.setWindowedMode(w,h);
        width=w;
        height=h;
        GL11.glViewport(0,0,w,h);
    }
    public LibgdxApplication application;
    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);
            width=pWidth.get(0);
            height=pWidth.get(0);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        //glfwShowWindow(window);
        font = new FontRender(owner.getFont());
    }
    @Override
    public void timeChanged(long newTime) {
        ((LibgdxApplication) Gdx.app.getApplicationListener()).onTimeChanged(newTime);
    }

    @Override
    public void initDanmaku(Danmaku.DanmakuData danmakuData) {

    }

    @Override
    public void moveDanmaku(Danmaku.DanmakuData danmakuData, Iterator<Danmaku.DanmakuData> iterator) {

    }

    @Override
    public void removeDanmaku(Danmaku.DanmakuData danmakuData, Iterator<Danmaku.DanmakuData> iterator) {

    }

    @Override
    public Window getWindow() {
        return jDialog;
    }

    @Override
    public boolean isReady() {
        return init;
    }

    @Override
    public void reSize() {
        while (!init) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        getWindow().setSize(owner.getFullvideo().getSize());
        getWindow().validate();
        if (getWindow().getWidth()<=0) return;
        needResize=true;
    }

    @Override
    public void enable() {
        jDialog.setVisible(true);

    }

    @Override
    public void disable() {
        windowCreated=true;
        jDialog.setVisible(false);
    }

    @Override
    public void onSeek() {
        if (application!=null)
            org.eu.hanana.reimu.hnnvideomod.videoplayer.danmaku.Danmaku.clearAllOnScreen(application.danmakus);
    }

    public boolean isPlaying() {
        return owner.player.mediaPlayerComponent.mediaPlayer().status().isPlaying();
    }
    public void about() {
        application.about();
    }
}
