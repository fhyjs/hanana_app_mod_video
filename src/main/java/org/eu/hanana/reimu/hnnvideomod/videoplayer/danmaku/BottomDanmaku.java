package org.eu.hanana.reimu.hnnvideomod.videoplayer.danmaku;

import org.eu.hanana.reimu.hnnvideomod.Utils;

public class BottomDanmaku extends Danmaku{

    @Override
    public void move(int maxWidth, int maxHeight) {

    }

    @Override
    public void show(int maxWidth, int maxHeight) {
        super.show(maxWidth, maxHeight);
        x= (int) (maxWidth/2.3);
        y=Utils.getRandomIntInRange(maxHeight/2,0);
    }

    @Override
    public void check(int width, int height) {
        if (getShowtimeSec()>4){
            state=DanmakuState.HIDDEN;
        }
    }

    @Override
    public void init() {
        text=decodeText(content);
    }
}
