package org.eu.hanana.reimu.hnnvideomod.videoplayer.gl;

import org.eu.hanana.reimu.hnnvideomod.Utils;
import org.eu.hanana.reimu.hnnvideomod.videoplayer.GlDanmaku;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontRender {
    Font font;
    private final Map<String,Text> texts = new HashMap<>();
    public FontRender(Font font){
        this.font=font;
        addToRenderList("3");

    }
    public void addToRenderList(String c){
        texts.put(c,new Text());
        new Processor(c).start();
    }
    public void drawString(String s,float x,float y){
        for (int i = 0; i < s.length(); i++) {
            char c=s.charAt(i);
            String cs = String.valueOf(c);
            if (!texts.containsKey(cs)){
                addToRenderList(cs);
                continue;
            }
            if (!texts.get(cs).ok){
                continue;
            }
            BufferedImage txtImg=texts.get(cs).txtImg;
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texts.get(cs).texID);
            GL11.glTranslatef(x,y,10);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0, 0);
            GL11.glVertex2i(0, 0);

            GL11.glTexCoord2f(1, 0);
            GL11.glVertex2i(512, 0);

            GL11.glTexCoord2f(1, 1);;
            GL11.glVertex2i(512, 512);

            GL11.glTexCoord2f(0, 1);
            GL11.glVertex2i(0, 512);
            GL11.glEnd();
            GL11.glPopMatrix();
        }
    }
    public static class Text{
        public boolean ok=false;
        public BufferedImage txtImg;
        public int texID;
    }
    public class Processor extends Thread{
        private final String c;
        public Processor(String c){
            this.c=c;
        }

        @Override
        public void run() {
            BufferedImage offscreenImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics offscreenGraphics = offscreenImage.getGraphics();

            // 获取FontMetrics对象
            FontMetrics fontMetrics = offscreenGraphics.getFontMetrics(font);

            // 要测量的字符串

            // 获取字符串的宽度和高度
            int width = fontMetrics.stringWidth(c);
            int height = fontMetrics.getHeight();

            // 创建新的BufferedImage
            BufferedImage textImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics textGraphics = textImage.getGraphics();
            textGraphics.setFont(font);
            textGraphics.setColor(Color.BLUE);
            textGraphics.drawString(c, 0, fontMetrics.getAscent()); // 使用getAscent获取基线位置
            texts.get(c).txtImg=textImage;

            GlDanmaku.runnables.add(()-> {
                int textureID = GL11.glGenTextures();
                GL11.glBindTexture(GL11.GL_TEXTURE_2D,textureID);
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D,0,GL11.GL_RGBA,width,height,0,GL11.GL_RGBA,GL11.GL_UNSIGNED_BYTE,Utils.convertImageToByteBuffer(textImage));
                texts.get(c).ok=true;
                texts.get(c).texID=textureID;
            });

        }
    }
}
