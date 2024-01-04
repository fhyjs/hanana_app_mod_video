/*
 * Created by JFormDesigner on Sat Dec 23 00:27:05 CST 2023
 */

package org.eu.hanana.reimu.hnnvideomod.videoplayer;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eu.hanana.reimu.hnnvideomod.Utils;
import org.eu.hanana.reimu.hnnvideomod.VideoDialog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

import static org.eu.hanana.reimu.hnnvideomod.Utils.areDoublesEqual;

/**
 * @author administer
 */
public class Danmaku extends JDialog implements IDanmaku{
    //private static final Logger logger = LogManager.getLogger("DanmakuPlayer");
    private final List<DanmakuData> danmakuData = new ArrayList<>();
    private final List<DanmakuData> onScreenDanmakuData = new ArrayList<>();
    private final VideoDialog owner;
    // 创建 Random 对象
    private final Random random = new Random();
    private final DanmakuPanel danmakuPanel;
    public float danmakuSpeed=0.05f;
    public boolean ready;
    public Danmaku(VideoDialog owner) {
        super(owner);
        this.owner=owner;
        ready=false;
        setUndecorated(true);  // 设置无边框
        initComponents();
        setBackground(new Color(0, 0, 0, 0));  // 设置背景透明
        add(danmakuPanel=new DanmakuPanel());
        owner.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                danmakuPanel.setSize(getSize());
                //System.out.println(danmakuPanel.getSize());
            }
        });
    }

    public void timeChanged(long newTime) {
        for (DanmakuData danmakuDatum : danmakuData) {
            if (areDoublesEqual(danmakuDatum.timestamp,newTime/1000d)&&danmakuDatum.state!= DanmakuData.DanmakuState.SHOWN){
                danmakuDatum.state= DanmakuData.DanmakuState.SHOWN;
                onScreenDanmakuData.add(danmakuDatum);
                initDanmaku(danmakuDatum);
            }
        }
        danmakuPanel.repaint();
    }
    public void initDanmaku(DanmakuData danmakuData) {
        danmakuData.shownTime=0;
        switch (danmakuData.mode) {
            case 1,2,3:
                danmakuData.x = getSize().width;
                danmakuData.y = random.nextInt(getSize().height + 1);
                danmakuData.text=StringEscapeUtils.unescapeHtml4(danmakuData.content);
                break;
            case 4:
                danmakuData.x = getSize().width/2;
                danmakuData.y = getSize().height-random.nextInt((int) (getSize().height*0.9 + 10));
                danmakuData.text=StringEscapeUtils.unescapeHtml4(danmakuData.content);
                break;
            case 5:
                danmakuData.x = getSize().width/2;
                danmakuData.y = random.nextInt((int) (getSize().height*0.1 + 10));
                danmakuData.text=StringEscapeUtils.unescapeHtml4(danmakuData.content);
                break;
            case 6:
                danmakuData.x = 0;
                danmakuData.y = random.nextInt(getSize().height + 1);
                danmakuData.text=StringEscapeUtils.unescapeHtml4(danmakuData.content);
                break;
            case 7:
                List<String> data = danmakuData.getPositionData();
                float x=Float.parseFloat(data.get(0));
                float y=Float.parseFloat(data.get(1));
                if (x<1&&y<1){
                    danmakuData.x= (int) (getSize().width*x);
                    danmakuData.y= (int) (getSize().height*y);
                }else {
                    danmakuData.x= (int) x;
                    danmakuData.y= (int) y;
                }
                danmakuData.text=StringEscapeUtils.unescapeHtml4(data.get(4)).substring(1,StringEscapeUtils.unescapeHtml4(data.get(4)).length()-1);
                String[] opacities = StringEscapeUtils.unescapeHtml4(data.get(2)).substring(1,StringEscapeUtils.unescapeHtml4(data.get(2)).length()-1).split("-");
                danmakuData.opacity=Float.parseFloat(opacities[0]);
                break;
            default:
                danmakuData.text=StringEscapeUtils.unescapeHtml4(danmakuData.content);
                break;
        }
    }
    public void moveDanmaku(DanmakuData danmakuData, Iterator<DanmakuData> iterator) {
        danmakuData.shownTime++;
        switch (danmakuData.mode) {
            case 1,2,3:
                danmakuData.x -= (int) (getWidth() * danmakuSpeed);
                if (danmakuData.x < 0) {
                    removeDanmaku(danmakuData,iterator);
                }
                break;
            case 4,5:
                if (danmakuData.shownTime>30)
                    removeDanmaku(danmakuData,iterator);
                break;
            case 6:
                danmakuData.x += (int) (getWidth() * danmakuSpeed);
                if (danmakuData.x > getSize().getWidth()) {
                    removeDanmaku(danmakuData,iterator);
                }
                break;
            case 7:
                List<String> data = danmakuData.getPositionData();
                String[] opacities = StringEscapeUtils.unescapeHtml4(data.get(2)).substring(1,data.get(2).length()-1).split("-");
                float progress = danmakuData.shownTime/Float.parseFloat(data.get(3))/4;
                if (progress>1)
                    removeDanmaku(danmakuData,iterator);
                danmakuData.opacity+=(Float.parseFloat(opacities[1])-danmakuData.opacity)*progress;
                if (data.size()>=9){
                    danmakuData.x+= (int) ((Float.parseFloat(data.get(7))-danmakuData.x)*progress);
                    danmakuData.y+= (int) ((Float.parseFloat(data.get(8))-danmakuData.y)*progress);
                }
                break;
        }
    }
    public void removeDanmaku(DanmakuData danmakuData, Iterator<DanmakuData> iterator) {
        danmakuData.state = DanmakuData.DanmakuState.HIDDEN;
        iterator.remove(); // 使用迭代器的remove方法来安全地移除元素
    }

    @Override
    public Window getWindow() {
        return this;
    }

    @Override
    public boolean isReady() {
        return ready;
    }

    @Override
    public void reSize() {

    }

    @Override
    public void onSeek() {

    }

    private class DanmakuPanel extends JPanel {
        private final Color bgColor = new Color(0, 0, 0, 0);
        private DanmakuPanel(){
            super();
            setBackground(bgColor);  // 设置背景透明
            loadDanmaku();
        }
        public void loadDanmaku(){
            if (owner.danmakuStr!=null)
                danmakuData.addAll(DanmakuData.getDanmakus(owner.danmakuStr));
            ready=true;
        }
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2d = null;
            if (g instanceof Graphics2D) {
                 g2d = ((Graphics2D) g);
            }

            assert g2d != null;
            g2d.setBackground(bgColor);
            g.clearRect(0,0,getWidth(),getHeight());

            Iterator<DanmakuData> iterator = onScreenDanmakuData.iterator();
            while (iterator.hasNext()) {
                DanmakuData onScreenDanmakuDatum = iterator.next();
                Color color = new Color(onScreenDanmakuDatum.fontColor);
                g.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),(int)(255*onScreenDanmakuDatum.opacity)));

                // 缩放文本
                double scale = onScreenDanmakuDatum.fontSize/25d*1.5; // 设置缩放比例
                g2d.scale(scale, scale);
                // 绘制缩放后的文本
                g.drawString(onScreenDanmakuDatum.text,
                        (int) (onScreenDanmakuDatum.x / scale),
                        (int) (onScreenDanmakuDatum.y / scale));

                // 恢复缩放
                g2d.scale(1 / scale, 1 / scale);

                moveDanmaku(onScreenDanmakuDatum,iterator);

            }
        }
    }
    public static class DanmakuData{
        public DanmakuState state;
        public int x;
        public int y;
        public double timestamp;
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
         * 显示时间
         */
        public int shownTime;
        public int fontSize;
        public int fontColor;
        public long timeInMilliseconds;
        public int danmakuPoolType;
        public String userID;
        public String danmakuID;
        public String content;
        public String text;
        public List<String> positionData;
        public List<String> getPositionData(){
            if (positionData!=null) return positionData;
            List<String> list = new ArrayList<>();
            if (!content.startsWith("[")||!content.endsWith("]")) throw new IllegalArgumentException("format err");
            content=content.substring(1,content.length()-1);
            Collections.addAll(list, content.split(","));
            positionData=list;
            return list;
        }
        public DanmakuData(){
            this.state=DanmakuState.HIDDEN;
        }
        public static List<DanmakuData> getDanmakus(String xmlData) {

            List<DanmakuData> danmakuDataList = null;
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
                    DanmakuData data = new DanmakuData();
                    data.danmakuID = danmakuID;
                    data.fontColor = Integer.parseInt(fontColor);
                    data.timestamp = Double.parseDouble(timestamp);
                    data.danmakuPoolType = Integer.parseInt(danmakuPoolType);
                    data.fontSize = Integer.parseInt(fontSize);
                    data.mode = Integer.parseInt(mode);
                    data.timeInMilliseconds = Long.parseLong(timeInMilliseconds);
                    data.userID = userID;
                    data.content= content;
                    danmakuDataList.add(data);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return danmakuDataList;
        }

        public enum DanmakuState{
            HIDDEN,
            SHOWN,
            PREPARED
        }
    }
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off

        //======== this ========
        var contentPane = getContentPane();
        contentPane.setLayout(new GridLayout());
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
