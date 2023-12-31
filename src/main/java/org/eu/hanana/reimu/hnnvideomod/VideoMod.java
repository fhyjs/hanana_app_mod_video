package org.eu.hanana.reimu.hnnvideomod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eu.hanana.reimu.hnnapp.ModLoader;
import org.eu.hanana.reimu.hnnapp.mods.Event;
import org.eu.hanana.reimu.hnnapp.mods.ModEntry;
import org.eu.hanana.reimu.hnnapp.mods.events.AppEvent;
import org.eu.hanana.reimu.hnnapp.mods.events.BrowserLoadEvent;

import java.util.HashMap;
import java.util.Map;

@ModEntry(id = VideoMod.MOD_ID, name = "视频播放器")
public class VideoMod {
    public final static String MOD_ID = "video_player";
    public final static Logger logger = LogManager.getLogger("VideoPlayer");
    public static final Map<String,VlcPlayer> PLAYER_MAP = new HashMap<>();
    public VideoMod(){
        logger.info("Load!");
        ModLoader.getLoader().regEventBuses(this);
    }
    @Event
    public void onLoadStartEvent(BrowserLoadEvent.Start event){
        for (VlcPlayer value : PLAYER_MAP.values()) {
            value.frame.dispose();
        }
        PLAYER_MAP.clear();
    }
    @Event
    public void onLoadEndEvent(BrowserLoadEvent.End event){
        event.cefFrame.executeJavaScript("loadJs(\"video://player/plugin.js\");",event.cefFrame.getURL(),0);
    }
    @Event
    public void onRegSchemes(AppEvent.RegSchemes event){
        event.schemesRegisterHandler.addScheme(
                VideoSchemeHandler.scheme,
                VideoSchemeHandler.domain,
                VideoSchemeHandler.class,
                true,
                false,
                false,
                true,
                true,
                true,
                true
        );
    }
}
