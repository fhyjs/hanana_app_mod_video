package org.eu.hanana.reimu.hnnvideomod.videoplayer;

import java.awt.*;
import java.util.Iterator;

public interface IDanmaku {
    void timeChanged(long newTime);
    void initDanmaku(Danmaku.DanmakuData danmakuData) ;
    void moveDanmaku(Danmaku.DanmakuData danmakuData, Iterator<Danmaku.DanmakuData> iterator);
    void removeDanmaku(Danmaku.DanmakuData danmakuData, Iterator<Danmaku.DanmakuData> iterator);
    Window getWindow();
    boolean isReady();
    public void reSize();
    void enable();
    void disable();

    void onSeek();
}
