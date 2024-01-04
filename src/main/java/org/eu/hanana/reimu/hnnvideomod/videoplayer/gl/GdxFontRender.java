package org.eu.hanana.reimu.hnnvideomod.videoplayer.gl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import org.eu.hanana.reimu.hnnvideomod.videoplayer.GlDanmaku;

import java.lang.reflect.Field;
import java.util.*;

public class GdxFontRender {
    private final FreeTypeFontGenerator generator;
    private final FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private final Map<Character, FontData> fonts = new HashMap<Character, FontData>();
    public GdxFontRender(FreeTypeFontGenerator generator,FreeTypeFontGenerator.FreeTypeFontParameter parameter) {
        this.generator=generator;
        this.parameter=parameter;
    }
    public void drawString(Batch batch, String string, float x, float y, Color color){
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (!fonts.containsKey(c)){
                FreeTypeFontGenerator.FreeTypeFontParameter parameter1 = copyParameter(parameter);
                new Processor(c,parameter1).start();
                continue;
            }
            FontData fontData = fonts.get(c);
            fontData.font.setColor(color);
            fontData.font.draw(batch,String.valueOf(c),x,y);
            x+=fontData.w+2;
        }
    }
    public void dispose(){
        generator.dispose(); // don't forget to dispose to avoid memory leaks!
    }
    private class Processor extends Thread{
        FreeTypeFontGenerator.FreeTypeFontParameter parameter;
        char s;
        public Processor(char s, FreeTypeFontGenerator.FreeTypeFontParameter parameter){
            this.s=s;
            this.parameter=parameter;
        }
        @Override
        public void run() {
            GlDanmaku.runnables.add(()->{
                parameter.characters = String.valueOf(s);
                BitmapFont.Glyph glyph = generator.generateData(parameter).getGlyph(s);
                FontData fontData = new FontData();
                fontData.w=glyph.width;
                fontData.font=generator.generateFont(parameter);
                fonts.put(s,fontData);
            });
        }
    }
    public static class FontData{
        BitmapFont font;
        int w;
    }
    public static FreeTypeFontGenerator.FreeTypeFontParameter copyParameter(FreeTypeFontGenerator.FreeTypeFontParameter source){
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        for (Field field : fontParameter.getClass().getFields()) {
            try {
                field.set(fontParameter,field.get(source));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return fontParameter;
    }
}
