package org.eu.hanana.reimu.hnnvideomod.videoplayer.danmaku;

import org.eu.hanana.reimu.hnnvideomod.Utils;

import java.util.ArrayList;
import java.util.List;

public class PoitionedDanmaku extends Danmaku implements IProgressDanmaku{
    private float progress,duration,opacityFrom,opacityTo;
    private int type;//5:简单
    private int xF,yF;
    private float xD,yD;
    int maxWidth;
    int maxHeight;
    private final List<String> args=new ArrayList<>();

    @Override
    public void move(int maxWidth, int maxHeight) {
        opacity=getDataByProgress(opacityFrom,opacityTo);
    }

    @Override
    public void show(int maxWidth, int maxHeight) {
        super.show(maxWidth, maxHeight);
        progress=0;
        this.maxHeight=maxHeight;
        this.maxWidth=maxWidth;
        x=getPosX(xD,yD);
        y=getPosY(xD,yD);
    }

    @Override
    public void check(int width, int height) {
        if (progress>1)
            state=DanmakuState.HIDDEN;
    }
    public int getPosX(float x,float y){
        if (x<1&&y<1){
            float scaledX = x * getScale();  // 考虑缩放
            float relativeX = scaledX * maxWidth;
            return (int) (relativeX / getScale());
        }
        return (int) x;
    }
    public int getPosY(float x,float y){
        if (x<1&&y<1){
            float scaledY = y * getScale();  // 考虑缩放
            float relativeY = (1 - scaledY) * maxHeight;
            return (int) (relativeY / getScale());
        }
        return (int) y;
    }
    @Override
    public void init() {
        args.clear();
        content=content.substring(1,content.length()-1);
        args.addAll(List.of(content.split(",")));
        type=args.size();
        text=decodeText(args.get(4));
        duration=Float.parseFloat(args.get(3));
        String opacityData = decodeText(args.get(2));
        opacityData=opacityData.substring(1,opacityData.length()-1);
        opacityFrom= Float.parseFloat(opacityData.split("-")[0]);
        opacityTo= Float.parseFloat(opacityData.split("-")[1]);
        xD= Float.parseFloat(args.get(0));
        yD= Float.parseFloat(args.get(1));
        if (mode>=6){
            rotateY= Float.parseFloat(args.get(5));
            rotateZ= Float.parseFloat(args.get(6));
        }
    }
    public float getDataByProgress(float f,float t){
        return f+(t-f)*progress;
    }
    @Override
    public float getEndTime() {
        return duration;
    }

    @Override
    public void setProgress(float v) {
        progress=v;
    }
}
