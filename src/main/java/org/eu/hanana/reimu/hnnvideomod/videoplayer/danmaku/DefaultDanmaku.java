package org.eu.hanana.reimu.hnnvideomod.videoplayer.danmaku;

import org.eu.hanana.reimu.hnnvideomod.Utils;

public class DefaultDanmaku extends Danmaku{

    @Override
    public void move(int maxWidth, int maxHeight) {
        x-= 2;
    }

    @Override
    public void show(int maxWidth, int maxHeight) {
        super.show(maxWidth, maxHeight);
        x=maxWidth;
        y=Utils.getRandomIntInRange(maxHeight,0);
    }

    @Override
    public void check(int width, int height) {
        if (x<0){
            state=DanmakuState.HIDDEN;
        }
    }

    @Override
    public void init() {
        text=decodeText(content);
    }
}
