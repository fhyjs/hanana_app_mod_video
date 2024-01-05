package org.eu.hanana.reimu.hnnvideomod.videoplayer.gl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.eu.hanana.reimu.hnnvideomod.Utils;
import org.eu.hanana.reimu.hnnvideomod.videoplayer.GlDanmaku;
import org.eu.hanana.reimu.hnnvideomod.videoplayer.danmaku.Danmaku;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.eu.hanana.reimu.hnnvideomod.videoplayer.GlDanmaku.runnables;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class LibgdxApplication implements ApplicationListener {
    GlDanmaku glDanmaku;
    private GdxFontRender font;

    public LibgdxApplication(GlDanmaku glDanmaku) {
        this.glDanmaku=glDanmaku;
    }
    ShapeRenderer renderer;
    SpriteBatch batch;
    public List<Danmaku> danmakus;
    List<Danmaku> allOnScreen;
    Thread timer;
    @Override
    public void create() {
        while (glDanmaku==null||glDanmaku.window==0L) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        GLFW.glfwHideWindow(glDanmaku.window);
        GLFW.glfwSetWindowTitle(glDanmaku.window,"LWJGL弹幕渲染器");

        renderer = new ShapeRenderer();
        batch = new SpriteBatch();
        renderer.setProjectionMatrix(batch.getProjectionMatrix());
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size=18;
        font=new GdxFontRender(new FreeTypeFontGenerator(Gdx.files.internal("font/微软雅黑.ttf")),fontParameter);
        timer = new Thread(this::timeTicker);

        danmakus = Danmaku.getDanmakus(Utils.getAssets(getClass().getClassLoader(), "video/tst1.xml"));
        timer.start();

        glDanmaku.init=true;
    }
    private void render0() {
        GL11.glClearColor(0,0,0,0);
        GL11.glClear(GL_COLOR_BUFFER_BIT);
        if (allOnScreen!=null) {
            for (Danmaku danmaku : allOnScreen) {
                danmaku.draw(batch, font);
            }
        }
    }
    public void onTimeChanged(long time){
        for (Danmaku danmaku : danmakus) {
            if (Utils.areDoublesEqual(danmaku.timestamp,time/1000f)){
                if (danmaku.state!= Danmaku.DanmakuState.SHOWN){
                    danmaku.show(glDanmaku.width,glDanmaku.height);
                    danmaku.state= Danmaku.DanmakuState.SHOWN;
                }
            }
        }
    }
    private void timeTicker(){
        while (!Thread.currentThread().isInterrupted()){
            allOnScreen=Danmaku.getAllOnScreen(danmakus);

            for (Danmaku danmaku : allOnScreen) {
                if (glDanmaku.isPlaying()) {
                    danmaku.shownTime++;
                    danmaku.move(glDanmaku.width, glDanmaku.height);
                    danmaku.update();
                }
                danmaku.check(glDanmaku.width, glDanmaku.height);
            }
            try {
                Thread.sleep(1000 / 60);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void resize(int width, int height) {
        glDanmaku.needResize=true;
    }

    @Override
    public void render() {
        try {
            render0();
            glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
            glBlitFramebuffer(0, 0, glDanmaku.width, glDanmaku.height, 0, 0, glDanmaku.width, glDanmaku.height, GL_COLOR_BUFFER_BIT, GL_NEAREST);

            ByteBuffer bb = org.lwjgl.system.MemoryUtil.memAlloc(glDanmaku.width * glDanmaku.height * 4);
            glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
            glReadPixels(0, 0, glDanmaku.width, glDanmaku.height, GL_RGBA, GL_UNSIGNED_BYTE, bb);
            // Test with stb_image to write as jpeg:
            // org.lwjgl.stb.STBImageWrite.stbi_flip_vertically_on_write(true);
            try {
                glDanmaku.image = Utils.createImageFromByteBuffer(bb,glDanmaku.width,glDanmaku.height);
            }catch (Exception e){
                System.out.println("Skip a error frame.");
            }

            org.lwjgl.system.MemoryUtil.memFree(bb);
        }catch (Throwable e){
            e.printStackTrace();
        }
        if (glDanmaku.needResize){
            glDanmaku.reSize((int) (glDanmaku.getWindow().getSize().getWidth()), (int) (glDanmaku.getWindow().getSize().getHeight()));
            batch.dispose();
            batch=new SpriteBatch();
            glDanmaku.needResize=false;
        }
        java.util.List<Runnable> runnableList= new ArrayList<>(runnables);
        for (Runnable runnable : runnableList) {
            try {
                runnable.run();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        runnables.clear();
    }



    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        renderer.dispose();
        font.dispose();
        timer.interrupt();
    }
}
