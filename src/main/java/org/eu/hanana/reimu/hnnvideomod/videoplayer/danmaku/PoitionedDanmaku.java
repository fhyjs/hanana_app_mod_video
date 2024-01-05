package org.eu.hanana.reimu.hnnvideomod.videoplayer.danmaku;

import org.eu.hanana.reimu.hnnvideomod.Utils;

import java.util.ArrayList;
import java.util.List;

public class PoitionedDanmaku extends Danmaku implements IProgressDanmaku{
    private float progress,duration,opacityFrom,opacityTo;
    private int type;//5:简单
    private final List<String> args=new ArrayList<>();

    @Override
    public void move(int maxWidth, int maxHeight) {
        x-= 2;
    }

    @Override
    public void show(int maxWidth, int maxHeight) {
        super.show(maxWidth, maxHeight);
        x=maxWidth;
        y=Utils.getRandomIntInRange(maxHeight,0);
        opacity=
    }

    @Override
    public void check(int width, int height) {
        if (x<0){
            state=DanmakuState.HIDDEN;
        }
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
