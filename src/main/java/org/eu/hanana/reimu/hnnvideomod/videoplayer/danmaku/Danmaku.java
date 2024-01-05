package org.eu.hanana.reimu.hnnvideomod.videoplayer.danmaku;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import org.apache.commons.lang3.StringEscapeUtils;
import org.eu.hanana.reimu.hnnvideomod.videoplayer.gl.GdxFontRender;
import org.lwjgl.opengl.GL11;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Danmaku {
    public DanmakuState state;
    public int maxWidth;
    public int maxHeight;
    public int x;
    public int y;
    /**
     * 视频内弹幕出现时间
     */
    public double timestamp;
    /**
     * 透明度
     */
    public float opacity=1;
    /**
     * 弹幕类型<br/>
     * 1 2 3：普通弹幕<br/>
     * 4：底部弹幕<br/>
     * 5：顶部弹幕<br/>
     * 6：逆向弹幕<br/>
     * 7：高级弹幕<br/>
     * 8：代码弹幕<br/>
     * 9：BAS弹幕（pool必须为2）<br/>
     */
    public int mode;
    /**
     * 已显示时间
     */
    public int shownTime;
    public int fontSize;
    /**
     * 弹幕颜色
     */
    public int fontColor;
    /**
     * 弹幕发送时间
     */
    public long timeInMilliseconds;
    /**
     * 弹幕池类型
     */
    public int danmakuPoolType;
    /**
     * 发送者
     */
    public String userID;
    public String danmakuID;
    /**
     * 弹幕数据
     */
    public String content;
    /**
     * 显示的文本
     */
    public String text="";
    /**
     * Z旋转
     */
    public float rotateZ;
    /**
     * Y旋转
     */
    public float rotateY;
    public Danmaku(){
        this.state= DanmakuState.HIDDEN;
    }
    public static Class<? extends Danmaku> getDanmakuClass(int type){
        if (type>=1&&type<=3) {
            return DefaultDanmaku.class;
        }
        if (type==4) {
            return BottomDanmaku.class;
        }
        if (type==5) {
            return TopDanmaku.class;
        }
        if (type==6) {
            return ReverseDanmaku.class;
        }
        if (type==7) {
            return PoitionedDanmaku.class;
        }
        return null;
    }
    public static List<Danmaku> getDanmakus(String xmlData) {

        List<Danmaku> danmakuDataList = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream input = new ByteArrayInputStream(xmlData.getBytes(StandardCharsets.UTF_8));
            Document document = builder.parse(input);

            NodeList danmakuList = document.getElementsByTagName("d");
            danmakuDataList = new ArrayList<>();
            for (int i = 0; i < danmakuList.getLength(); i++) {
                Element danmaku = (Element) danmakuList.item(i);
                String pValue = danmaku.getAttribute("p");
                String content = danmaku.getTextContent();

                // 解析pValue，获取各个参数
                String[] params = pValue.split(",");
                String timestamp = params[0];
                String mode = params[1];
                String fontSize = params[2];
                String fontColor = params[3];
                String timeInMilliseconds = params[4];
                String danmakuPoolType = params[5];
                String userID = params[6];
                String danmakuID = params[7];

                // 在这里处理解析到的数据，可以打印、存储或进行其他操作
                Class<? extends Danmaku> danmakuClass = getDanmakuClass(Integer.parseInt(mode));
                if (danmakuClass==null){
                    System.out.printf("An unknown danmaku type(%s)%n",mode);
                    continue;
                }
                Danmaku data =danmakuClass.getDeclaredConstructor().newInstance();
                data.danmakuID = danmakuID;
                data.fontColor = Integer.parseInt(fontColor);
                data.timestamp = Double.parseDouble(timestamp);
                data.danmakuPoolType = Integer.parseInt(danmakuPoolType);
                data.fontSize = Integer.parseInt(fontSize);
                data.mode = Integer.parseInt(mode);
                data.timeInMilliseconds = Long.parseLong(timeInMilliseconds);
                data.userID = userID;
                data.content= content;
                try {
                    data.init();
                }catch (Exception e){
                    e.printStackTrace();
                }
                danmakuDataList.add(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return danmakuDataList;
    }
    public void update(){
        if (this instanceof IProgressDanmaku){
            IProgressDanmaku progressDanmaku = (IProgressDanmaku) this;
            progressDanmaku.setProgress((shownTime/60f)/progressDanmaku.getEndTime());
        }
    }
    public float getShowtimeSec(){
        return shownTime/60f;
    }
    public void draw(Batch batch, GdxFontRender font){
        if (batch.isDrawing()) batch.end();
        batch.begin();
        // 将角度转换为弧度
        float rotateYRad = MathUtils.degRad * rotateY;
        float rotateZRad = MathUtils.degRad * rotateZ;

        batch.setTransformMatrix(batch.getTransformMatrix().setToRotation(new Vector3(x,y,1),new Vector3(0,rotateYRad,rotateZRad)).scale(getScale(), getScale(), 1));
        Color color = new Color(fontColor);
        font.drawString(batch,text,x,y, new com.badlogic.gdx.graphics.Color(color.getRed()/255f,color.getGreen()/255f,color.getBlue()/255f,opacity));
        batch.setTransformMatrix(batch.getTransformMatrix().setToRotation(new Vector3(x,y,1),new Vector3(0,0,0)).scale(1/getScale(),1/getScale(), 1));
        batch.end();
    }

    public abstract void move(int maxWidth, int maxHeight);
    public void show(int maxWidth,int maxHeight){
        shownTime=0;
    }

    public abstract void check(int width, int height);
    public float getScale(){
        return fontSize/25f;
    }
    public enum DanmakuState{
        HIDDEN,
        SHOWN,
        PREPARED
    }
    public static List<Danmaku> getAllOnScreen(List<Danmaku> danmakus){
        List<Danmaku> list = new ArrayList<>();
        for (Danmaku danmaku : danmakus) {
            if (danmaku.state==DanmakuState.SHOWN) list.add(danmaku);
        }
        return list;
    }
    public static void clearAllOnScreen(List<Danmaku> danmakus){
        List<Danmaku> list = getAllOnScreen(danmakus);
        for (Danmaku danmaku : list) {
            danmaku.state=DanmakuState.HIDDEN;
        }
    }
    public abstract void init() throws Exception;
    public static String decodeText(String text){
        return StringEscapeUtils.unescapeHtml4(text);
    }
}
