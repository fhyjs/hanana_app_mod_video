/*
 * Created by JFormDesigner on Sat Dec 23 00:27:05 CST 2023
 */

package org.eu.hanana.reimu.hnnvideomod.videoplayer;

import org.eu.hanana.reimu.hnnvideomod.Utils;
import org.eu.hanana.reimu.hnnvideomod.VideoDialog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author administer
 */
public class Danmaku extends JDialog {
    //private static final Logger logger = LogManager.getLogger("DanmakuPlayer");
    private final List<DanmakuData> danmakuData = new ArrayList<>();
    private final List<DanmakuData> onScreenDanmakuData = new ArrayList<>();
    private final VideoDialog owner;
    // 创建 Random 对象
    private final Random random = new Random();
    private DanmakuPanel danmakuPanel;
    public Danmaku(VideoDialog owner) {
        super(owner);
        this.owner=owner;
        setUndecorated(true);  // 设置无边框
        initComponents();
        setBackground(new Color(0, 0, 0, 0));  // 设置背景透明
        add(danmakuPanel=new DanmakuPanel());
    }

    public void timeChanged(long newTime) {
        for (DanmakuData danmakuDatum : danmakuData) {
            if (danmakuDatum.timestamp==newTime/1000f){
                danmakuDatum.state= DanmakuData.DanmakuState.SHOWN;
                onScreenDanmakuData.add(danmakuDatum);
                danmakuDatum.x=getWidth();
                danmakuDatum.y= random.nextInt(getHeight() + 1);
            }
        }
        danmakuPanel.repaint();
    }

    private class DanmakuPanel extends JPanel {
        private DanmakuPanel(){
            super();
            setBackground(new Color(0, 0, 0, 0));  // 设置背景透明
            loadDanmaku();
        }
        public void loadDanmaku(){
            danmakuData.addAll(DanmakuData.getDanmakus(Utils.getAssets(DanmakuData.class.getClassLoader(),"video/tst.xml")));
        }
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for (DanmakuData onScreenDanmakuDatum : onScreenDanmakuData) {
                g.setColor(new Color(onScreenDanmakuDatum.fontColor));
                g.drawString(onScreenDanmakuDatum.content,onScreenDanmakuDatum.x,onScreenDanmakuDatum.y);
                onScreenDanmakuDatum.x-= (int) (getWidth()*0.2);
                if (onScreenDanmakuDatum.y<0){
                    onScreenDanmakuDatum.state= DanmakuData.DanmakuState.HIDDEN;
                    onScreenDanmakuData.remove(onScreenDanmakuDatum);
                }
            }
        }
    }
    public static class DanmakuData{
        public DanmakuState state;
        public int x;
        public int y;
        public float timestamp;
        public int mode;
        public int fontSize;
        public int fontColor;
        public long timeInMilliseconds;
        public int danmakuPoolType;
        public String userID;
        public String danmakuID;
        public String content;

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
                    data.timestamp = Float.parseFloat(timestamp);
                    data.danmakuPoolType = Integer.parseInt(danmakuPoolType);
                    data.fontSize = Integer.parseInt(fontSize);
                    data.mode = Integer.parseInt(mode);
                    data.timeInMilliseconds = Long.parseLong(timeInMilliseconds);
                    data.userID = userID;
                    data.content=content;
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
        contentPane.setLayout(new GridLayout(1, 1));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
